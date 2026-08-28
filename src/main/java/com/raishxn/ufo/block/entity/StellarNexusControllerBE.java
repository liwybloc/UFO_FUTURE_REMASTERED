package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.util.EntityDamageHelper;
import com.raishxn.ufo.api.multiblock.IMultiblockController;
import com.raishxn.ufo.api.multiblock.IMultiblockPart;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import com.raishxn.ufo.block.entity.pattern.StellarNexusPatternFactory;
import com.raishxn.ufo.block.StellarNexusControllerBlock;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.recipe.StellarSimulationRecipe;
import net.pedroksl.ae2addonlib.recipes.IngredientStack;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.blockentity.grid.AENetworkedBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Block Entity for the Stellar Nexus Controller.
 * <p>
 * Manages the multiblock structure, AE energy charging, fuel/coolant
 * extraction from the ME network, thermal system, and simulation processing.
 * <p>
 * <b>Terminology:</b>
 * <ul>
 *   <li><b>Energy</b> (energyBuffer) = AE power charged passively from the AE grid</li>
 *   <li><b>Fuel</b> = liquid combustible extracted from ME storage on start (e.g., Hydrogen)</li>
 *   <li><b>Coolant</b> = liquid refrigerant consumed during operation (e.g., Gelid Cryotheum)</li>
 * </ul>
 */
import com.raishxn.ufo.screen.StellarNexusControllerMenu;
import com.raishxn.ufo.block.MultiblockBlocks;

public final class StellarNexusControllerBE extends BlockEntity implements IMultiblockController, MenuProvider {

    private boolean assembled = false;
    private boolean structureDirty = true;
    private int scanCooldown = 0;
    private final List<BlockPos> parts = new ArrayList<>();

    private Identifier activeRecipeId = null;
    private int progress = 0;
    private int maxProgress = 0; // Cached total time
    private boolean running = false;
    private final ContainerData data;

    private long energyBuffer = 0;
    private static final long GLOBAL_ENERGY_CAPACITY = 200_000_000_000L; // 200 Billion AE global buffer
    private long energyCapacity = GLOBAL_ENERGY_CAPACITY;

    private int heatLevel = 0; // 0-1000 (displayed as 0.0% - 100.0%)
    private static final int MAX_HEAT = 1000;
    private boolean safeMode = true; // Default ON: auto-shutdown at 100% heat
    private int cooldownTimer = 0; // Ticks remaining for 30-min cooldown after overheat
    private static final int COOLDOWN_DURATION = 36000; // 30 minutes = 36000 ticks
    private static final int COOLDOWN_SAVE_INTERVAL = 20;

    private static final double SAFE_MODE_MULTIPLIER = 2.5;

    private boolean autoStart = false;
    private boolean simulationLocked = false;
    private boolean isOverclocked = false;

    private boolean exploding = false;
    private int explosionTick = 0;
    private int explosionRadius = 50;
    private int explosionShellRadius = 0;
    private int explosionCursorX = 0;
    private int explosionCursorY = 0;
    private int explosionCursorZ = 0;
    private static final int EXPLOSION_BLOCKS_PER_TICK = 4096;

    private int fieldLevel = 0;

    private static MultiblockPattern PATTERN;

    private static final long[] ENERGY_RATE_BY_TIER = { 0, 500_000, 1_000_000, 2_000_000 };

