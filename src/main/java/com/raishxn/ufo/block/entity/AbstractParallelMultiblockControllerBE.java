package com.raishxn.ufo.block.entity;

import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.crafting.IPatternDetails;
import appeng.api.implementations.blockentities.ICraftingMachine;
import appeng.api.implementations.blockentities.PatternContainerGroup;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.storage.IStorageService;
import appeng.api.stacks.AEKey;
import appeng.api.storage.MEStorage;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.grid.AENetworkedBlockEntity;
import com.raishxn.ufo.api.multiblock.MultiblockTierScaling;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.block.MultiblockBlocks;
import com.raishxn.ufo.block.entity.processing.MultiblockProcessingRecipe;
import com.raishxn.ufo.block.entity.processing.ParallelProcessState;
import com.raishxn.ufo.compat.mekanism.MekanismChemicalStorage;
import com.raishxn.ufo.compat.mekanism.UfoMekanismKey;
import com.raishxn.ufo.fluid.ModFluids;
import com.raishxn.ufo.init.ModSounds;
import com.raishxn.ufo.item.custom.BaseCatalystItem;
import com.raishxn.ufo.item.custom.DimensionalCatalystItem;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractParallelMultiblockControllerBE extends AbstractSimpleMultiblockControllerBE implements ICraftingMachine {
    protected static final int MAX_PARALLEL_THREADS = 27;
    protected static final int SAFE_MODE_PARALLEL_THREADS = 9;
    protected static final int OVERCLOCK_SPEED_MULTIPLIER = 5;
    private static final int RECIPE_CACHE_REFRESH_TICKS = 100;
    private static final int THERMAL_MAX = 10000;
    private static final int OVERLOAD_TICKS = 100;
    private static final float THERMAL_EXPLOSION_POWER = 30.0F;
    protected final List<ParallelProcessState> processStates = new ArrayList<>();
    private long lastClientSyncTick = Long.MIN_VALUE;
    private int lastClientSyncHash = Integer.MIN_VALUE;
    private int thermalTicker = 0;
    private int overloadTimer = -1;
    @Nullable
    private PatternContainerGroup cachedCraftingMachineInfo;
    private int cachedCraftingMachineTier = Integer.MIN_VALUE;
    @Nullable
    private List<MultiblockProcessingRecipe> cachedAvailableRecipes;
    @Nullable
    private Map<ResourceLocation, MultiblockProcessingRecipe> cachedRecipeIndex;
    private long lastRecipeCacheRefreshTick = Long.MIN_VALUE;

    protected AbstractParallelMultiblockControllerBE(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.maxTemperature = THERMAL_MAX;
        for (int i = 0; i < MAX_PARALLEL_THREADS; i++) {
            this.processStates.add(new ParallelProcessState());
        }
    }

    @Override
    protected void machineTick() {
        if (!this.assembled || this.level == null) {
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.storedEnergy = 0L;
            this.maxStoredEnergy = 0L;
            this.displayedRecipes.clear();
            updateTemperature(0, null, null, CatalystProfile.DEFAULT);
            syncClientState(false);
            return;
        }

        RecipeSnapshot recipes = getRecipeSnapshot();
        List<MultiblockProcessingRecipe> availableRecipes = recipes.recipes();
        Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex = recipes.index();

        AENetworkedBlockEntity nodeBE = getConnectedNetworkNode();
        if (nodeBE == null || nodeBE.getActionableNode() == null) {
            clearProcessStates();
            refreshProcessStates(recipeIndex);
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.storedEnergy = 0L;
            this.maxStoredEnergy = 0L;
            rebuildDisplayedRecipes(recipeIndex);
            updateTemperature(0, null, null, CatalystProfile.DEFAULT);
            syncClientState(false);
            return;
        }

        IGridNode node = nodeBE.getActionableNode();
        IGrid grid = node.getGrid();
        if (grid == null) {
            clearProcessStates();
            refreshProcessStates(recipeIndex);
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.storedEnergy = 0L;
            this.maxStoredEnergy = 0L;
            rebuildDisplayedRecipes(recipeIndex);
            updateTemperature(0, null, null, CatalystProfile.DEFAULT);
            syncClientState(false);
            return;
        }

        IEnergyService energyService = grid.getEnergyService();
        IStorageService storageService = grid.getStorageService();
        MEStorage inventory = storageService.getInventory();
        IActionSource src = IActionSource.ofMachine(nodeBE);
        refreshProcessStates(recipeIndex);
        CatalystProfile catalystProfile = getCatalystProfile();

        boolean anyRunning = false;
        int hottestMaxProgress = 0;
        int hottestProgress = 0;
        int runningThreads = 0;
        boolean thermalLocked = this.safeMode && this.temperature >= this.maxTemperature;
        int parallelLimit = getParallelThreadLimit();

        for (ParallelProcessState processState : this.processStates) {
            if (!processState.isActive()) {
                continue;
            }

            MultiblockProcessingRecipe recipe = recipeIndex.get(processState.getRecipeId());
            if (recipe == null) {
                processState.clear();
                continue;
            }

            int scaledMaxProgress = getAdjustedProcessingTime(recipe, catalystProfile);
            if (scaledMaxProgress > hottestMaxProgress) {
                hottestMaxProgress = scaledMaxProgress;
                hottestProgress = processState.getProgress();
            }

            if (!MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier()) || thermalLocked) {
                continue;
            }

            if (runningThreads >= parallelLimit) {
                continue;
            }

            processState.resizeBuffers(recipe.itemInputs().size(), recipe.fluidInputs().size(), recipe.chemicalInputs().size());
            long scaledEnergy = getAdjustedEnergyCost(recipe, catalystProfile);
            chargeEnergy(processState, energyService, scaledEnergy);
            boolean materialsFulfilled = pullIngredients(processState, recipe, inventory, src);
            if (!materialsFulfilled || processState.getEnergyBuffer() < scaledEnergy) {
                continue;
            }

            runningThreads++;
            anyRunning = true;
            processState.setProgress(processState.getProgress() + getProgressPerTick());
            if (processState.getProgress() >= scaledMaxProgress) {
                finishRecipe(processState, recipe, inventory, src);
            }
        }

        this.running = anyRunning;
        this.maxProgress = hottestMaxProgress;
        this.progress = hottestProgress;
        updateDisplayedEnergy(recipeIndex, catalystProfile);
        rebuildDisplayedRecipes(recipeIndex, catalystProfile);
        updateTemperature(runningThreads, inventory, src, catalystProfile);
        this.setChanged();
        syncClientState(true);
    }

    private Map<ResourceLocation, MultiblockProcessingRecipe> indexRecipes(List<MultiblockProcessingRecipe> availableRecipes) {
        Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex = new HashMap<>(availableRecipes.size());
        for (MultiblockProcessingRecipe recipe : availableRecipes) {
            recipeIndex.put(recipe.id(), recipe);
        }
        return recipeIndex;
    }

    private RecipeSnapshot getRecipeSnapshot() {
        long gameTime = this.level != null ? this.level.getGameTime() : 0L;
        if (this.cachedAvailableRecipes == null
                || this.cachedRecipeIndex == null
                || this.lastRecipeCacheRefreshTick == Long.MIN_VALUE
                || gameTime - this.lastRecipeCacheRefreshTick >= RECIPE_CACHE_REFRESH_TICKS) {
            refreshRecipeCache(gameTime);
        }
        return new RecipeSnapshot(this.cachedAvailableRecipes, this.cachedRecipeIndex);
    }

    private void refreshRecipeCache(long gameTime) {
        List<MultiblockProcessingRecipe> recipes = List.copyOf(getAvailableRecipes());
        this.cachedAvailableRecipes = recipes;
        this.cachedRecipeIndex = indexRecipes(recipes);
        this.lastRecipeCacheRefreshTick = gameTime;
    }

    private void invalidateRecipeCache() {
        this.cachedAvailableRecipes = null;
        this.cachedRecipeIndex = null;
        this.lastRecipeCacheRefreshTick = Long.MIN_VALUE;
    }

    private void refreshProcessStates(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex) {
        for (ParallelProcessState state : this.processStates) {
            if (!state.isActive()) {
                continue;
            }

            MultiblockProcessingRecipe recipe = recipeIndex.get(state.getRecipeId());
            if (recipe == null || !MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())) {
                state.clear();
                continue;
            }

            state.resizeBuffers(recipe.itemInputs().size(), recipe.fluidInputs().size(), recipe.chemicalInputs().size());
            if (!state.isPatternPushed() && !state.hasBufferedWork()) {
                state.clear();
            }
        }
    }

    private void clearProcessStates() {
        for (ParallelProcessState state : this.processStates) {
            state.clear();
        }
    }

    protected int getParallelThreadLimit() {
        return this.safeMode ? SAFE_MODE_PARALLEL_THREADS : MAX_PARALLEL_THREADS;
    }

    protected int getActiveProcessCount() {
        int count = 0;
        for (ParallelProcessState state : this.processStates) {
            if (state.isActive()) {
                count++;
            }
        }
        return count;
    }

    protected int getProgressPerTick() {
        return this.overclocked ? OVERCLOCK_SPEED_MULTIPLIER : 1;
    }

    protected double getHeatGenerationMultiplier() {
        return 1.0D;
    }

    private void chargeEnergy(ParallelProcessState state, IEnergyService energyService, long targetEnergy) {
        if (state.getEnergyBuffer() >= targetEnergy) {
            return;
        }
        long needed = targetEnergy - state.getEnergyBuffer();
        long chargeRate = 5_000_000L;
        double extracted = energyService.extractAEPower(Math.min(needed, chargeRate), Actionable.MODULATE, PowerMultiplier.CONFIG);
        state.setEnergyBuffer(state.getEnergyBuffer() + (long) extracted);
    }

    private boolean pullIngredients(ParallelProcessState state, MultiblockProcessingRecipe recipe, MEStorage inventory, IActionSource src) {
        boolean materialsFulfilled = true;

        for (int i = 0; i < recipe.itemInputs().size(); i++) {
            var requirement = recipe.itemInputs().get(i);
            if (state.getItemBuffers()[i] >= requirement.amount()) {
                continue;
            }
            long needed = requirement.amount() - state.getItemBuffers()[i];
            long toExtract = Math.min(needed, 100_000L);
            for (ItemStack match : requirement.ingredient().getItems()) {
                long extracted = inventory.extract(AEItemKey.of(match), toExtract, Actionable.MODULATE, src);
                state.getItemBuffers()[i] += extracted;
                toExtract -= extracted;
                if (toExtract <= 0) {
                    break;
                }
            }
            if (state.getItemBuffers()[i] < requirement.amount()) {
                materialsFulfilled = false;
            }
        }

        for (int i = 0; i < recipe.fluidInputs().size(); i++) {
            var requirement = recipe.fluidInputs().get(i);
            if (state.getFluidBuffers()[i] >= requirement.amount()) {
                continue;
            }
            long needed = requirement.amount() - state.getFluidBuffers()[i];
            long extracted = inventory.extract(AEFluidKey.of(requirement.fluid().getFluid()), Math.min(needed, 1_000_000L), Actionable.MODULATE, src);
            state.getFluidBuffers()[i] += extracted;
            if (state.getFluidBuffers()[i] < requirement.amount()) {
                materialsFulfilled = false;
            }
        }

        for (int i = 0; i < recipe.chemicalInputs().size(); i++) {
            var requirement = recipe.chemicalInputs().get(i);
            if (state.getChemicalBuffers()[i] >= requirement.amount()) {
                continue;
            }
            long needed = requirement.amount() - state.getChemicalBuffers()[i];
            long extracted = extractChemicalFromHatches(requirement.chemicalId(), Math.min(needed, 1_000_000L));
            state.getChemicalBuffers()[i] += extracted;
            if (state.getChemicalBuffers()[i] < requirement.amount()) {
                materialsFulfilled = false;
            }
        }

        return materialsFulfilled;
    }

    private long extractChemicalFromHatches(ResourceLocation chemicalId, long amount) {
        if (this.level == null || amount <= 0L) {
            return 0L;
        }

        long remaining = amount;
        long extractedTotal = 0L;
        for (BlockPos partPos : this.parts) {
            if (!(this.level.getBlockEntity(partPos) instanceof MekanismChemicalStorage storage) || !storage.supportsChemicalIO()) {
                continue;
            }

            ResourceLocation storedId = storage.getStoredChemicalId();
            if (storedId == null || !storedId.equals(chemicalId)) {
                continue;
            }

            long extracted = Math.min(remaining, storage.getStoredChemicalAmount());
            if (extracted <= 0L) {
                continue;
            }

            storage.setStoredChemical(storedId, storage.getStoredChemicalAmount() - extracted);
            extractedTotal += extracted;
            remaining -= extracted;
            if (remaining <= 0L) {
                break;
            }
        }
        return extractedTotal;
    }

    private void finishRecipe(ParallelProcessState state, MultiblockProcessingRecipe recipe, MEStorage inventory, IActionSource src) {
        CatalystProfile catalystProfile = getCatalystProfile();
        for (var output : recipe.outputs()) {
            if (!output.item().isEmpty()) {
                inventory.insert(AEItemKey.of(output.item()), getAdjustedItemOutputAmount(output.amount(), catalystProfile), Actionable.MODULATE, src);
            }
            if (!output.fluid().isEmpty()) {
                inventory.insert(AEFluidKey.of(output.fluid().getFluid()), getAdjustedItemOutputAmount(output.amount(), catalystProfile), Actionable.MODULATE, src);
            }
        }

        state.clear();
    }

    private void rebuildDisplayedRecipes(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex) {
        rebuildDisplayedRecipes(recipeIndex, getCatalystProfile());
    }

    private void rebuildDisplayedRecipes(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex, CatalystProfile catalystProfile) {
        this.displayedRecipes.clear();
        for (ParallelProcessState processState : this.processStates) {
            if (!processState.isActive()) {
                continue;
            }
            MultiblockProcessingRecipe recipe = recipeIndex.get(processState.getRecipeId());
            if (recipe == null) {
                continue;
            }
            var primaryOutput = recipe.primaryOutput();
            int scaledMaxProgress = getAdjustedProcessingTime(recipe, catalystProfile);
            int displayedMaxProgress = getDisplayedTicks(scaledMaxProgress);
            int displayedProgress = Math.min(displayedMaxProgress, getDisplayedTicks(processState.getProgress()));
            Component label = primaryOutput.item().isEmpty()
                    ? (primaryOutput.fluid().isEmpty() ? Component.literal(recipe.name()) : primaryOutput.fluid().getHoverName())
                    : primaryOutput.item().getHoverName();
            if (!MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())) {
                label = label.copy().append(Component.literal(" [Locked: MK" + recipe.requiredTier() + "]"));
            }
            this.displayedRecipes.add(new UniversalDisplayedRecipe(
                    primaryOutput.item(),
                    primaryOutput.fluid(),
                    label,
                    getMaximumAdjustedItemOutputAmount(primaryOutput.amount(), catalystProfile),
                    displayedProgress,
                    displayedMaxProgress));
        }
    }

    protected int getDisplayedTicks(int rawTicks) {
        int divisor = getProgressPerTick();
        return Math.max(0, (rawTicks + divisor - 1) / divisor);
    }

    private void updateDisplayedEnergy(Map<ResourceLocation, MultiblockProcessingRecipe> recipeIndex, CatalystProfile catalystProfile) {
        long bufferedEnergy = 0L;
        long targetEnergy = 0L;
        for (ParallelProcessState processState : this.processStates) {
            if (!processState.isActive()) {
                continue;
            }

            bufferedEnergy += Math.max(0L, processState.getEnergyBuffer());
            MultiblockProcessingRecipe recipe = recipeIndex.get(processState.getRecipeId());
            if (recipe != null) {
                targetEnergy += Math.max(0L, getAdjustedEnergyCost(recipe, catalystProfile));
            }
        }

        this.storedEnergy = bufferedEnergy;
        this.maxStoredEnergy = targetEnergy;
    }

    private void updateTemperature(int activeThreads, @Nullable MEStorage inventory, @Nullable IActionSource src, CatalystProfile catalystProfile) {
        this.thermalTicker++;

        if (catalystProfile.creative()) {
            if (this.temperature > 0 && inventory != null && src != null) {
                this.temperature -= consumeCoolant(inventory, src);
            }
        } else if (activeThreads > 0) {
            if (this.thermalTicker % 2 == 0) {
                int baseHeat = Math.max(1, activeThreads) * (this.overclocked ? 5 : 1);
                int heatToAdd = Math.max(0, (int) Math.ceil(baseHeat * getHeatGenerationMultiplier() * catalystProfile.heatMultiplier()));
                this.temperature = Math.min(this.maxTemperature, this.temperature + heatToAdd);
            }
        } else if (this.temperature > 0 && this.thermalTicker % 40 == 0) {
            this.temperature -= 1;
        }

        if (this.temperature > 0 && inventory != null && src != null) {
            this.temperature -= consumeCoolant(inventory, src);
        }

        if (this.temperature < 0) {
            this.temperature = 0;
        }

        if (this.safeMode) {
            this.overloadTimer = -1;
            return;
        }

        if (this.temperature >= this.maxTemperature) {
            if (this.overloadTimer == -1) {
                this.overloadTimer = OVERLOAD_TICKS;
            }
        } else {
            this.overloadTimer = -1;
        }

        if (this.overloadTimer > 0) {
            if (this.level != null && this.overloadTimer % 20 == 0) {
                this.level.playSound(null, this.worldPosition, ModSounds.DMA_ALARM.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.6f, 0.8f);
            }

            this.overloadTimer--;
            if (this.overloadTimer == 0) {
                triggerThermalExplosion();
            }
        }
    }

    private int consumeCoolant(MEStorage inventory, IActionSource src) {
        for (AEFluidKey coolantKey : getCoolantPriority()) {
            if (coolantKey == null || coolantKey.getFluid() == Fluids.EMPTY) {
                continue;
            }

            CoolantProfile profile = getCoolantProfile(coolantKey.getFluid());
            long simulatedAvailable = inventory.extract(coolantKey, profile.maxConsumePerTick(), Actionable.SIMULATE, src);
            if (simulatedAvailable <= 0L) {
                continue;
            }

            long amountToConsume;
            long heatCooled;
            if (profile.millibucketsPerHeat() > 0) {
                amountToConsume = Math.min(simulatedAvailable, profile.maxConsumePerTick());
                long possibleHeat = amountToConsume / profile.millibucketsPerHeat();
                heatCooled = Math.min(this.temperature, possibleHeat);
                amountToConsume = heatCooled * profile.millibucketsPerHeat();
            } else {
                amountToConsume = Math.min(simulatedAvailable, profile.maxConsumePerTick());
                long possibleHeat = amountToConsume * profile.heatPerMillibucket();
                if (this.temperature < possibleHeat) {
                    amountToConsume = Math.max(1L,
                            (long) Math.ceil(this.temperature / (double) profile.heatPerMillibucket()));
                }
                heatCooled = Math.min(this.temperature, amountToConsume * profile.heatPerMillibucket());
            }

            if (amountToConsume <= 0L || heatCooled <= 0L) {
                continue;
            }

            long extracted = inventory.extract(coolantKey, amountToConsume, Actionable.MODULATE, src);
            if (extracted <= 0L) {
                continue;
            }

            if (profile.millibucketsPerHeat() > 0) {
                return (int) Math.min(this.temperature, extracted / profile.millibucketsPerHeat());
            }

            return (int) Math.min(this.temperature, extracted * profile.heatPerMillibucket());
        }

        return 0;
    }

    private AEFluidKey[] getCoolantPriority() {
        AEFluidKey tier1 = AEFluidKey.of(ModFluids.SOURCE_GELID_CRYOTHEUM.get());
        AEFluidKey tier2 = AEFluidKey.of(ModFluids.SOURCE_STABLE_COOLANT.get());
        AEFluidKey tier3 = AEFluidKey.of(ModFluids.SOURCE_TEMPORAL_FLUID.get());
        return switch (this.machineTier) {
            case 3 -> new AEFluidKey[]{tier3, tier2, tier1};
            case 2 -> new AEFluidKey[]{tier2, tier3, tier1};
            default -> new AEFluidKey[]{tier1, tier2, tier3};
        };
    }

    private CoolantProfile getCoolantProfile(Fluid fluid) {
        if (fluid == ModFluids.SOURCE_TEMPORAL_FLUID.get() || fluid == ModFluids.FLOWING_TEMPORAL_FLUID.get()) {
            return new CoolantProfile(100, 0, 10);
        }
        if (fluid == ModFluids.SOURCE_STABLE_COOLANT.get() || fluid == ModFluids.FLOWING_STABLE_COOLANT.get()) {
            return new CoolantProfile(50, 0, 10);
        }
        if (fluid == ModFluids.SOURCE_GELID_CRYOTHEUM.get() || fluid == ModFluids.FLOWING_GELID_CRYOTHEUM.get()) {
            return new CoolantProfile(0, 120, 1000);
        }
        return new CoolantProfile(15, 0, 10);
    }

    private void triggerThermalExplosion() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        Level level = this.level;
        level.explode(null,
                this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5,
                THERMAL_EXPLOSION_POWER,
                Level.ExplosionInteraction.BLOCK);
        onControllerBroken();
        removeControllerBlockAfterExplosion();
        this.temperature = 0;
        this.overloadTimer = -1;
        this.running = false;
        this.progress = 0;
        this.maxProgress = 0;
        clearProcessStates();
        updateDisplayedEnergy(Map.of(), CatalystProfile.DEFAULT);
        this.displayedRecipes.clear();
        saveChanges();
    }

    private void removeControllerBlockAfterExplosion() {
        if (this.level == null) {
            return;
        }

        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return;
        }

        this.level.removeBlock(this.worldPosition, false);
    }

    private AENetworkedBlockEntity getConnectedNetworkNode() {
        if (this.level == null) {
            return null;
        }
        for (BlockPos position : this.parts) {
            if (this.level.getBlockEntity(position) instanceof AENetworkedBlockEntity nodeBE) {
                if (nodeBE.getActionableNode() != null && nodeBE.getActionableNode().getGrid() != null) {
                    return nodeBE;
                }
            }
        }
        return null;
    }

    private CatalystProfile getCatalystProfile() {
        double heatMultiplier = 1.0D;
        double speedMultiplier = 1.0D;
        double energyMultiplier = 1.0D;
        double bonusDropChance = 0.0D;
        boolean creative = false;
        int identicalCount = 0;
        BaseCatalystItem firstCatalyst = null;
        boolean synergyPossible = true;

        for (int i = 0; i < this.upgrades.size(); i++) {
            ItemStack upgradeStack = this.upgrades.getStackInSlot(i);
            if (upgradeStack.isEmpty()) {
                synergyPossible = false;
                continue;
            }

            if (upgradeStack.getItem() instanceof DimensionalCatalystItem) {
                creative = true;
                synergyPossible = false;
                continue;
            }

            if (upgradeStack.getItem() instanceof BaseCatalystItem catalyst) {
                heatMultiplier += catalyst.getStaticHeat() / 100.0D;
                speedMultiplier *= catalyst.getSpeedMultiplier();
                energyMultiplier *= catalyst.getPowerMultiplier();
                bonusDropChance += catalyst.getBonusDropChance();

                if (firstCatalyst == null) {
                    firstCatalyst = catalyst;
                    identicalCount++;
                } else if (firstCatalyst == catalyst) {
                    identicalCount++;
                } else {
                    synergyPossible = false;
                }
                continue;
            }

            synergyPossible = false;
        }

        if (synergyPossible && identicalCount == 4 && firstCatalyst != null) {
            heatMultiplier *= 1.5D;
            if ("chrono".equals(firstCatalyst.getFamily())) {
                speedMultiplier *= 2.0D;
            } else if ("matterflow".equals(firstCatalyst.getFamily())) {
                energyMultiplier *= 0.5D;
            } else if ("quantum".equals(firstCatalyst.getFamily())) {
                bonusDropChance += 0.5D;
            } else if ("overflux".equals(firstCatalyst.getFamily())) {
                heatMultiplier *= 0.5D;
            }
        }

        if (creative) {
            return CatalystProfile.CREATIVE;
        }

        return new CatalystProfile(
                false,
                Math.max(0.0D, heatMultiplier),
                Math.max(0.01D, speedMultiplier),
                Math.max(0.0D, energyMultiplier),
                Math.max(0.0D, bonusDropChance));
    }

    private int getAdjustedProcessingTime(MultiblockProcessingRecipe recipe, CatalystProfile catalystProfile) {
        if (catalystProfile.creative()) {
            return 1;
        }
        int tierAdjustedTime = MultiblockTierScaling.adjustedTime(recipe.time(), this.machineTier, recipe.requiredTier());
        return Math.max(1, (int) Math.ceil(tierAdjustedTime / catalystProfile.speedMultiplier()));
    }

    private long getAdjustedEnergyCost(MultiblockProcessingRecipe recipe, CatalystProfile catalystProfile) {
        if (catalystProfile.creative()) {
            return 0L;
        }
        long tierAdjustedEnergy = MultiblockTierScaling.adjustedEnergy(recipe.energy(), this.machineTier, recipe.requiredTier());
        return Math.max(1L, (long) Math.ceil(tierAdjustedEnergy * catalystProfile.energyMultiplier()));
    }

    private long getAdjustedItemOutputAmount(long baseAmount, CatalystProfile catalystProfile) {
        if (baseAmount <= 0L) {
            return 0L;
        }

        double bonusChance = Math.max(0.0D, catalystProfile.bonusDropChance());
        long bonusRolls = (long) bonusChance;
        double fractionalBonusRoll = bonusChance - bonusRolls;
        if (fractionalBonusRoll > 0.0D && this.level != null && this.level.random.nextDouble() < fractionalBonusRoll) {
            bonusRolls++;
        }

        return saturatedMultiply(baseAmount, 1L + bonusRolls);
    }

    private long getMaximumAdjustedItemOutputAmount(long baseAmount, CatalystProfile catalystProfile) {
        if (baseAmount <= 0L) {
            return 0L;
        }
        return saturatedMultiply(baseAmount, 1L + (long) Math.ceil(Math.max(0.0D, catalystProfile.bonusDropChance())));
    }

    private long saturatedMultiply(long value, long multiplier) {
        if (value <= 0L || multiplier <= 0L) {
            return 0L;
        }
        if (value > Long.MAX_VALUE / multiplier) {
            return Long.MAX_VALUE;
        }
        return value * multiplier;
    }

    protected abstract List<MultiblockProcessingRecipe> getAvailableRecipes();

    protected MultiblockProcessingRecipe findRecipe(List<MultiblockProcessingRecipe> availableRecipes, ResourceLocation recipeId) {
        for (MultiblockProcessingRecipe recipe : availableRecipes) {
            if (recipe.id().equals(recipeId)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public PatternContainerGroup getCraftingMachineInfo() {
        if (this.cachedCraftingMachineInfo == null || this.cachedCraftingMachineTier != this.machineTier) {
            this.cachedCraftingMachineTier = this.machineTier;
            this.cachedCraftingMachineInfo = new PatternContainerGroup(
                    AEItemKey.of(this.getBlockState().getBlock().asItem()),
                    Component.translatable(getControllerTranslationKey()),
                    List.of(Component.literal("MK" + this.machineTier)));
        }
        return this.cachedCraftingMachineInfo;
    }

    @Override
    public boolean pushPattern(IPatternDetails patternDetails, KeyCounter[] inputs, net.minecraft.core.Direction ejectionDirection) {
        if (!this.assembled) {
            return false;
        }

        if (getActiveProcessCount() >= getParallelThreadLimit()) {
            return false;
        }

        MultiblockProcessingRecipe recipe = resolvePatternRecipe(patternDetails, inputs);
        if (recipe == null || !MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())) {
            return false;
        }

        ParallelProcessState state = findInactiveState();
        if (state == null) {
            return false;
        }

        state.clear();
        state.setRecipeId(recipe.id());
        state.setPatternPushed(true);
        state.resizeBuffers(recipe.itemInputs().size(), recipe.fluidInputs().size(), recipe.chemicalInputs().size());
        for (int i = 0; i < recipe.itemInputs().size(); i++) {
            state.getItemBuffers()[i] = recipe.itemInputs().get(i).amount();
        }
        for (int i = 0; i < recipe.fluidInputs().size(); i++) {
            state.getFluidBuffers()[i] = recipe.fluidInputs().get(i).amount();
        }
        for (int i = 0; i < recipe.chemicalInputs().size(); i++) {
            state.getChemicalBuffers()[i] = recipe.chemicalInputs().get(i).amount();
        }
        state.setEnergyBuffer(0L);
        state.setProgress(0);

        for (KeyCounter input : inputs) {
            input.clear();
        }

        rebuildDisplayedRecipes(getRecipeSnapshot().index());
        saveChanges();
        return true;
    }

    @Override
    public boolean acceptsPlans() {
        return this.assembled
                && getActiveProcessCount() < getParallelThreadLimit()
                && findInactiveState() != null;
    }

    private ParallelProcessState findInactiveState() {
        for (ParallelProcessState state : this.processStates) {
            if (!state.isActive()) {
                return state;
            }
        }
        return null;
    }

    private MultiblockProcessingRecipe resolvePatternRecipe(IPatternDetails patternDetails, KeyCounter[] inputs) {
        List<MultiblockProcessingRecipe> outputMatches = new ArrayList<>();
        for (MultiblockProcessingRecipe recipe : getRecipeSnapshot().recipes()) {
            if (MultiblockTierScaling.canRunRecipe(this.machineTier, recipe.requiredTier())
                    && patternMatchesOutputs(patternDetails.getOutputs(), recipe.outputs(), getCatalystProfile())) {
                outputMatches.add(recipe);
            }
        }

        if (outputMatches.isEmpty()) {
            return null;
        }

        if (outputMatches.size() == 1) {
            return outputMatches.getFirst();
        }

        for (MultiblockProcessingRecipe recipe : outputMatches) {
            if (patternMatchesInputs(inputs, recipe)) {
                return recipe;
            }
        }

        return null;
    }

    private boolean patternMatchesInputs(KeyCounter[] inputs, MultiblockProcessingRecipe recipe) {
        List<PatternStack> availableStacks = flattenInputs(inputs);
        if (availableStacks.isEmpty() && (!recipe.itemInputs().isEmpty() || !recipe.fluidInputs().isEmpty())) {
            return false;
        }

        List<PatternStack> remaining = new ArrayList<>(availableStacks);
        for (var requirement : recipe.itemInputs()) {
            if (!removeMatchingItemRequirement(remaining, requirement)) {
                return false;
            }
        }
        for (var requirement : recipe.fluidInputs()) {
            if (!removeMatchingFluidRequirement(remaining, requirement)) {
                return false;
            }
        }
        for (var requirement : recipe.chemicalInputs()) {
            if (!removeMatchingChemicalRequirement(remaining, requirement)) {
                return false;
            }
        }
        return remaining.isEmpty();
    }

    private boolean patternMatchesOutputs(List<GenericStack> outputs, List<MultiblockProcessingRecipe.OutputStack> recipeOutputs, CatalystProfile catalystProfile) {
        if (outputs.size() != recipeOutputs.size()) {
            return false;
        }

        List<PatternStack> remaining = new ArrayList<>();
        for (GenericStack output : outputs) {
            remaining.add(new PatternStack(output.what(), output.amount()));
        }

        for (var output : recipeOutputs) {
            AEKey expectedKey = !output.item().isEmpty()
                    ? AEItemKey.of(output.item())
                    : AEFluidKey.of(output.fluid().getFluid());
            if (expectedKey == null) {
                return false;
            }

            boolean matched = false;
            for (int i = 0; i < remaining.size(); i++) {
                PatternStack candidate = remaining.get(i);
                if (candidate.key.equals(expectedKey) && patternOutputAmountMatches(candidate.amount, output.amount(), catalystProfile)) {
                    remaining.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        return remaining.isEmpty();
    }

    private boolean patternOutputAmountMatches(long patternAmount, long baseAmount, CatalystProfile catalystProfile) {
        return patternAmount == baseAmount || patternAmount == getMaximumAdjustedItemOutputAmount(baseAmount, catalystProfile);
    }

    private List<PatternStack> flattenInputs(KeyCounter[] inputs) {
        List<PatternStack> stacks = new ArrayList<>();
        for (KeyCounter counter : inputs) {
            for (var entry : counter) {
                stacks.add(new PatternStack(entry.getKey(), entry.getLongValue()));
            }
        }
        return stacks;
    }

    private boolean removeMatchingItemRequirement(List<PatternStack> remaining, MultiblockProcessingRecipe.ItemRequirement requirement) {
        for (int i = 0; i < remaining.size(); i++) {
            PatternStack stack = remaining.get(i);
            if (stack.key instanceof AEItemKey itemKey
                    && stack.amount >= requirement.amount()
                    && requirement.ingredient().test(itemKey.toStack((int) stack.amount))) {
                long leftover = stack.amount - requirement.amount();
                if (leftover > 0L) {
                    remaining.set(i, new PatternStack(stack.key, leftover));
                } else {
                    remaining.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    private boolean removeMatchingFluidRequirement(List<PatternStack> remaining, MultiblockProcessingRecipe.FluidRequirement requirement) {
        for (int i = 0; i < remaining.size(); i++) {
            PatternStack stack = remaining.get(i);
            if (stack.key instanceof AEFluidKey fluidKey
                    && stack.amount >= requirement.amount()
                    && fluidKey.getFluid() == requirement.fluid().getFluid()) {
                long leftover = stack.amount - requirement.amount();
                if (leftover > 0L) {
                    remaining.set(i, new PatternStack(stack.key, leftover));
                } else {
                    remaining.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    private boolean removeMatchingChemicalRequirement(List<PatternStack> remaining, MultiblockProcessingRecipe.ChemicalRequirement requirement) {
        for (int i = 0; i < remaining.size(); i++) {
            PatternStack stack = remaining.get(i);
            if (stack.key instanceof UfoMekanismKey chemicalKey
                    && stack.amount >= requirement.amount()
                    && chemicalKey.getId().equals(requirement.chemicalId())) {
                long leftover = stack.amount - requirement.amount();
                if (leftover > 0L) {
                    remaining.set(i, new PatternStack(stack.key, leftover));
                } else {
                    remaining.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    private record PatternStack(AEKey key, long amount) {
    }

    private record RecipeSnapshot(
            List<MultiblockProcessingRecipe> recipes,
            Map<ResourceLocation, MultiblockProcessingRecipe> index) {
    }

    private record CoolantProfile(int heatPerMillibucket, int millibucketsPerHeat, long maxConsumePerTick) {
    }

    private record CatalystProfile(
            boolean creative,
            double heatMultiplier,
            double speedMultiplier,
            double energyMultiplier,
            double bonusDropChance) {
        private static final CatalystProfile DEFAULT = new CatalystProfile(false, 1.0D, 1.0D, 1.0D, 0.0D);
        private static final CatalystProfile CREATIVE = new CatalystProfile(true, 0.0D, 1000.0D, 0.0D, 1.0D);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag processTags = new ListTag();
        for (ParallelProcessState state : this.processStates) {
            processTags.add(state.save(registries));
        }
        tag.put("processStates", processTags);
        tag.putInt("thermalTicker", this.thermalTicker);
        tag.putInt("overloadTimer", this.overloadTimer);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("processStates", Tag.TAG_LIST)) {
            ListTag processTags = tag.getList("processStates", Tag.TAG_COMPOUND);
            for (int i = 0; i < Math.min(processTags.size(), this.processStates.size()); i++) {
                this.processStates.get(i).load(processTags.getCompound(i), registries);
            }
        }
        this.thermalTicker = tag.getInt("thermalTicker");
        this.overloadTimer = tag.contains("overloadTimer") ? tag.getInt("overloadTimer") : -1;
    }

    @Override
    public void onControllerBroken() {
        super.onControllerBroken();
        invalidateRecipeCache();
        for (ParallelProcessState processState : this.processStates) {
            processState.clear();
        }
        this.storedEnergy = 0L;
        this.maxStoredEnergy = 0L;
        this.overloadTimer = -1;
    }

    @Override
    protected int resolveMachineTier(com.raishxn.ufo.api.multiblock.MultiblockPattern.MatchResult result) {
        if (this.level == null) {
            return com.raishxn.ufo.api.multiblock.MultiblockMachineTier.MK1.level();
        }

        BlockState controllerState = this.level.getBlockState(this.worldPosition);
        Direction facing = MultiblockControllerDefinitions.getPatternFacing(this, controllerState);

        int resolvedTier = com.raishxn.ufo.api.multiblock.MultiblockMachineTier.MK3.level();
        boolean foundField = false;
        for (BlockPos fieldPos : getControllerPattern().getExpectedPositions(this.worldPosition, facing, 'F')) {
            BlockState fieldState = this.level.getBlockState(fieldPos);
            if (fieldState.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T1.get())) {
                resolvedTier = Math.min(resolvedTier, 1);
                foundField = true;
            } else if (fieldState.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T2.get())) {
                resolvedTier = Math.min(resolvedTier, 2);
                foundField = true;
            } else if (fieldState.is(MultiblockBlocks.STELLAR_FIELD_GENERATOR_T3.get())) {
                foundField = true;
            }
        }

        return foundField ? resolvedTier : com.raishxn.ufo.api.multiblock.MultiblockMachineTier.MK1.level();
    }

    @Override
    protected boolean hasOngoingWork() {
        for (ParallelProcessState state : this.processStates) {
            if (state.isActive()) {
                return true;
            }
        }
        return super.hasOngoingWork();
    }

    @Override
    public int getGuiActiveParallels() {
        return getActiveProcessCount();
    }

    @Override
    public int getGuiMaxParallels() {
        return getParallelThreadLimit();
    }

    private void syncClientState(boolean throttle) {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        int syncHash = computeClientSyncHash();
        if (syncHash == this.lastClientSyncHash) {
            return;
        }

        long gameTime = this.level.getGameTime();
        if (throttle && this.lastClientSyncTick != Long.MIN_VALUE && gameTime - this.lastClientSyncTick < 5L) {
            return;
        }

        this.lastClientSyncTick = gameTime;
        this.lastClientSyncHash = syncHash;
        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), net.minecraft.world.level.block.Block.UPDATE_ALL);
    }

    private int computeClientSyncHash() {
        int hash = Boolean.hashCode(this.assembled);
        hash = 31 * hash + Boolean.hashCode(this.running);
        hash = 31 * hash + this.progress;
        hash = 31 * hash + this.maxProgress;
        hash = 31 * hash + this.temperature;
        hash = 31 * hash + this.machineTier;
        hash = 31 * hash + Boolean.hashCode(this.safeMode);
        hash = 31 * hash + Boolean.hashCode(this.overclocked);
        hash = 31 * hash + Long.hashCode(this.storedEnergy);
        hash = 31 * hash + Long.hashCode(this.maxStoredEnergy);
        hash = 31 * hash + getActiveProcessCount();
        hash = 31 * hash + computeDisplayedRecipesHash();
        return hash;
    }

    private int computeDisplayedRecipesHash() {
        int hash = 1;
        for (UniversalDisplayedRecipe recipe : this.displayedRecipes) {
            hash = 31 * hash + recipe.label().getString().hashCode();
            hash = 31 * hash + recipe.progress();
            hash = 31 * hash + recipe.maxProgress();
            hash = 31 * hash + Long.hashCode(recipe.outputAmount());
            hash = 31 * hash + java.util.Objects.hashCode(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem()));
            hash = 31 * hash + java.util.Objects.hashCode(net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid()));
        }
        return hash;
    }
}
