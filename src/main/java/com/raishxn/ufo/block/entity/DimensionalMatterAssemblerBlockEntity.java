package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.util.EntityDamageHelper;
import java.util.*;
import java.util.Comparator;

import net.pedroksl.ae2addonlib.recipes.IngredientStack;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.mojang.serialization.Codec;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.fluids.FluidStack;

import com.raishxn.ufo.block.DimensionalMatterAssemblerBlock;
import com.raishxn.ufo.datagen.ModDataComponents;
import com.raishxn.ufo.recipe.DimensionalMatterAssemblerRecipe;
import com.raishxn.ufo.init.ModRecipes;

import appeng.api.behaviors.ExternalStorageStrategy;
import appeng.api.config.*;
import appeng.api.config.Actionable;
import appeng.api.inventories.ISegmentedInventory;
import appeng.api.inventories.InternalInventory;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.IActionSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.orientation.RelativeSide;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEFluidKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.GenericStack;
import appeng.api.storage.MEStorage;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;

import appeng.api.util.AECableType;
import appeng.api.util.IConfigManager;
import appeng.api.util.IConfigurableObject;
import appeng.blockentity.grid.AENetworkedPoweredBlockEntity;
import appeng.core.definitions.AEItems;
import appeng.helpers.externalstorage.GenericStackInv;
import appeng.me.storage.CompositeStorage;
import appeng.menu.ISubMenu;
import appeng.parts.automation.StackWorldBehaviors;
import appeng.util.SettingsFrom;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.CombinedInternalInventory;
import appeng.util.inv.FilteredInternalInventory;
import appeng.util.inv.filter.AEItemFilters;
import net.pedroksl.ae2addonlib.api.IDirectionalOutputHost;