    public StellarNexusControllerBE(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(final int pIndex) {
                return switch (pIndex) {
                    case 0 -> StellarNexusControllerBE.this.progress;
                    case 1 -> StellarNexusControllerBE.this.maxProgress;
                    case 2 -> StellarNexusControllerBE.this.assembled ? 1 : 0;
                    case 3 -> StellarNexusControllerBE.this.fieldLevel;
                    case 4 -> StellarNexusControllerBE.this.energyCapacity > 0
                            ? (int) (StellarNexusControllerBE.this.energyBuffer * 100
                                    / StellarNexusControllerBE.this.energyCapacity)
                            : 0;
                    case 5 -> StellarNexusControllerBE.this.running ? 1 : 0;
                    case 6 -> StellarNexusControllerBE.this.heatLevel;
                    case 7 -> StellarNexusControllerBE.this.safeMode ? 1 : 0;
                    case 8 -> StellarNexusControllerBE.this.cooldownTimer;
                    case 9 -> (int) (StellarNexusControllerBE.this.energyBuffer & 0xFFFF);
                    case 10 -> (int) ((StellarNexusControllerBE.this.energyBuffer >> 16) & 0xFFFF);
                    case 11 -> (int) ((StellarNexusControllerBE.this.energyBuffer >> 32) & 0xFFFF);
                    case 12 -> (int) ((StellarNexusControllerBE.this.energyBuffer >> 48) & 0xFFFF);
                    case 13 -> (int) (GLOBAL_ENERGY_CAPACITY & 0xFFFF);
                    case 14 -> (int) ((GLOBAL_ENERGY_CAPACITY >> 16) & 0xFFFF);
                    case 15 -> (int) ((GLOBAL_ENERGY_CAPACITY >> 32) & 0xFFFF);
                    case 16 -> (int) ((GLOBAL_ENERGY_CAPACITY >> 48) & 0xFFFF);
                    case 17 -> StellarNexusControllerBE.this.autoStart ? 1 : 0;
                    case 18 -> StellarNexusControllerBE.this.simulationLocked ? 1 : 0;
                    case 19 -> StellarNexusControllerBE.this.isOverclocked ? 1 : 0;
                    default -> 0;
                };
            }

            @Override
            public void set(final int pIndex, final int pValue) {
                switch (pIndex) {
                    case 0 -> StellarNexusControllerBE.this.progress = pValue;
                    case 1 -> StellarNexusControllerBE.this.maxProgress = pValue;
                    case 2 -> StellarNexusControllerBE.this.assembled = pValue == 1;
                    case 3 -> StellarNexusControllerBE.this.fieldLevel = pValue;
                    case 5 -> StellarNexusControllerBE.this.running = pValue == 1;
                    case 6 -> StellarNexusControllerBE.this.heatLevel = pValue;
                    case 7 -> StellarNexusControllerBE.this.safeMode = pValue == 1;
                    case 8 -> StellarNexusControllerBE.this.cooldownTimer = pValue;
                    case 17 -> StellarNexusControllerBE.this.autoStart = pValue == 1;
                    case 18 -> StellarNexusControllerBE.this.simulationLocked = pValue == 1;
                    case 19 -> StellarNexusControllerBE.this.isOverclocked = pValue == 1;
                }
            }

            @Override
            public int getCount() {
                return 20;
            }
        };
    }


    @Override
    public boolean isAssembled() {
        return this.assembled;
    }

    @Override
    public void scanStructure(final Level level) {
        scanStructure(level, null);
    }

    public void scanStructure(final Level level, @Nullable final Player player) {
        final MultiblockPattern pattern = getPattern();
        final BlockState currentState = level.getBlockState(this.worldPosition);
        net.minecraft.core.Direction facing = net.minecraft.core.Direction.NORTH;
        if (currentState.hasProperty(net.minecraft.world.level.block.DirectionalBlock.FACING)) {
            facing = currentState.getValue(net.minecraft.world.level.block.DirectionalBlock.FACING);
        }
        final MultiblockPattern.MatchResult result = pattern.match(level, this.worldPosition, facing);
        final List<BlockPos> expectedE = pattern.getExpectedPositions(this.worldPosition, facing, 'E');
        boolean hasUnloadedFieldPositions = false;
        int tier1 = 0, tier2 = 0, tier3 = 0;
        for (final BlockPos ePos : expectedE) {
            if (!level.isLoaded(ePos)) {
                hasUnloadedFieldPositions = true;
                continue;
            }

            final Block block = level.getBlockState(ePos).getBlock();
            if (block == MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get()) {
                tier1++;
            } else if (block == MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get()) {
                tier2++;
            } else if (block == MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get()) {
                tier3++;
            }
        }

        final boolean waitingForChunks = result.hasUnloadedPositions() || hasUnloadedFieldPositions;
        if (waitingForChunks && player == null) {
            this.scanCooldown = 0;
            this.structureDirty = true;
            return;
        }

        final boolean wasAssembled = this.assembled;
        this.structureDirty = false;
        this.scanCooldown = 0;
        this.assembled = result.isValid();

        // Global Hatch Validation — no longer requires Fuel Hatch
        if (this.assembled) {
            int itemOutputs = 0;
            int fluidOutputs = 0;
            int itemInputs = 0;
            int energyInputs = 0;

            for (final BlockPos partPos : result.partPositions()) {
                final Block block = level.getBlockState(partPos).getBlock();
                if (block == MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())
                    itemOutputs++;
                else if (block == MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())
                    fluidOutputs++;
                else if (block == MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())
                    itemInputs++;
                else if (block == MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get())
                    energyInputs++;
            }

            if (itemOutputs != 1 || fluidOutputs != 1 || itemInputs != 1 || energyInputs != 1) {
                this.assembled = false;
            }
        }

        final int totalFound = tier1 + tier2 + tier3;
        final int targetFields = expectedE.size();

        if (this.assembled) {
            final int typesCount = (tier1 > 0 ? 1 : 0) + (tier2 > 0 ? 1 : 0) + (tier3 > 0 ? 1 : 0);

            if (typesCount > 1 || totalFound < targetFields) {
                this.assembled = false;
                this.fieldLevel = 0;
            } else if (typesCount == 1) {
                if (tier3 > 0) this.fieldLevel = 3;
                else if (tier2 > 0) this.fieldLevel = 2;
                else this.fieldLevel = 1;
            } else {
                this.fieldLevel = 0;
            }
        } else {
            this.fieldLevel = 0;
        }

        if (player != null && waitingForChunks) {
            player.sendSystemMessage(Component.literal("§e§l[STELLAR NEXUS] §7Ainda aguardando chunks da estrutura carregarem. Tente novamente em alguns segundos."));
        } else if (player != null && !this.assembled) {
            if (targetFields > 0 && (totalFound < targetFields || (tier1 > 0 && tier2 > 0) || (tier2 > 0 && tier3 > 0) || (tier1 > 0 && tier3 > 0))) {
                player.sendSystemMessage(Component.literal("§c§l[STELLAR NEXUS] §eIncomplete or Mixed Field Generators detected:"));
                
                if (tier1 > 0 || totalFound == 0) {
                    player.sendSystemMessage(Component.literal("  §7- Missing §c" + (targetFields - tier1) + "§7 blocks for §fTier 1§7 equivalence"));
                }
                if (tier2 > 0) {
                    player.sendSystemMessage(Component.literal("  §7- Missing §c" + (targetFields - tier2) + "§7 blocks for §bTier 2§7 equivalence"));
                }
                if (tier3 > 0) {
                    player.sendSystemMessage(Component.literal("  §7- Missing §c" + (targetFields - tier3) + "§7 blocks for §dTier 3§7 equivalence"));
                }
            } else {
                if (totalFound == targetFields) {
                     player.sendSystemMessage(Component.literal("§c§l[STELLAR NEXUS] §eStructure incomplete. Check casings, condensation matrix, or hatches."));
                } else {
                     player.sendSystemMessage(Component.literal("§c§l[STELLAR NEXUS] §eStructure match failed."));
                }
            }
        }

        this.parts.clear();
        if (this.assembled) {
            this.parts.addAll(result.partPositions());

            for (final BlockPos partPos : this.parts) {
                if (level.getBlockEntity(partPos) instanceof final IMultiblockPart part) {
                    part.linkToController(this.worldPosition);
                    if (part instanceof final MassiveOutputHatchBE hatch) {
                        hatch.refreshGridConnection();
                    }
                }
            }
        }

        if (wasAssembled != this.assembled) {
            final BlockState finalState = level.getBlockState(this.worldPosition);
            if (finalState.getBlock() instanceof StellarNexusControllerBlock) {
                level.setBlock(this.worldPosition,
                        finalState.setValue(StellarNexusControllerBlock.ASSEMBLED, this.assembled),
                        Block.UPDATE_CLIENTS);
            }
            this.setChanged();
        }
    }

    @Override
    public void addPart(final BlockPos partPos) {
        if (!this.parts.contains(partPos)) {
            this.parts.add(partPos);
        }
    }

    @Override
    public void removePart(final BlockPos partPos) {
        this.parts.remove(partPos);
        this.structureDirty = true;
    }

    @Override
    public List<BlockPos> getParts() {
        return Collections.unmodifiableList(this.parts);
    }

    @Override
    public BlockPos getControllerPos() {
        return this.worldPosition;
    }


    public void serverTick() {
        if (this.level == null || this.level.isClientSide())
            return;

        if (this.exploding) {
            processExplosionTick();
            return;
        }

        if (this.structureDirty) {
            this.scanCooldown++;
            if (this.scanCooldown >= 20) {
                scanStructure(this.level);
            }
        }


        if (this.cooldownTimer > 0) {
            this.cooldownTimer--;
            if (this.cooldownTimer == 0) {
                this.heatLevel = 0;
                this.setChanged();
            } else if (this.cooldownTimer % COOLDOWN_SAVE_INTERVAL == 0) {
                this.setChanged();
            }
            return;
        }

        if (this.assembled) {
            processMachineTick();

            if (!this.running && this.activeRecipeId != null && this.cooldownTimer == 0) {
                if (this.autoStart) {
                    startOperation();
                }
            }
        } else {
            this.progress = 0;
            this.running = false;
        }
    }


    public boolean isActive() {
        return this.running;
    }

    /**
     * Validates all requirements for starting a simulation and returns a list
     * of error messages. An empty list means the simulation can start.
     */
    public List<Component> getStartErrors() {
        final List<Component> errors = new ArrayList<>();

        if (!this.assembled) {
            errors.add(Component.literal("§c✗ Structure not assembled"));
        }
        if (this.running) {
            errors.add(Component.literal("§c✗ Already in operation"));
        }
        if (this.cooldownTimer > 0) {
            final int secLeft = this.cooldownTimer / 20;
            errors.add(Component.literal("§c✗ Cooling down: " + secLeft + "s remaining"));
        }
        if (this.activeRecipeId == null) {
            errors.add(Component.literal("§c✗ No simulation program selected"));
            return errors;
        }

        if (this.level == null)
            return errors;

        final var recipeOpt = getActiveRecipe();
        if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof final StellarSimulationRecipe recipe)) {
            errors.add(Component.literal("§c✗ Invalid simulation program"));
            return errors;
        }

        if (this.fieldLevel < recipe.getFieldTier()) {
            errors.add(Component.literal("§c✗ Field Generator too low: Mk." + toRoman(this.fieldLevel) + " (need Mk."
                    + toRoman(recipe.getFieldTier()) + ")"));
        }

        double multiplier = this.safeMode ? SAFE_MODE_MULTIPLIER : 1.0;
        if (this.isOverclocked) multiplier *= 10.0;
        final long effectiveEnergyCost = (long) (recipe.getEnergyCost() * multiplier);

        double fuelMultiplier = this.safeMode ? SAFE_MODE_MULTIPLIER : 1.0;
        if (this.isOverclocked) fuelMultiplier *= 5.0;
        final long effectiveFuelAmount = (long) (recipe.getFuelAmount() * fuelMultiplier);

        if (this.energyBuffer < effectiveEnergyCost) {
            final int pct = this.energyCapacity > 0 ? (int) (this.energyBuffer * 100 / this.energyCapacity) : 0;
            final String safeNote = this.safeMode ? " §7(2.5x Safe Mode)" : "";
            errors.add(Component.literal("§c✗ Energy: " + formatAmount(this.energyBuffer) + " / "
                    + formatAmount(effectiveEnergyCost) + " AE" + safeNote));
        }

        if (!recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
            final AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
            if (nodeBE == null || nodeBE.getActionableNode() == null || nodeBE.getActionableNode().getGrid() == null) {
                errors.add(Component.literal("§c✗ No ME network connection"));
            } else {
                final Identifier fuelRL = Identifier.parse(recipe.getFuelFluid());
                final Fluid fuelFluid = BuiltInRegistries.FLUID.getValue(fuelRL);
                if (fuelFluid == null || fuelFluid == net.minecraft.world.level.material.Fluids.EMPTY) {
                    errors.add(Component.literal("§c✗ Invalid fuel fluid type: " + fuelRL));
                } else {
                    final AEFluidKey fuelKey = AEFluidKey.of(fuelFluid);
                    final MEStorage storage = nodeBE.getActionableNode().getGrid().getStorageService().getInventory();
                    final IActionSource src = IActionSource.ofMachine(nodeBE);
                    final long available = storage.extract(fuelKey, effectiveFuelAmount, Actionable.SIMULATE, src);
                    if (available < effectiveFuelAmount) {
                        final String fluidName = formatFluidName(fuelRL.getPath());
                        final String safeNote = this.safeMode ? " §7(2.5x Safe Mode)" : "";
                        errors.add(Component.literal("§c✗ Fuel: " + formatAmount(available) + " / "
                                + formatAmount(effectiveFuelAmount) + " mB §f" + fluidName + safeNote));
                    }
                }
            }
        }

        final AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        if (nodeBE != null && nodeBE.getActionableNode() != null && nodeBE.getActionableNode().getGrid() != null) {
            final MEStorage storage = nodeBE.getActionableNode().getGrid().getStorageService().getInventory();
            final IActionSource src = IActionSource.ofMachine(nodeBE);

            for (final var req : recipe.getItemInputs()) {
                if (!req.isEmpty()) {
                    final long available = simulateExtractItem(req, storage, src);
                    if (available < req.getAmount()) {
                        String itemName = "Unknown Item";
                        final List<ItemStack> matches = req.getIngredient().getValues().stream().map(holder -> new ItemStack(holder.value())).toList();
                        if (!matches.isEmpty()) {
                            itemName = matches.getFirst().getHoverName().getString();
                        }
                        errors.add(Component.literal("§c✗ Missing: " + formatAmount(available) + " / "
                                + formatAmount(req.getAmount()) + "x §f" + itemName));
                    }
                }
            }
            for (final var req : recipe.getFluidInputs()) {
                if (!req.isEmpty()) {
                    final long available = simulateExtractFluid(req, storage, src);
                    if (available < req.getAmount()) {
                        String fluidName = "Unknown Fluid";
                        final var fluidStacks = req.getIngredient().fluids();
                        if (!fluidStacks.isEmpty()) {
                            final Identifier fluidRL = BuiltInRegistries.FLUID.getKey(fluidStacks.getFirst().value());
                            fluidName = formatFluidName(fluidRL.getPath());
                        }
                        errors.add(Component.literal("§c✗ Missing: " + formatAmount(available) + " / "
                                + formatAmount(req.getAmount()) + " mB §f" + fluidName));
                    }
                }
            }
        }

        return errors;
    }

    /**
     * Called from the network packet when the player clicks "Start Operation".
     * Returns a list of error messages (empty = success).
     */
    public List<Component> startOperation() {
        final List<Component> errors = getStartErrors();
        if (!errors.isEmpty())
            return errors;

        if (this.level == null || this.level.isClientSide())
            return List.of(Component.literal("§c✗ Internal error"));

        final var recipeOpt = getActiveRecipe();
        if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof final StellarSimulationRecipe recipe)) {
            return List.of(Component.literal("§c✗ Invalid recipe"));
        }

        final AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        if (nodeBE == null || nodeBE.getActionableNode() == null)
            return List.of(Component.literal("§c✗ No network"));
        final IGridNode node = nodeBE.getActionableNode();
        if (node.getGrid() == null)
            return List.of(Component.literal("§c✗ No grid"));

        final IGrid grid = node.getGrid();
        final IActionSource src = IActionSource.ofMachine(nodeBE);
        final MEStorage storage = grid.getStorageService().getInventory();

        double multiplier = this.safeMode ? SAFE_MODE_MULTIPLIER : 1.0;
        if (this.isOverclocked) multiplier *= 10.0;
        final long effectiveEnergyCost = (long) (recipe.getEnergyCost() * multiplier);

        double fuelMultiplier = this.safeMode ? SAFE_MODE_MULTIPLIER : 1.0;
        if (this.isOverclocked) fuelMultiplier *= 5.0;
        final long effectiveFuelAmount = (long) (recipe.getFuelAmount() * fuelMultiplier);

        final ResourceReservation reservation = reserveStartResources(recipe, storage, src, effectiveFuelAmount);
        if (reservation == null) {
            return List.of(Component.literal("§c✗ Failed to extract inputs"));
        }
        extractReservation(reservation, storage, src);

        this.energyBuffer -= effectiveEnergyCost;
        this.maxProgress = recipe.getTime();
        this.progress = 0;
        this.running = true;
        this.setChanged();
        if (this.level != null) {
            final BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }
        return List.of(); // Success
    }

    public void toggleSafeMode() {
        if (!this.running) {
            this.safeMode = !this.safeMode;
            this.setChanged();
        }
    }

    public void toggleAutoStart() {
        this.autoStart = !this.autoStart;
        this.setChanged();
    }

    public void toggleSimulationLock() {
        this.simulationLocked = !this.simulationLocked;
        this.setChanged();
    }

    public void toggleOverclock() {
        if (!this.running) {
            this.isOverclocked = !this.isOverclocked;
            this.setChanged();
        }
    }

    private void processMachineTick() {
        boolean changed = false;
        if (this.energyCapacity != GLOBAL_ENERGY_CAPACITY) {
            this.energyCapacity = GLOBAL_ENERGY_CAPACITY;
            changed = true;
        }

        final AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        changed |= chargeEnergyFromNetwork(nodeBE);

        if (this.activeRecipeId == null) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        final var recipeOpt = getActiveRecipe();
        if (recipeOpt.isEmpty() || !(recipeOpt.get().value() instanceof final StellarSimulationRecipe recipe)) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        this.maxProgress = recipe.getTime();
        if (!this.running) {
            if (changed) {
                this.setChanged();
            }
            return;
        }

        if (nodeBE == null || nodeBE.getActionableNode() == null)
            return;
        final IGridNode node = nodeBE.getActionableNode();
        if (node.getGrid() == null)
            return;

        final IGrid grid = node.getGrid();
        final IActionSource src = IActionSource.ofMachine(nodeBE);
        final MEStorage storage = grid.getStorageService().getInventory();

        int heatPerTick = recipe.getCoolingLevel() + 1;
        if (this.isOverclocked) heatPerTick *= 5;
        this.heatLevel = Math.min(MAX_HEAT, this.heatLevel + heatPerTick);

        final int coolingApplied = consumeCoolant(recipe, grid);
        this.heatLevel = Math.max(0, this.heatLevel - coolingApplied);

        if (this.heatLevel >= MAX_HEAT) {
            if (this.safeMode) {
                this.running = false;
                this.progress = 0;
                this.cooldownTimer = this.isOverclocked ? 144000 : COOLDOWN_DURATION;
                this.setChanged();
                if (this.level != null) {
                    final BlockPos pos = this.worldPosition;
                    final int cooldownMinutes = this.cooldownTimer / 1200;
                    this.level.players().forEach(p -> p.sendSystemMessage(
                            Component.literal("§c§l[STELLAR NEXUS] §eSafe Mode activated at " + pos.toShortString()
                                    + " - " + cooldownMinutes + " minute cooldown initiated.")));

                    final BlockState state = this.level.getBlockState(this.worldPosition);
                    this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
                }
                return;
            } else {
                triggerStellarExplosion();
                return;
            }
        }

        this.progress += this.isOverclocked ? 5 : 1;
        if (this.progress >= recipe.getTime()) {
            injectOutputs(recipe, storage, src);
            this.progress = 0;
            this.running = false;

            if (this.level != null) {
                final BlockState state = this.level.getBlockState(this.worldPosition);
                this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            }
        }
        this.setChanged();
    }

    private boolean chargeEnergyFromNetwork(@Nullable final AENetworkedBlockEntity nodeBE) {
        if (nodeBE == null || nodeBE.getActionableNode() == null || this.fieldLevel < 1 || this.fieldLevel > 3) {
            return false;
        }

        final IGridNode node = nodeBE.getActionableNode();
        if (node.getGrid() == null) {
            return false;
        }

        final long spaceLeft = this.energyCapacity - this.energyBuffer;
        if (spaceLeft <= 0L) {
            return false;
        }

        final long toCharge = Math.min(ENERGY_RATE_BY_TIER[this.fieldLevel], spaceLeft);
        if (toCharge <= 0L) {
            return false;
        }

        final IEnergyService energy = node.getGrid().getEnergyService();
        final double extracted = energy.extractAEPower(toCharge, Actionable.MODULATE, PowerMultiplier.CONFIG);
        final long accepted = Math.min(spaceLeft, (long) extracted);
        if (accepted <= 0L) {
            return false;
        }

        this.energyBuffer += accepted;
        return true;
    }

    private void triggerStellarExplosion() {
        if (this.level == null)
            return;
        final BlockPos pos = this.worldPosition;

        this.explosionRadius = this.fieldLevel == 3 ? 100 : (this.fieldLevel == 2 ? 50 : 30);

        this.level.players().forEach(p -> p.sendSystemMessage(
                Component.literal("§4§l[STELLAR NEXUS] §c§lCRITICAL THERMAL FAILURE at " + pos.toShortString()
                        + "! CATASTROPHIC EXPLOSION!")));

        this.level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                Math.max(12.0f, this.explosionRadius * 0.18f), Level.ExplosionInteraction.BLOCK);

        this.exploding = true;
        this.explosionTick = 0;
        this.explosionShellRadius = 0;
        resetExplosionCursor();

        this.running = false;
        this.progress = 0;
        this.heatLevel = 0;
        this.energyBuffer = 0;
        this.assembled = false;

        if (this.level != null) {
            final BlockState state = this.level.getBlockState(this.worldPosition);
            this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
        }

        onControllerBroken();
    }

    private void processExplosionTick() {
        if (this.level == null) {
            this.exploding = false;
            return;
        }

        this.explosionTick++;
        int processed = 0;
        while (processed < EXPLOSION_BLOCKS_PER_TICK && this.exploding) {
            if (this.explosionShellRadius > this.explosionRadius) {
                finishExplosionWave();
                break;
            }

            final int radius = this.explosionShellRadius;
            final int stepResult = processExplosionCursor(radius);
            if (stepResult > 0) {
                processed++;
            } else if (stepResult == 0) {
                damageEntitiesForShell(radius);
                spawnExplosionPulse(radius);
                this.explosionShellRadius++;
                resetExplosionCursor();
            }
        }

        this.setChanged();
    }

    private int processExplosionCursor(final int radius) {
        if (radius == 0) {
            processExplosionBlock(this.worldPosition, 0);
            return 0;
        }

        if (this.explosionCursorY > radius) {
            return 0;
        }

        final int dx = this.explosionCursorX;
        final int dy = this.explosionCursorY;
        final int dz = this.explosionCursorZ;

        advanceExplosionCursor(radius);

        final int distSq = dx * dx + dy * dy + dz * dz;
        final int outerSq = radius * radius;
        final int innerSq = (radius - 1) * (radius - 1);
        if (distSq > outerSq || distSq <= innerSq) {
            return -1;
        }

        processExplosionBlock(this.worldPosition.offset(dx, dy, dz), radius);
        return 1;
    }

    private void advanceExplosionCursor(final int radius) {
        this.explosionCursorX++;
        if (this.explosionCursorX > radius) {
            this.explosionCursorX = -radius;
            this.explosionCursorZ++;
            if (this.explosionCursorZ > radius) {
                this.explosionCursorZ = -radius;
                this.explosionCursorY++;
            }
        }
    }

    private void resetExplosionCursor() {
        this.explosionCursorX = -this.explosionShellRadius;
        this.explosionCursorY = -this.explosionShellRadius;
        this.explosionCursorZ = -this.explosionShellRadius;
    }

    private void processExplosionBlock(final BlockPos target, final int radius) {
        if (this.level == null || !this.level.isLoaded(target) || !this.level.isInWorldBounds(target)) {
            return;
        }

        final BlockState targetState = this.level.getBlockState(target);
        if (targetState.isAir() || targetState.getDestroySpeed(this.level, target) < 0) {
            return;
        }

        final int lavaRadius = Math.max(3, (int) (this.explosionRadius * 0.28));
        if (radius <= lavaRadius) {
            this.level.setBlock(target, net.minecraft.world.level.block.Blocks.LAVA.defaultBlockState(), Block.UPDATE_ALL);
            return;
        }

        this.level.setBlock(target, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);

        final BlockPos above = target.above();
        if (this.level.isLoaded(above) && this.level.getBlockState(above).isAir()) {
            this.level.setBlock(above, net.minecraft.world.level.block.Blocks.FIRE.defaultBlockState(), Block.UPDATE_ALL);
        }
    }

    private void damageEntitiesForShell(final int radius) {
        if (this.level == null || radius <= 0) {
            return;
        }

        final double shell = Math.min(radius + 2.0, this.explosionRadius);
        final var entities = this.level.getEntitiesOfClass(
                net.minecraft.world.entity.LivingEntity.class,
                new net.minecraft.world.phys.AABB(
                        this.worldPosition.getX() - shell, this.worldPosition.getY() - shell, this.worldPosition.getZ() - shell,
                        this.worldPosition.getX() + shell, this.worldPosition.getY() + shell, this.worldPosition.getZ() + shell));

        for (final var entity : entities) {
            final double dist = Math.sqrt(entity.distanceToSqr(this.worldPosition.getCenter()));
            if (dist > shell) {
                continue;
            }

            final float damage = (float) Math.max(6.0, (this.explosionRadius - dist) * 1.8);
            EntityDamageHelper.hurt(entity, this.level.damageSources().explosion(null), damage);
            entity.setRemainingFireTicks(Math.max(entity.getRemainingFireTicks(), 200));
        }
    }

    private void spawnExplosionPulse(final int radius) {
        if (this.level == null || radius <= 0) {
            return;
        }

        if (radius == 1 || radius == this.explosionRadius || radius % 4 == 0) {
            final var random = this.level.getRandom();
            final double offsetScale = Math.max(2.0, radius * 0.35);
            final double ox = this.worldPosition.getX() + 0.5 + (random.nextDouble() - 0.5) * offsetScale;
            final double oy = this.worldPosition.getY() + 0.5 + (random.nextDouble() - 0.5) * offsetScale;
            final double oz = this.worldPosition.getZ() + 0.5 + (random.nextDouble() - 0.5) * offsetScale;
            final float power = Math.min(18.0f, 4.0f + radius * 0.12f);
            this.level.explode(null, ox, oy, oz, power, Level.ExplosionInteraction.BLOCK);
        }
    }

    private void finishExplosionWave() {
        this.exploding = false;
        this.explosionTick = 0;
        this.explosionShellRadius = 0;
        resetExplosionCursor();
        removeControllerBlockAfterExplosion();
    }

    private void removeControllerBlockAfterExplosion() {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return;
        }

        this.level.removeBlock(this.worldPosition, false);
    }

    private AENetworkedBlockEntity getConnectedNetworkNode() {
        if (this.level == null)
            return null;
        AENetworkedBlockEntity fallback = null;

        for (final BlockPos p : this.parts) {
            if (!(this.level.getBlockEntity(p) instanceof final AENetworkedBlockEntity nodeBE)) {
                continue;
            }
            if (nodeBE.getActionableNode() == null || nodeBE.getActionableNode().getGrid() == null) {
                continue;
            }

            final BlockState state = this.level.getBlockState(p);
            if (state.is(MultiblockBlocks.ME_MASSIVE_INPUT_HATCH.get())) {
                return nodeBE;
            }
            if (fallback == null && state.is(MultiblockBlocks.ME_MASSIVE_FLUID_HATCH.get())) {
                fallback = nodeBE;
                continue;
            }
            if (fallback == null && state.is(MultiblockBlocks.AE_ENERGY_INPUT_HATCH.get())) {
                fallback = nodeBE;
                continue;
            }
            if (fallback == null && state.is(MultiblockBlocks.ME_MASSIVE_OUTPUT_HATCH.get())) {
                fallback = nodeBE;
            }
        }

        return fallback;
    }




    /** Amount of coolant fluid (mB) consumed per tick during active operation. */
    private static final long COOLANT_CONSUMPTION_PER_TICK = 100;

    /**
     * Tries to extract the recipe's specific coolant fluid from the ME network.
     * Returns the cooling power applied this tick (heat units to subtract).
     * <p>
     * Prioritizes the intended coolant ladder without falling back to water.
     * <p>
     * Coolant effectiveness per tier:
     * <ul>
     * <li>Gelid Cryotheum (T1): 1 cooling/mB</li>
     * <li>Stable Coolant (T2): 4 cooling/mB</li>
     * <li>Temporal Fluid (T3): 8 cooling/mB</li>
     * </ul>
     */
    private int consumeCoolant(final StellarSimulationRecipe recipe, final IGrid grid) {
        final AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        if (nodeBE == null)
            return 0;

        final IActionSource src = IActionSource.ofMachine(nodeBE);
        final MEStorage storage = grid.getStorageService().getInventory();

        final AEFluidKey t3 = AEFluidKey.of(BuiltInRegistries.FLUID.getValue(Identifier.parse("ufo:source_temporal_fluid")));
        final AEFluidKey t2 = AEFluidKey.of(BuiltInRegistries.FLUID.getValue(Identifier.parse("ufo:source_stable_coolant")));
        final AEFluidKey t1 = AEFluidKey.of(BuiltInRegistries.FLUID.getValue(Identifier.parse("ufo:source_gelid_cryotheum")));
        final AEFluidKey[] toTry;
        if (this.fieldLevel == 3) {
            toTry = new AEFluidKey[]{t3, t2, t1};
        } else if (this.fieldLevel == 2) {
            toTry = new AEFluidKey[]{t2, t3, t1};
        } else if (this.fieldLevel == 1) {
            toTry = new AEFluidKey[]{t1, t2, t3};
        } else {
            toTry = new AEFluidKey[0];
        }

        double multiplier = this.safeMode ? SAFE_MODE_MULTIPLIER : 1.0;
        if (this.isOverclocked) multiplier *= 5.0;
        final long effectiveCoolantPerTick = (long) (COOLANT_CONSUMPTION_PER_TICK * multiplier);

        for (final AEFluidKey coolantKey : toTry) {
            if (coolantKey == null || coolantKey.getFluid() == net.minecraft.world.level.material.Fluids.EMPTY) continue;
            final long extracted = storage.extract(coolantKey, effectiveCoolantPerTick, Actionable.MODULATE, src);
            if (extracted > 0) {
                final int efficiency = getCoolantEfficiency(coolantKey.getFluid());

                return (int) (extracted * efficiency / effectiveCoolantPerTick) * (coolingTierBonus() + 1);
            }
        }

        return 0; // No coolant available — heat will continue to rise!
    }

    /**
     * Bonus cooling multiplier based on field generator tier.
     */
    private int coolingTierBonus() {
        return this.fieldLevel;
    }

    private int getCoolantEfficiency(final Fluid fluid) {
        if (fluid == ModFluids.SOURCE_TEMPORAL_FLUID.get() || fluid == ModFluids.FLOWING_TEMPORAL_FLUID.get()) {
            return 8;
        }
        if (fluid == ModFluids.SOURCE_STABLE_COOLANT.get() || fluid == ModFluids.FLOWING_STABLE_COOLANT.get()) {
            return 4;
        }
        return 1;
    }

    private Optional<RecipeHolder<?>> getActiveRecipe() {
        if (this.activeRecipeId == null || !(this.level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return serverLevel.recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, this.activeRecipeId));
    }

    public Identifier getActiveRecipeId() {
        return this.activeRecipeId;
    }

    public void setActiveRecipe(final Identifier activeRecipeId) {
        this.activeRecipeId = activeRecipeId;
        this.progress = 0;
        this.setChanged();

        if (this.level != null) {
            final BlockState state = this.level.getBlockState(this.getBlockPos());
            this.level.sendBlockUpdated(this.getBlockPos(), state, state, 3);
        }
    }


    private boolean extractInputs(final StellarSimulationRecipe recipe, final MEStorage storage, final IActionSource src) {
        for (final var req : recipe.getItemInputs()) {
            if (!req.isEmpty() && simulateExtractItem(req, storage, src) < req.getAmount())
                return false;
        }
        for (final var req : recipe.getFluidInputs()) {
            if (!req.isEmpty() && simulateExtractFluid(req, storage, src) < req.getAmount())
                return false;
        }

        for (final var req : recipe.getItemInputs()) {
            if (!req.isEmpty())
                modulateExtractItem(req, storage, src);
        }
        for (final var req : recipe.getFluidInputs()) {
            if (!req.isEmpty())
                modulateExtractFluid(req, storage, src);
        }
        return true;
    }

    @Nullable
    private ResourceReservation reserveStartResources(final StellarSimulationRecipe recipe, final MEStorage storage, final IActionSource src, final long effectiveFuelAmount) {
        final Map<AEItemKey, Long> itemReservations = new HashMap<>();
        final Map<AEFluidKey, Long> fluidReservations = new HashMap<>();

        if (!recipe.getFuelFluid().isEmpty() && recipe.getFuelAmount() > 0) {
            final Identifier fuelRL = Identifier.parse(recipe.getFuelFluid());
            final Fluid fuelFluid = BuiltInRegistries.FLUID.getValue(fuelRL);
            if (fuelFluid == null || fuelFluid == net.minecraft.world.level.material.Fluids.EMPTY) {
                return null;
            }
            if (!reserveFluid(AEFluidKey.of(fuelFluid), effectiveFuelAmount, fluidReservations, storage, src)) {
                return null;
            }
        }

        for (final var req : recipe.getItemInputs()) {
            if (!req.isEmpty() && !reserveItem(req, itemReservations, storage, src)) {
                return null;
            }
        }
        for (final var req : recipe.getFluidInputs()) {
            if (!req.isEmpty() && !reserveFluid(req, fluidReservations, storage, src)) {
                return null;
            }
        }

        return new ResourceReservation(itemReservations, fluidReservations);
    }

    private boolean reserveItem(final IngredientStack.Item req, final Map<AEItemKey, Long> reservations, final MEStorage storage, final IActionSource src) {
        final long amount = req.getAmount();
        for (final ItemStack match : req.getIngredient().getValues().stream().map(holder -> new ItemStack(holder.value())).toList()) {
            final AEItemKey key = AEItemKey.of(match);
            final long reserved = reservations.getOrDefault(key, 0L);
            final long neededWithReservation = saturatedAdd(reserved, amount);
            final long available = storage.extract(key, neededWithReservation, Actionable.SIMULATE, src);
            if (available >= neededWithReservation) {
                reservations.put(key, neededWithReservation);
                return true;
            }
        }
        return false;
    }

    private boolean reserveFluid(final IngredientStack.Fluid req, final Map<AEFluidKey, Long> reservations, final MEStorage storage, final IActionSource src) {
        final long amount = req.getAmount();
        for (final FluidStack match : req.getIngredient().fluids().stream().map(holder -> new FluidStack(holder.value(), 1)).toList()) {
            if (reserveFluid(AEFluidKey.of(match.getFluid()), amount, reservations, storage, src)) {
                return true;
            }
        }
        return false;
    }

    private boolean reserveFluid(final AEFluidKey key, final long amount, final Map<AEFluidKey, Long> reservations, final MEStorage storage, final IActionSource src) {
        if (amount <= 0L) {
            return true;
        }
        final long reserved = reservations.getOrDefault(key, 0L);
        final long neededWithReservation = saturatedAdd(reserved, amount);
        final long available = storage.extract(key, neededWithReservation, Actionable.SIMULATE, src);
        if (available < neededWithReservation) {
            return false;
        }
        reservations.put(key, neededWithReservation);
        return true;
    }

    private void extractReservation(final ResourceReservation reservation, final MEStorage storage, final IActionSource src) {
        for (final var entry : reservation.itemReservations().entrySet()) {
            storage.extract(entry.getKey(), entry.getValue(), Actionable.MODULATE, src);
        }
        for (final var entry : reservation.fluidReservations().entrySet()) {
            storage.extract(entry.getKey(), entry.getValue(), Actionable.MODULATE, src);
        }
    }

    private long saturatedAdd(final long a, final long b) {
        if (b > 0L && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }
        return a + b;
    }

    private long simulateExtractItem(final IngredientStack.Item req, final MEStorage storage, final IActionSource src) {
        long extracted = 0;
        long needed = req.getAmount();
        for (final ItemStack match : req.getIngredient().getValues().stream().map(holder -> new ItemStack(holder.value())).toList()) {
            final long ext = storage.extract(AEItemKey.of(match), needed, Actionable.SIMULATE, src);
            extracted += ext;
            needed -= ext;
            if (needed <= 0)
                break;
        }
        return extracted;
    }

    private void modulateExtractItem(final IngredientStack.Item req, final MEStorage storage, final IActionSource src) {
        long needed = req.getAmount();
        for (final ItemStack match : req.getIngredient().getValues().stream().map(holder -> new ItemStack(holder.value())).toList()) {
            final long ext = storage.extract(AEItemKey.of(match), needed, Actionable.MODULATE, src);
            needed -= ext;
            if (needed <= 0)
                break;
        }
    }

    private long simulateExtractFluid(final IngredientStack.Fluid req, final MEStorage storage, final IActionSource src) {
        long extracted = 0;
        long needed = req.getAmount();
        for (final FluidStack match : req.getIngredient().fluids().stream().map(holder -> new FluidStack(holder.value(), 1)).toList()) {
            final long ext = storage.extract(AEFluidKey.of(match.getFluid()), needed, Actionable.SIMULATE, src);
            extracted += ext;
            needed -= ext;
            if (needed <= 0)
                break;
        }
        return extracted;
    }

    private void modulateExtractFluid(final IngredientStack.Fluid req, final MEStorage storage, final IActionSource src) {
        long needed = req.getAmount();
        for (final FluidStack match : req.getIngredient().fluids().stream().map(holder -> new FluidStack(holder.value(), 1)).toList()) {
            final long ext = storage.extract(AEFluidKey.of(match.getFluid()), needed, Actionable.MODULATE, src);
            needed -= ext;
            if (needed <= 0)
                break;
        }
    }

    private void injectOutputs(final StellarSimulationRecipe recipe, final MEStorage storage, final IActionSource src) {
        for (final GenericStack out : recipe.getItemOutputs()) {
            storage.insert(out.what(), out.amount(), Actionable.MODULATE, src);
        }
        for (final GenericStack out : recipe.getFluidOutputs()) {
            storage.insert(out.what(), out.amount(), Actionable.MODULATE, src);
        }
    }

    private record ResourceReservation(Map<AEItemKey, Long> itemReservations, Map<AEFluidKey, Long> fluidReservations) {
    }

    public void markStructureDirty() {
        this.structureDirty = true;
        this.scanCooldown = 0;
    }

    public void onControllerBroken() {
        if (this.level == null)
            return;
        for (final BlockPos partPos : this.parts) {
            if (this.level.getBlockEntity(partPos) instanceof final IMultiblockPart part) {
                part.unlinkFromController();
            }
        }
        this.parts.clear();
        this.assembled = false;
    }


    private static String formatAmount(final long amount) {
        if (amount >= 1_000_000_000)
            return String.format("%.1fB", amount / 1_000_000_000.0);
        if (amount >= 1_000_000)
            return String.format("%.1fM", amount / 1_000_000.0);
        if (amount >= 1_000)
            return String.format("%.1fK", amount / 1_000.0);
        return String.valueOf(amount);
    }

    private static String toRoman(final int tier) {
        return switch (tier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            default -> String.valueOf(tier);
        };
    }

    /**
     * Converts a fluid registry path to a human-readable name.
     * e.g., "source_gelid_cryotheum" → "Gelid Cryotheum"
     */
    private static String formatFluidName(String path) {
        if (path.startsWith("source_"))
            path = path.substring(7);
        if (path.startsWith("flowing_"))
            path = path.substring(8);
        final String[] words = path.split("_");
        final StringBuilder sb = new StringBuilder();
        for (final String word : words) {
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }


    private static MultiblockPattern getPattern() {
        if (PATTERN == null) {
            PATTERN = StellarNexusPatternFactory.getPattern();
        }
        return PATTERN;
    }


    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("block.ufo.stellar_nexus_controller");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(final int id, @NotNull final Inventory playerInventory, @NotNull final Player playerEntity) {
        return new StellarNexusControllerMenu(id, playerInventory, this, this.data);
    }


    @Override
    protected void saveAdditional(@NotNull final ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putBoolean("running", this.running);
        output.putBoolean("safeMode", this.safeMode);
        output.putInt("progress", this.progress);
        output.putInt("heatLevel", this.heatLevel);
        output.putInt("cooldownTimer", this.cooldownTimer);
        output.putLong("energyBuffer", this.energyBuffer);
        output.putLong("energyCapacity", this.energyCapacity);
        output.putBoolean("autoStart", this.autoStart);
        output.putBoolean("simulationLocked", this.simulationLocked);

        if (this.activeRecipeId != null) {
            output.store("activeRecipeId", Identifier.CODEC, this.activeRecipeId);
        }
        final var partsList = output.list("parts", BlockPos.CODEC);
        this.parts.forEach(partsList::add);
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.running = input.getBooleanOr("running", false);
        this.safeMode = input.getBooleanOr("safeMode", true);
        this.progress = input.getIntOr("progress", 0);
        this.heatLevel = input.getIntOr("heatLevel", 0);
        this.cooldownTimer = input.getIntOr("cooldownTimer", 0);
        this.autoStart = input.getBooleanOr("autoStart", false);
        this.simulationLocked = input.getBooleanOr("simulationLocked", false);
        this.energyBuffer = input.getLong("energyBuffer").orElseGet(() -> input.getLongOr("fuelBuffer", 0L));
        this.energyCapacity = input.getLong("energyCapacity").orElseGet(() -> input.getLongOr("fuelCapacity", this.energyCapacity));
        this.activeRecipeId = input.read("activeRecipeId", Identifier.CODEC).orElse(null);

        this.parts.clear();
        for (final BlockPos pos : input.listOrEmpty("parts", BlockPos.CODEC)) {
            this.parts.add(pos.immutable());
        }

        this.structureDirty = true;
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.Packet<net.minecraft.network.protocol.game.ClientGamePacketListener> getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }
}