public class DimensionalMatterAssemblerBlockEntity extends AENetworkedPoweredBlockEntity
        implements IGridTickable, IUpgradeableObject, IConfigurableObject, IDirectionalOutputHost {
    private static final int MAX_INPUT_SLOTS = 9;
    private static final int MAX_OUTPUT_SLOTS = 2;
    private static final int MAX_POWER_STORAGE = 500000;
    private static final int MAX_TANK_CAPACITY = 16000;

    private static List<RecipeHolder<DimensionalMatterAssemblerRecipe>> sortedRecipeCache = null;
    private static RecipeManager cachedRecipeManagerRef = null;

    private final IUpgradeInventory upgrades;
    private final IConfigManager configManager;

    private final AppEngInternalInventory inputInv = new AppEngInternalInventory(this, MAX_INPUT_SLOTS, 64);
    private final AppEngInternalInventory outputInv = new AppEngInternalInventory(this, MAX_OUTPUT_SLOTS, 64);
    private final InternalInventory inv = new CombinedInternalInventory(this.inputInv, this.outputInv);

    private final FilteredInternalInventory inputExposed = new FilteredInternalInventory(this.inputInv,
            AEItemFilters.INSERT_ONLY);
    private final FilteredInternalInventory outputExposed = new FilteredInternalInventory(this.outputInv,
            AEItemFilters.EXTRACT_ONLY);
    private final InternalInventory invExposed = new CombinedInternalInventory(this.inputExposed, this.outputExposed);

    private final CustomGenericInv fluidInv = new CustomGenericInv(Set.of(AEKeyType.fluids()), this::onChangeTank,
            GenericStackInv.Mode.STORAGE, 4);

    private boolean working = false;
    private int processingTime = 0;
    private boolean dirty = false;

    private DimensionalMatterAssemblerRecipe cachedTask = null;

    private EnumSet<RelativeSide> allowedOutputs = EnumSet.noneOf(RelativeSide.class);

    private final HashMap<Direction, Map<AEKeyType, ExternalStorageStrategy>> exportStrategies = new HashMap<>();

    private boolean showWarning = false;

    private int temperature = 0;
    private int maxTemperature = 10000;
    private int overloadTimer = -1;
    private int localMaxPower = MAX_POWER_STORAGE;
    private int thermalTicker = 0; // Tick counter for smooth heat/cool transitions
    private int productiveThermalTicks = 0;
    private int productiveHeatRemainder = 0;

    public DimensionalMatterAssemblerBlockEntity(final BlockEntityType<?> type, final BlockPos pos, final BlockState blockState) {
        super(type, pos, blockState);

        this.getMainNode().setFlags().setIdlePowerUsage(0).addService(IGridTickable.class, this);
        this.setInternalMaxPower(this.localMaxPower);

        this.fluidInv.setCapacity(AEKeyType.fluids(), MAX_TANK_CAPACITY);

        // BlockEntityType construction because the block is already registered.
        // BuiltInRegistries.ITEM.get() returns AIR during BET registration because
        this.upgrades = appeng.api.upgrades.UpgradeInventories.forMachine(
                blockState.getBlock().asItem(), 4, this::saveChanges);

        this.configManager = appeng.api.util.IConfigManager.builder(this::onConfigChanged)
                .registerSetting(appeng.api.config.Settings.AUTO_EXPORT, appeng.api.config.YesNo.NO)
                .build();

        this.setPowerSides(getGridConnectableSides(getOrientation()));
    }

    public boolean isWorking() {
        return this.working;
    }

    public void setWorking(final boolean working) {
        if (working != this.working) {
            if (this.level != null && !this.level.isClientSide()) {
                updateBlockState(working);
                this.markForUpdate();
            }
        }
        this.working = working;
    }

    @Override
    public void onReady() {
        super.onReady();
        recalculateUpgrades();
    }

    public int getTemperature() {
        return this.temperature;
    }

    public int getMaxTemperature() {
        return this.maxTemperature;
    }

    public int getOverloadTimer() {
        return this.overloadTimer;
    }

    public void serverTick() {
        if (this.level == null || this.level.isClientSide())
            return;

        if (this.dirty) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }

        handleThermalLogic();
    }

    /**
     * Executes the thermal simulation loop, regulating machine heat based on production.
     * <p><b>Thermal Engine Lifecycle:</b></p>
     * <ul>
     *      <li><b>Generation</b>: While working, block adds heat dynamically based on its upgrades (+1 HU/4ticks).</li>
     *      <li><b>Passive Cooling</b>: While idle, ambient block surfaces slowly radiate heat back to the environment (-1 HU/40ticks).</li>
     *      <li><b>Active Synergies</b>: Fluids injected into the Cooling Tank are instantly consumed to suppress excess heat.
     *          Each fluid class (e.g., Starlight vs Gelid Cryotheum) is balanced with specific HU/mB ratios.</li>
     * </ul>
     */
    private void handleThermalLogic() {
        if (this.level == null || this.level.isClientSide())
            return;

        this.thermalTicker++;

        if (this.hasCreativeCatalyst) {
            this.temperature = 0;
            this.overloadTimer = -1;
            return;
        }

        if (this.productiveThermalTicks > 0) {
            this.productiveHeatRemainder += this.productiveThermalTicks;
            this.productiveThermalTicks = 0;
            while (this.productiveHeatRemainder >= 4) {
                final int generationAmount = (int) Math.max(0, Math.round(this.currentHeatMultiplier));
                this.temperature += generationAmount;
                this.productiveHeatRemainder -= 4;
            }
        } else {
            if (!this.isWorking() && this.temperature > 0 && this.thermalTicker % 40 == 0) {
                this.temperature -= 1;
            }
        }

        if (this.temperature > 0) {
            final GenericStack coolantStack = this.fluidInv.getStack(2); // Input Coolant tank
            if (coolantStack != null && coolantStack.what() instanceof final AEFluidKey fluidKey
                    && coolantStack.amount() > 0) {
                final String fluidId = net.minecraft.core.registries.BuiltInRegistries.FLUID.getKey(fluidKey.getFluid())
                        .toString();
                int mBPerHeat = 0;
                int heatPerMB = 0;

                if (fluidId.contains("temporal")) {
                    heatPerMB = 100; // extreme endgame coolant
                } else if (fluidId.contains("stable_coolant")) {
                    heatPerMB = 50; // intended mid-tier progression coolant
                } else if (fluidId.contains("starlight")) {
                    heatPerMB = 30; // good utility coolant, but below stable coolant
                } else if (fluidId.contains("gelid_cryotheum")) {
                    mBPerHeat = 24; // starter coolant should sustain a single basic DMA
                } else {
                    heatPerMB = 15; // default fallback for generic fluids
                }

                long amountToConsume = 0;
                long heatCooled = 0;

                if (mBPerHeat > 0) {
                    amountToConsume = Math.min(coolantStack.amount(), 1000); // max 1 bucket per tick
                    final long possibleHeat = amountToConsume / mBPerHeat;
                    heatCooled = Math.min(this.temperature, possibleHeat);
                    amountToConsume = heatCooled * mBPerHeat;
                } else if (heatPerMB > 0) {
                    amountToConsume = Math.min(10, coolantStack.amount());
                    final long possibleHeat = amountToConsume * heatPerMB;
                    if (this.temperature < possibleHeat) {
                        amountToConsume = Math.max(1, (this.temperature / heatPerMB));
                    }
                    heatCooled = amountToConsume * heatPerMB;
                }

                if (amountToConsume > 0) {
                    this.fluidInv.extractInternal(2, fluidKey, amountToConsume, Actionable.MODULATE);
                    this.temperature -= (int) heatCooled;
                }
            }
        }

        if (this.temperature < 0)
            this.temperature = 0;

        if (this.isWorking() && this.level.getGameTime() % 40 == 0 && this.temperature > 0) {
            this.level.playSound(null, this.worldPosition, com.raishxn.ufo.init.ModSounds.DMA_WORK.get(),
                    net.minecraft.sounds.SoundSource.BLOCKS, 0.3f, 1.0f);
        }

        final double heatRatio = (double) this.temperature / Math.max(1, this.maxTemperature);
        if (heatRatio >= 0.5) { // Threshold reduced to 50%
            if (this.level instanceof final ServerLevel sLevel) {
                final double baseTime = sLevel.getGameTime() / 10.0;
                final double[] radii = { 6.0, 7.0, 8.0 };
                final double[] speeds = { 1.5, 1.0, 0.5 };

                for (int ring = 0; ring < 3; ring++) {
                    final double time = baseTime * speeds[ring];
                    final double r = radii[ring];
                    for (int i = 0; i < 12; i++) { // 6 particles per ring (total 18)
                        final double angle = time + (i * ((Math.PI * 2) / 6));
                        final double px = this.worldPosition.getX() + 0.5 + r * Math.cos(angle);
                        final double py = this.worldPosition.getY() + 0.5;
                        final double pz = this.worldPosition.getZ() + 0.5 + r * Math.sin(angle);
                        sLevel.sendParticles(net.minecraft.core.particles.ParticleTypes.FLAME, px, py, pz, 1, 0, 0, 0,
                                0.0);
                    }
                }
            }

            final net.minecraft.world.phys.AABB hazardArea = new net.minecraft.world.phys.AABB(this.worldPosition).inflate(7);
            final List<Player> players = this.level.getEntitiesOfClass(Player.class, hazardArea);
            for (final Player player : players) {
                if (this.level.getGameTime() % 20 == 0) {
                    if (!com.raishxn.ufo.event.HazardHandler.hasThermalProtection(player)) {
                        EntityDamageHelper.hurt(player, this.level.damageSources().onFire(), 4.0F);
                        player.setRemainingFireTicks(60);
                    }
                }
            }
        }

        if (this.temperature >= this.maxTemperature) {
            if (this.overloadTimer == -1) {
                this.overloadTimer = 100; // 5 seconds (20 ticks * 5)
            }
        } else {
            this.overloadTimer = -1; // clear
        }

        if (this.overloadTimer > 0) {
            if (this.overloadTimer % 20 == 0) {
                this.level.playSound(null, this.worldPosition, com.raishxn.ufo.init.ModSounds.DMA_ALARM.get(),
                        net.minecraft.sounds.SoundSource.BLOCKS, 0.6f, 1.0f);

                final int seconds = this.overloadTimer / 20;
                final net.minecraft.world.phys.AABB hazardArea = new net.minecraft.world.phys.AABB(this.worldPosition)
                        .inflate(15);
                for (final Player player : this.level.getEntitiesOfClass(Player.class, hazardArea)) {
                    player.sendOverlayMessage(
                            net.minecraft.network.chat.Component
                                    .literal("CRITICAL OVERLOAD IN " + seconds + " SECONDS!")
                                    .withStyle(net.minecraft.ChatFormatting.RED, net.minecraft.ChatFormatting.BOLD)
                    );
                }
            }
            this.overloadTimer--;

            if (this.overloadTimer == 0) {
                final net.minecraft.server.MinecraftServer server = this.level.getServer();
                if (server != null) {
                    final java.lang.String msg = "[⚠ THERMAL ALERT] Dimensional Matter Assembler exploded catastrophically at [X: "
                            +
                            this.worldPosition.getX() + ", Y: " + this.worldPosition.getY() + ", Z: " +
                            this.worldPosition.getZ() + "]!";
                    server.getPlayerList().broadcastSystemMessage(
                            net.minecraft.network.chat.Component.literal(msg).withStyle(
                                    net.minecraft.ChatFormatting.DARK_RED, net.minecraft.ChatFormatting.BOLD),
                            false);
                }

                this.level.explode(null, this.worldPosition.getX(), this.worldPosition.getY(),
                        this.worldPosition.getZ(),
                        10.0f, net.minecraft.world.level.Level.ExplosionInteraction.BLOCK); // Powerful block breaking
                removeBlockAfterCatastrophicExplosion();
                this.overloadTimer = -1;
            }
        }
    }

    private void removeBlockAfterCatastrophicExplosion() {
        if (this.level == null || this.level.getBlockEntity(this.worldPosition) != this) {
            return;
        }

        this.level.removeBlock(this.worldPosition, false);
    }

    private void updateBlockState(final boolean working) {
        if (this.level == null || this.notLoaded() || this.isRemoved()) {
            return;
        }

        final BlockState current = this.level.getBlockState(this.worldPosition);
        if (current.getBlock() instanceof DimensionalMatterAssemblerBlock) {
            final BlockState newState = current.setValue(DimensionalMatterAssemblerBlock.WORKING, working);

            if (current != newState) {
                this.level.setBlock(this.worldPosition, newState, Block.UPDATE_CLIENTS);
            }
        }
    }

    public int getMaxProcessingTime() {
        return this.cachedTask != null ? this.cachedTask.time() : 200;
    }

    public int getProcessingTime() {
        return this.processingTime;
    }

    private void setProcessingTime(final int processingTime) {
        this.processingTime = processingTime;
    }

    @Override
    protected void saveVisualState(final ValueOutput data) {
        super.saveVisualState(data);
        data.putBoolean("working", isWorking());
        data.putInt("temperature", this.temperature);
        data.putInt("maxTemperature", this.maxTemperature);
        data.putInt("overloadTimer", this.overloadTimer);
    }

    @Override
    protected void loadVisualState(final ValueInput data) {
        super.loadVisualState(data);
        this.working = data.getBooleanOr("working", false);
        this.temperature = data.getIntOr("temperature", this.temperature);
        this.maxTemperature = data.getIntOr("maxTemperature", this.maxTemperature);
        this.overloadTimer = data.getIntOr("overloadTimer", this.overloadTimer);
    }

    public void saveChanges() {
        recalculateUpgrades();
        this.setChanged();
    }

    private void persistChangesQuietly() {
        this.setChanged();
    }

    private double currentHeatMultiplier = 1.0;
    private double currentSpeedMultiplier = 1.0;
    private double currentPowerMultiplier = 1.0;
    private double currentBonusDropChance = 0.0;
    private boolean hasCreativeCatalyst = false;

    private void recalculateUpgrades() {
        long newMaxPower = MAX_POWER_STORAGE;
        double heatMult = 1.0;
        double speedMult = 1.0;
        double powerMult = 1.0;
        double bonusDropChance = 0.0;

        int identicalCount = 0;
        com.raishxn.ufo.item.custom.BaseCatalystItem firstCatalyst = null;
        boolean synergyPossible = true;

        boolean foundCreative = false;

        for (int i = 0; i < this.upgrades.size(); i++) {
            final ItemStack upgradeStack = this.upgrades.getStackInSlot(i);
            if (!upgradeStack.isEmpty()) {
                if (upgradeStack.getItem() instanceof com.raishxn.ufo.item.custom.DimensionalCatalystItem) {
                    foundCreative = true;
                } else if (upgradeStack.getItem() instanceof final com.raishxn.ufo.item.custom.BaseCatalystItem catalyst) {
                    newMaxPower *= catalyst.getBufferMultiplier();

                    heatMult += Math.max(0, catalyst.getStaticHeat() / 100.0);
                    speedMult *= catalyst.getSpeedMultiplier();
                    powerMult *= catalyst.getPowerMultiplier();
                    bonusDropChance += catalyst.getBonusDropChance();

                    if (firstCatalyst == null) {
                        firstCatalyst = catalyst;
                        identicalCount++;
                    } else if (firstCatalyst == catalyst) {
                        identicalCount++;
                    } else {
                        synergyPossible = false;
                    }
                } else {
                    synergyPossible = false; // Missing or non-catalyst prevents synergy
                }
            } else {
                synergyPossible = false;
            }
        }

        if (synergyPossible && identicalCount == 4) {
            heatMult *= 1.5; // 50% more heat generation as debuff
            if ("chrono".equals(firstCatalyst.getFamily())) {
                speedMult *= 2.0; // Huge speed synergy
            } else if ("matterflow".equals(firstCatalyst.getFamily())) {
                powerMult *= 0.5; // Huge power discount
            } else if ("quantum".equals(firstCatalyst.getFamily())) {
                bonusDropChance += 0.5;
            }
        }

        if (foundCreative) {
             heatMult = 0.0;
             speedMult = 1000.0;
             powerMult = 0.0;
             bonusDropChance = 1.0;
             newMaxPower = MAX_POWER_STORAGE;
        }

        this.hasCreativeCatalyst = foundCreative;
        this.currentHeatMultiplier = heatMult;
        this.currentSpeedMultiplier = speedMult;
        this.currentPowerMultiplier = powerMult;
        this.currentBonusDropChance = bonusDropChance;
        this.localMaxPower = (int) Math.min(Integer.MAX_VALUE, newMaxPower);
        this.maxTemperature = 10000; // Reset max capacity; heat is now a multiplier payload
        this.setInternalMaxPower(this.localMaxPower);
    }

    @Override
    public InternalInventory getInternalInventory() {
        return this.inv;
    }

    public InternalInventory getInput() {
        return this.inputInv;
    }

    public InternalInventory getOutput() {
        return this.outputInv;
    }

    public GenericStackInv getTank() {
        return this.fluidInv;
    }

    public void setShowWarning(final boolean show) {
        this.showWarning = show;
    }

    public boolean showWarning() {
        return this.showWarning;
    }

    @Override
    public EnumSet<RelativeSide> getAllowedOutputs() {
        return this.allowedOutputs;
    }

    @Nullable
    @Override
    public InternalInventory getSubInventory(final Identifier id) {
        if (id.equals(ISegmentedInventory.STORAGE)) {
            return this.getInternalInventory();
        } else if (id.equals(ISegmentedInventory.UPGRADES)) {
            return this.upgrades;
        }
        return super.getSubInventory(id);
    }

    @Override
    protected InternalInventory getExposedInventoryForSide(final Direction facing) {
        return this.invExposed;
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return upgrades;
    }

    private void onChangeInventory() {
        this.dirty = true;
        getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
    }

    public void onChangeInventory(final AppEngInternalInventory inv, final int slot) {
        onChangeInventory();
    }

    public void onChangeTank() {
        onChangeInventory();
        if (this.level != null && !this.level.isClientSide()) {
            this.markForUpdate();
        }
    }

    private boolean hasAutoExportWork() {
        return (!this.outputInv.getStackInSlot(0).isEmpty() || !this.outputInv.getStackInSlot(1).isEmpty()
                || this.fluidInv.getStack(0) != null
                || this.fluidInv.getAmount(0) > 0
                || this.fluidInv.getAmount(1) > 0)
                && configManager.getSetting(Settings.AUTO_EXPORT) == YesNo.YES;
    }

    private boolean hasCraftWork() {
        final var task = this.getTask();
        if (task != null) {
            boolean outputsFit = true;
            for (int i = 0; i < task.itemOutputs().size(); i++) {
                final var outStack = task.itemOutputs().get(i);
                if (outStack.what() instanceof final AEItemKey itemKey) {
                    final int outputAmount = getOutputAmountWithMaxBonus(outStack.amount());
                    final var stack = itemKey.toStack(outputAmount);
                    if (!this.outputInv.insertItem(i, stack, true).isEmpty()) {
                        outputsFit = false;
                        break;
                    }
                }
            }
            if (!outputsFit)
                return false;

            for (int i = 0; i < task.fluidOutputs().size(); i++) {
                final var outStack = task.fluidOutputs().get(i);
                if (outStack.what() instanceof final AEFluidKey fluidKey) {
                    final int outputAmount = getOutputAmountWithMaxBonus(outStack.amount());
                    if (this.fluidInv.cantAdd(i, fluidKey, outputAmount)) {
                        return false;
                    }
                }
            }
            return true;
        }

        this.setProcessingTime(0);
        return this.isWorking();
    }

    @Nullable
    public DimensionalMatterAssemblerRecipe getTask() {
        if (this.cachedTask == null && level != null) {
            this.cachedTask = findRecipe(level);
        }
        return this.cachedTask;
    }

    private int getOutputAmountWithMaxBonus(final long baseAmount) {
        final long multiplier = 1L + (long) Math.ceil(Math.max(0.0, this.currentBonusDropChance));
        return (int) Math.min(Integer.MAX_VALUE, Math.max(0L, baseAmount) * multiplier);
    }

    private int getOutputAmountWithRolledBonus(final long baseAmount) {
        final long safeBaseAmount = Math.max(0L, baseAmount);
        final double bonusChance = Math.max(0.0, this.currentBonusDropChance);
        final long guaranteedBonusRolls = (long) bonusChance;
        final double fractionalBonusRoll = bonusChance - guaranteedBonusRolls;
        long bonusRolls = guaranteedBonusRolls;

        if (fractionalBonusRoll > 0.0 && this.level != null && this.level.getRandom().nextDouble() < fractionalBonusRoll) {
            bonusRolls++;
        }

        return (int) Math.min(Integer.MAX_VALUE, safeBaseAmount * (1L + bonusRolls));
    }

    private static List<RecipeHolder<DimensionalMatterAssemblerRecipe>> getSortedRecipes(final Level level) {
        if (!(level instanceof final ServerLevel serverLevel)) {
            return List.of();
        }
        final RecipeManager manager = serverLevel.recipeAccess();
        if (manager != cachedRecipeManagerRef || sortedRecipeCache == null) {
            sortedRecipeCache = manager.recipeMap().byType(ModRecipes.DMA_RECIPE_TYPE.get()).stream()
                    .sorted(Comparator
                            .comparingInt((RecipeHolder<DimensionalMatterAssemblerRecipe> holder) ->
                                    (int) holder.value().itemInputs().stream()
                                            .filter(r -> r != null && !r.isEmpty()).count())
                            .thenComparingLong((RecipeHolder<DimensionalMatterAssemblerRecipe> holder) ->
                                    holder.value().fluidInputs().stream()
                                            .filter(f -> f != null && !f.isEmpty())
                                            .mapToLong(IngredientStack::getAmount).sum())
                            .reversed())
                    .toList();
            cachedRecipeManagerRef = manager;
        }
        return sortedRecipeCache;
    }

    private DimensionalMatterAssemblerRecipe findRecipe(final Level level) {
        final var possibleRecipes = getSortedRecipes(level);
        for (final var recipeHolder : possibleRecipes) {
            final var recipe = recipeHolder.value();
            boolean matches = true;

            final List<ItemStack> availableInputs = new java.util.ArrayList<>();
            for (int i = 0; i < this.inputInv.size(); i++) {
                final var stack = this.inputInv.getStackInSlot(i);
                if (!stack.isEmpty())
                    availableInputs.add(stack.copy());
            }

            for (final var req : recipe.itemInputs()) {
                if (req == null || req.isEmpty())
                    continue;
                int amountNeeded = req.getAmount();
                for (final var stack : availableInputs) {
                    if (req.getIngredient().test(stack)) {
                        final int toTake = Math.min(stack.getCount(), amountNeeded);
                        stack.shrink(toTake);
                        amountNeeded -= toTake;
                    }
                    if (amountNeeded <= 0)
                        break;
                }
                if (amountNeeded > 0) {
                    matches = false;
                    break;
                }
            }

            if (!matches)
                continue;

            for (int i = 0; i < recipe.fluidInputs().size(); i++) {
                final var fluidInSlot = this.fluidInv.getStack(3); // Always use slot 3 (base fluid input)
                if (recipe.fluidInputs().get(i) != null && !recipe.fluidInputs().get(i).isEmpty()) {
                    if (fluidInSlot == null || fluidInSlot.amount() < recipe.fluidInputs().get(i).getAmount()) {
                        matches = false;
                        break;
                    }
                    if (!(fluidInSlot.what() instanceof final AEFluidKey fluidKey)) {
                        matches = false;
                        break;
                    }
                    final FluidStack fluidStack = fluidKey.toStack((int) fluidInSlot.amount());
                    if (!recipe.fluidInputs().get(i).getIngredient().test(fluidStack)) {
                        matches = false;
                        break;
                    }
                }
            }

            if (matches)
                return recipe;
        }
        return null;
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode iGridNode) {
        return new TickingRequest(1, 20, !hasAutoExportWork() && !this.hasCraftWork());
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode iGridNode, final int ticksSinceLastCall) {
        if (this.dirty) {
            if (level != null) {
                final var recipe = findRecipe(level);
                if (recipe == null) {
                    this.setProcessingTime(0);
                    this.setWorking(false);
                    this.cachedTask = null;
                } else if (recipe != this.cachedTask) {
                    this.setProcessingTime(0);
                    this.cachedTask = recipe;
                }
            }
            this.setChanged();
            this.dirty = false;
        }

        if (this.hasCraftWork()) {
            final boolean[] didWork = { false };
            getMainNode().ifPresent(grid -> {
                final IEnergyService eg = grid.getEnergyService();
                IEnergySource src = this;

                final int baseSpeedFactor = switch (this.upgrades.getInstalledUpgrades(AEItems.SPEED_CARD)) {
                    case 1 -> 3; // 66 ticks
                    case 2 -> 5; // 40 ticks
                    case 3 -> 10; // 20 ticks
                    case 4 -> 50; // 4 ticks
                    default -> 2; // 100 ticks
                };

                final int recipeTime = this.cachedTask != null ? this.cachedTask.time() : 200;

                final int speedFactor = this.hasCreativeCatalyst
                        ? Math.max(1, recipeTime)
                        : Math.min(Math.max(1, recipeTime), Math.max(1, Mth.ceil((float) (baseSpeedFactor * this.currentSpeedMultiplier))));
                final int progressReq = recipeTime - this.getProcessingTime();
                final float powerRatio = progressReq < speedFactor ? (float) progressReq / speedFactor : 1;
                final int requiredTicks = Mth.ceil((float) recipeTime / speedFactor);

                final int basePowerConsumption = Mth.floor(((float) Objects.requireNonNull(getTask()).energy() / requiredTicks) * powerRatio);
                final int powerConsumption = this.hasCreativeCatalyst
                        ? 0
                        : Math.max(1, (int) (basePowerConsumption * this.currentPowerMultiplier));
                final double powerThreshold = powerConsumption - 0.01;

                if (this.hasCreativeCatalyst) {
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                    setShowWarning(false);
                    didWork[0] = true;
                    return;
                }

                double powerReq = this.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);

                if (powerReq <= powerThreshold) {
                    src = eg;
                    final var oldPowerReq = powerReq;
                    powerReq = eg.extractAEPower(powerConsumption, Actionable.SIMULATE, PowerMultiplier.CONFIG);
                    if (oldPowerReq > powerReq) {
                        src = this;
                        powerReq = oldPowerReq;
                    }
                }

                if (powerReq > powerThreshold) {
                    src.extractAEPower(powerConsumption, Actionable.MODULATE, PowerMultiplier.CONFIG);
                    this.setProcessingTime(this.getProcessingTime() + speedFactor);
                    setShowWarning(false);
                    didWork[0] = true;
                } else if (powerReq != 0) {
                    final var progressRatio = src == this
                            ? powerReq / powerConsumption
                            : (powerReq - 10 * eg.getIdlePowerUsage()) / powerConsumption;
                    final var factor = Mth.floor(progressRatio * speedFactor);

                    if (factor >= 1) {
                        final var extracted = src.extractAEPower(
                                (double) (powerConsumption * factor) / speedFactor,
                                Actionable.MODULATE,
                                PowerMultiplier.CONFIG);
                        final var actualFactor = (int) Math.floor(extracted / powerConsumption * speedFactor);
                        this.setProcessingTime(this.getProcessingTime() + actualFactor);
                        didWork[0] = true;
                    }
                    setShowWarning(true);
                }
            });
            this.setWorking(didWork[0]);
            if (didWork[0]) {
                this.productiveThermalTicks++;
            }

            if (this.getProcessingTime() >= this.getMaxProcessingTime()) {
                this.setProcessingTime(0);
                final DimensionalMatterAssemblerRecipe out = this.getTask();
                if (out != null) {

                    for (int i = 0; i < out.itemOutputs().size(); i++) {
                        if (out.itemOutputs().get(i) != null
                                && out.itemOutputs().get(i).what() instanceof final AEItemKey itemKey) {
                            final int outAmount = getOutputAmountWithRolledBonus(out.itemOutputs().get(i).amount());
                            final var toIns = itemKey.toStack(outAmount);
                            this.outputInv.insertItem(i, toIns, false);
                        }
                    }

                    for (int i = 0; i < out.fluidOutputs().size(); i++) {
                        if (out.fluidOutputs().get(i) != null
                                && out.fluidOutputs().get(i).what() instanceof final AEFluidKey fluidKey) {
                            final int outAmount = getOutputAmountWithRolledBonus(out.fluidOutputs().get(i).amount());
                            this.fluidInv.add(i, fluidKey, outAmount);
                        }
                    }

                    for (final var req : out.itemInputs()) {
                        if (req != null && !req.isEmpty()) {
                            int amountNeeded = req.getAmount();
                            for (int i = 0; i < this.inputInv.size() && amountNeeded > 0; i++) {
                                final var currentStack = this.inputInv.getStackInSlot(i);
                                if (req.getIngredient().test(currentStack)) {
                                    final int toTake = Math.min(currentStack.getCount(), amountNeeded);
                                    currentStack.shrink(toTake);
                                    this.inputInv.setItemDirect(i, currentStack);
                                    amountNeeded -= toTake;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < out.fluidInputs().size(); i++) {
                        if (out.fluidInputs().get(i) != null && !out.fluidInputs().get(i).isEmpty()) {
                            final var currentStack = this.fluidInv.getStack(3); // Always slot 3 (base fluid)
                            if (currentStack != null) {
                                final var key = currentStack.what();
                                final long remaining = currentStack.amount() - out.fluidInputs().get(i).getAmount();
                                if (remaining > 0) {
                                    this.fluidInv.setStack(3, new GenericStack(key, remaining));
                                } else {
                                    this.fluidInv.setStack(3, null);
                                }
                            }
                        }
                    }
                }
                this.persistChangesQuietly();
                this.cachedTask = null;
                this.setWorking(false);
            }
        } else {
            setShowWarning(false);
        }

        if (this.pushOutResult()) {
            return TickRateModulation.URGENT;
        }

        return this.hasCraftWork()
                ? TickRateModulation.URGENT
                : this.hasAutoExportWork() ? TickRateModulation.SLOWER : TickRateModulation.SLEEP;
    }

    private boolean pushOutResult() {
        if (!this.hasAutoExportWork()) {
            return false;
        }

        final var orientation = this.getOrientation();

        for (final var side : allowedOutputs) {
            final var dir = orientation.getSide(side);
            final var target = getTarget(dir);

            if (target != null) {
                final var source = IActionSource.ofMachine(this);
                var movedStacks = false;

                for (int i = 0; i < this.outputInv.size(); i++) {
                    final var genStack = GenericStack.fromItemStack(this.outputInv.getStackInSlot(i));
                    if (genStack != null && genStack.what() != null) {
                        final var extractedStack = this.outputInv.extractItem(i, 64, false);
                        final var inserted = target.insert(genStack.what(), extractedStack.getCount(), Actionable.MODULATE,
                                source);
                        extractedStack.setCount(extractedStack.getCount() - (int) inserted);
                        this.outputInv.insertItem(i, extractedStack, false);
                        movedStacks |= inserted > 0;
                    }
                }

                for (int i = 0; i < 2; i++) {
                    final var outFluid = this.fluidInv.getStack(i);
                    if (outFluid != null && outFluid.what() != null) {
                        final var extracted = this.fluidInv.extract(i, outFluid.what(), outFluid.amount(),
                                Actionable.MODULATE);
                        final var inserted = target.insert(outFluid.what(), extracted, Actionable.MODULATE, source);
                        this.fluidInv.add(i, ((AEFluidKey) outFluid.what()), (int) (extracted - inserted));

                        if (this.fluidInv.getAmount(i) == 0)
                            this.fluidInv.clear(i);
                        movedStacks |= inserted > 0;
                    }
                }

                if (movedStacks) {
                    return true;
                }
            }
        }

        return false;
    }

    private CompositeStorage getTarget(final Direction dir) {
        if (this.exportStrategies.get(dir) == null) {
            final var be = this.getBlockEntity();
            this.exportStrategies.put(
                    dir,
                    StackWorldBehaviors.createExternalStorageStrategies(
                            (ServerLevel) be.getLevel(), be.getBlockPos().relative(dir), dir.getOpposite()));
        }

        final var externalStorages = new IdentityHashMap<AEKeyType, MEStorage>(2);
        for (final var entry : exportStrategies.get(dir).entrySet()) {
            final var wrapper = entry.getValue().createWrapper(false, () -> {
            });
            if (wrapper != null) {
                externalStorages.put(entry.getKey(), wrapper);
            }
        }

        if (!externalStorages.isEmpty()) {
            return new CompositeStorage(externalStorages);
        }
        return null;
    }

    @Override
    public IConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public AECableType getCableConnectionType(final Direction dir) {
        return AECableType.COVERED;
    }

    @Override
    public void saveAdditional(final ValueOutput data) {
        super.saveAdditional(data);
        this.fluidInv.writeToChildTag(data, "tank");

        final var outputTags = data.list("outputs", Codec.STRING);
        for (final var side : this.allowedOutputs) {
            outputTags.add(side.name());
        }

        this.upgrades.writeToNBT(data, "upgrades");
        this.configManager.writeToNBT(data);

        data.putInt("temperature", this.temperature);
        data.putInt("overloadTimer", this.overloadTimer);
        data.putInt("productiveHeatRemainder", this.productiveHeatRemainder);
    }

    @Override
    public void loadTag(final ValueInput data) {
        super.loadTag(data);
        this.fluidInv.readFromChildTag(data, "tank");

        this.allowedOutputs.clear();
        final var outputTags = data.listOrEmpty("outputs", Codec.STRING);
        if (!outputTags.isEmpty()) {
            for (final String outputTag : outputTags) {
                final RelativeSide side = Enum.valueOf(RelativeSide.class, outputTag);
                this.allowedOutputs.add(side);
            }
        }

        this.upgrades.readFromNBT(data, "upgrades");
        this.configManager.readFromNBT(data);

        this.temperature = data.getIntOr("temperature", this.temperature);
        this.overloadTimer = data.getIntOr("overloadTimer", this.overloadTimer);
        this.productiveHeatRemainder = data.getIntOr("productiveHeatRemainder", this.productiveHeatRemainder);

        recalculateUpgrades();
    }

    @Override
    protected boolean readFromStream(final RegistryFriendlyByteBuf data) {
        final var c = super.readFromStream(data);

        final var oldWorking = isWorking();
        final var newWorking = data.readBoolean();

        if (oldWorking != newWorking) {
            this.working = newWorking;
        }

        for (int i = 0; i < this.inv.size(); i++) {
            this.inv.setItemDirect(i, ItemStack.OPTIONAL_STREAM_CODEC.decode(data));
        }

        this.fluidInv.setStack(0, GenericStack.readBuffer(data));
        this.fluidInv.setStack(1, GenericStack.readBuffer(data));
        this.fluidInv.setStack(2, GenericStack.readBuffer(data));
        this.fluidInv.setStack(3, GenericStack.readBuffer(data));
        this.cachedTask = null;

        this.temperature = data.readInt();
        this.maxTemperature = data.readInt();
        this.overloadTimer = data.readInt();

        return c;
    }

    @Override
    protected void writeToStream(final RegistryFriendlyByteBuf data) {
        super.writeToStream(data);

        data.writeBoolean(isWorking());
        for (int i = 0; i < this.inv.size(); i++) {
            ItemStack.OPTIONAL_STREAM_CODEC.encode(data, this.inv.getStackInSlot(i));
        }

        GenericStack.writeBuffer(this.fluidInv.getStack(0), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(1), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(2), data);
        GenericStack.writeBuffer(this.fluidInv.getStack(3), data);

        data.writeInt(this.temperature);
        data.writeInt(this.maxTemperature);
        data.writeInt(this.overloadTimer);
    }

    @Override
    public void exportSettings(final SettingsFrom mode, final DataComponentMap.Builder builder, @Nullable final Player player) {
        super.exportSettings(mode, builder, player);
        builder.set(ModDataComponents.DMA_ALLOWED_OUTPUTS.get(), encodeAllowedOutputs());
    }

    @Override
    public void importSettings(final SettingsFrom mode, final DataComponentMap input, @Nullable final Player player) {
        super.importSettings(mode, input, player);
        final Integer exportedOutputs = input.get(ModDataComponents.DMA_ALLOWED_OUTPUTS.get());
        if (exportedOutputs != null) {
            updateOutputSides(decodeAllowedOutputs(exportedOutputs));
        }
    }

    private int encodeAllowedOutputs() {
        int mask = 0;
        for (final RelativeSide side : this.allowedOutputs) {
            mask |= 1 << side.ordinal();
        }
        return mask;
    }

    private static EnumSet<RelativeSide> decodeAllowedOutputs(final int mask) {
        final EnumSet<RelativeSide> outputs = EnumSet.noneOf(RelativeSide.class);
        for (final RelativeSide side : RelativeSide.values()) {
            if ((mask & (1 << side.ordinal())) != 0) {
                outputs.add(side);
            }
        }
        return outputs;
    }

    private void onConfigChanged(final IConfigManager manager, final Setting<?> setting) {
        if (setting == Settings.AUTO_EXPORT) {
            getMainNode().ifPresent((grid, node) -> grid.getTickManager().wakeDevice(node));
        }
        saveChanges();
    }

    @Override
    public void addAdditionalDrops(final Level level, final BlockPos pos, final List<ItemStack> drops) {
        super.addAdditionalDrops(level, pos, drops);

        for (final var upgrade : upgrades) {
            drops.add(upgrade);
        }

        for (var i = 0; i < this.fluidInv.size(); i++) {
            final var fluid = this.fluidInv.getStack(i);
            if (fluid != null) {
                fluid.what().addDrops(fluid.amount(), drops, level, pos);
            }
        }
    }

    @Override
    public void clearContent() {
        super.clearContent();
        this.fluidInv.clear();
        this.upgrades.clear();
    }

    /**
     * Clears a specific tank slot. Used by GUI clear buttons.
     */
    public void clearTank(final int slot) {
        if (slot >= 0 && slot < this.fluidInv.size()) {
            this.fluidInv.setStack(slot, null);
            this.onChangeTank();
            this.saveChanges();
        }
    }

    @Override
    public void updateOutputSides(final EnumSet<RelativeSide> allowedOutputs) {
        this.allowedOutputs = allowedOutputs;
        saveChanges();
    }

    @Override
    public void returnToMainMenu(final Player player, final ISubMenu iSubMenu) {
        appeng.menu.MenuOpener.returnTo(com.raishxn.ufo.menu.UFOMenus.DIMENSIONAL_MATTER_ASSEMBLER.get(), player,
                iSubMenu.getLocator());
    }

    @Override
    public ItemStack getMainMenuIcon() {
        return new ItemStack(this.getBlockState().getBlock().asItem());
    }

    public static class CustomGenericInv extends GenericStackInv {
        public CustomGenericInv(final Set<AEKeyType> supportedKeyTypes, @Nullable final Runnable listener, final Mode mode, final int size) {
            super(supportedKeyTypes, listener, mode, size);
        }

        @SuppressWarnings("deprecation")
        @Override
        public boolean isAllowedIn(final int slot, final AEKey what) {
            if (slot == 0 || slot == 1)
                return false;

            if (what instanceof final AEFluidKey fluidKey) {
                final boolean isCoolant = fluidKey.getFluid().builtInRegistryHolder()
                        .is(com.raishxn.ufo.util.ModTags.Fluids.COOLANT)
                        || fluidKey.getFluid().builtInRegistryHolder().is(com.raishxn.ufo.util.ModTags.Fluids.COOLANTS);

                if (slot == 2 && !isCoolant) {
                    return false;
                }

                if (slot == 3 && isCoolant) {
                    return false;
                }
            }

            return super.isAllowedIn(slot, what);
        }

        /**
         * Custom insertion logic for the DMA's fluid tanks.
         * Default GenericStackInv merges all identical fluids into the first matching slot.
         * The DMA uses slot 2 for coolants and slot 3 for base recipe fluids, so they MUST be kept separate.
         * This code dynamically routes the fluid into the correct slot depending on its type while still
         * properly tracking merging / remainder math when a slot is partially full.
         */
        @Override
        public long insert(final AEKey what, final long amount, final Actionable mode, final IActionSource source) {
            if (!(what instanceof AEFluidKey)) {
                return super.insert(what, amount, mode, source);
            }

            long remaining = amount;

            for (int slot = 2; slot <= 3 && remaining > 0; slot++) {
                if (!isAllowedIn(slot, what))
                    continue;

                final var stack = this.getStack(slot);
                if (stack == null) {
                    final long toInsert = Math.min(remaining, this.getMaxAmount(what));
                    if (mode == Actionable.MODULATE) {
                        this.setStack(slot, new GenericStack(what, toInsert));
                    }
                    remaining -= toInsert;
                } else if (stack.what().equals(what)) {
                    final long space = this.getMaxAmount(what) - stack.amount();
                    if (space > 0) {
                        final long toInsert = Math.min(remaining, space);
                        if (mode == Actionable.MODULATE) {
                            this.setStack(slot, new GenericStack(what, stack.amount() + toInsert));
                        }
                        remaining -= toInsert;
                    }
                }
            }

            return amount - remaining;
        }

        @Override
        public long extract(final int slot, final AEKey what, final long amount, final Actionable mode) {
            if (slot == 2 || slot == 3)
                return 0L;
            return super.extract(slot, what, amount, mode);
        }

        /**
         * Internal extraction that bypasses the external-only protection.
         * Used by the machine's own thermal logic to consume coolant.
         */
        public void extractInternal(final int slot, final AEKey what, final long amount, final Actionable mode) {
            super.extract(slot, what, amount, mode);
        }

        public boolean cantAdd(final int slot, final AEFluidKey key, final int amount) {
            final var stack = this.getStack(slot);
            if (stack == null)
                return false;
            if (!stack.what().equals(key))
                return true;
            return stack.amount() + amount > this.getMaxAmount(key);
        }

        public int add(final int slot, final AEFluidKey key, final int amount) {
            if (cantAdd(slot, key, amount))
                return 0;

            final var stack = this.getStack(slot);
            var newAmount = amount;
            if (stack != null)
                newAmount += (int) stack.amount();
            this.setStack(slot, new GenericStack(key, newAmount));
            return amount;
        }

        public void clear(final int slot) {
            final boolean changed = this.getStack(slot) != null;
            this.setStack(slot, null);
            if (changed) {
                onChange();
            }
        }
    }
}
