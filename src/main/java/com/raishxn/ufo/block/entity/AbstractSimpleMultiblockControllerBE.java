package com.raishxn.ufo.block.entity;

import com.raishxn.ufo.api.multiblock.IMultiblockController;
import com.raishxn.ufo.api.multiblock.IMultiblockPart;
import com.raishxn.ufo.api.multiblock.MultiblockMachineTier;
import com.raishxn.ufo.api.multiblock.MultiblockControllerDefinitions;
import com.raishxn.ufo.api.multiblock.MultiblockPattern;
import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AbstractSimpleMultiblockControllerBE extends BlockEntity implements IMultiblockController, MenuProvider, IUniversalMultiblockController, IUpgradeableObject {
    private static final int PERIODIC_STRUCTURE_SCAN_TICKS = 200;

    protected boolean assembled = false;
    protected boolean structureDirty = true;
    protected int scanCooldown = 0;
    protected final List<BlockPos> parts = new ArrayList<>();
    protected boolean running = false;
    protected int progress = 0;
    protected int maxProgress = 0;
    protected int temperature = 0;
    protected int maxTemperature = 10000;
    protected int machineTier = MultiblockMachineTier.MK1.level();
    protected long storedEnergy = 0L;
    protected long maxStoredEnergy = 0L;
    protected boolean safeMode = true;
    protected boolean overclocked = false;
    protected final List<UniversalDisplayedRecipe> displayedRecipes = new ArrayList<>();
    protected final IUpgradeInventory upgrades;

    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(final int index) {
            return switch (index) {
                case 0 -> assembled ? 1 : 0;
                case 1 -> running ? 1 : 0;
                case 2 -> progress;
                case 3 -> maxProgress;
                case 4 -> temperature;
                case 5 -> maxTemperature;
                case 6 -> safeMode ? 1 : 0;
                case 7 -> overclocked ? 1 : 0;
                default -> 0;
            };
        }

        @Override
        public void set(final int index, final int value) {
            switch (index) {
                case 0 -> assembled = value == 1;
                case 1 -> running = value == 1;
                case 2 -> progress = value;
                case 3 -> maxProgress = value;
                case 4 -> temperature = value;
                case 5 -> maxTemperature = value;
                case 6 -> safeMode = value == 1;
                case 7 -> overclocked = value == 1;
                default -> {
                }
            }
        }

        @Override
        public int getCount() {
            return 8;
        }
    };

    protected AbstractSimpleMultiblockControllerBE(final BlockEntityType<?> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
        this.upgrades = UpgradeInventories.forMachine(state.getBlock().asItem(), 4, this::saveChanges);
    }

    protected abstract MultiblockPattern getControllerPattern();

    protected abstract String getControllerTranslationKey();

    public void serverTick() {
        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        if (this.structureDirty || --this.scanCooldown <= 0) {
            scanStructure(this.level);
            this.scanCooldown = PERIODIC_STRUCTURE_SCAN_TICKS;
            this.structureDirty = false;
        }

        machineTick();
    }

    protected void machineTick() {
        tickThermals();
    }

    protected void tickThermals() {
        rebuildDisplayedRecipes();
        if (this.running) {
            this.temperature = Math.min(this.maxTemperature, this.temperature + (this.overclocked ? 8 : 2));
            if (this.safeMode && this.temperature >= this.maxTemperature) {
                this.running = false;
                this.progress = 0;
            }
        } else if (this.temperature > 0) {
            this.temperature = Math.max(0, this.temperature - 2);
        }
    }

    @Override
    public boolean isAssembled() {
        return this.assembled;
    }

    @Override
    public void scanStructure(final Level level) {
        final BlockState state = level.getBlockState(this.worldPosition);
        final Direction facing = MultiblockControllerDefinitions.getPatternFacing(this, state);

        final MultiblockPattern.MatchResult result = getControllerPattern().match(level, this.worldPosition, facing);
        final boolean wasAssembled = this.assembled;
        this.assembled = result.isValid();

        for (final BlockPos existingPart : new ArrayList<>(this.parts)) {
            if (!result.partPositions().contains(existingPart)
                    && level.getBlockEntity(existingPart) instanceof final IMultiblockPart part) {
                part.unlinkFromController();
            }
        }

        this.parts.clear();
        if (this.assembled) {
            this.machineTier = resolveMachineTier(result);
            for (final BlockPos partPos : result.partPositions()) {
                if (!partPos.equals(this.worldPosition)) {
                    this.parts.add(partPos);
                    if (level.getBlockEntity(partPos) instanceof final IMultiblockPart part) {
                        part.linkToController(this.worldPosition);
                    }
                }
            }
        } else {
            this.running = false;
            this.progress = 0;
            this.maxProgress = 0;
            this.temperature = 0;
            this.machineTier = MultiblockMachineTier.MK1.level();
        }

        updateControllerBlockState(level.getBlockState(this.worldPosition), wasAssembled);
        this.setChanged();
    }

    private void updateControllerBlockState(final BlockState currentState, final boolean wasAssembled) {
        if (this.level == null || wasAssembled == this.assembled) {
            return;
        }

        for (final var property : currentState.getProperties()) {
            if (property instanceof final BooleanProperty booleanProperty && "active".equals(booleanProperty.getName())) {
                this.level.setBlock(this.worldPosition, currentState.setValue(booleanProperty, this.assembled), Block.UPDATE_CLIENTS);
                return;
            }
        }

        this.level.sendBlockUpdated(this.worldPosition, currentState, currentState, Block.UPDATE_ALL);
    }

    public void markStructureDirty() {
        this.structureDirty = true;
        this.scanCooldown = 0;
    }

    protected int resolveMachineTier(final MultiblockPattern.MatchResult result) {
        return MultiblockMachineTier.MK1.level();
    }

    protected boolean hasOngoingWork() {
        return this.running || this.progress > 0 || this.maxProgress > 0;
    }

    public void onControllerBroken() {
        if (this.level == null) {
            return;
        }

        for (final BlockPos partPos : this.parts) {
            if (this.level.getBlockEntity(partPos) instanceof final IMultiblockPart part) {
                part.unlinkFromController();
            }
        }

        this.parts.clear();
        this.assembled = false;
        this.running = false;
        this.progress = 0;
        this.maxProgress = 0;
        this.temperature = 0;
        this.machineTier = MultiblockMachineTier.MK1.level();
        this.setChanged();
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
        markStructureDirty();
    }

    @Override
    public List<BlockPos> getParts() {
        return Collections.unmodifiableList(this.parts);
    }

    @Override
    public BlockPos getControllerPos() {
        return this.worldPosition;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(getControllerTranslationKey());
    }

    @Override
    protected void saveAdditional(@NotNull final ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("assembled", this.assembled);
        output.putBoolean("running", this.running);
        output.putInt("progress", this.progress);
        output.putInt("maxProgress", this.maxProgress);
        output.putInt("temperature", this.temperature);
        output.putInt("maxTemperature", this.maxTemperature);
        output.putInt("machineTier", this.machineTier);
        output.putLong("storedEnergy", this.storedEnergy);
        output.putLong("maxStoredEnergy", this.maxStoredEnergy);
        output.putBoolean("safeMode", this.safeMode);
        output.putBoolean("overclocked", this.overclocked);
        this.upgrades.writeToNBT(output, "upgrades");
        final var partsList = output.list("parts", BlockPos.CODEC);
        this.parts.forEach(partsList::add);
        saveDisplayedRecipes(output);
    }

    @Override
    protected void loadAdditional(@NotNull final ValueInput input) {
        super.loadAdditional(input);
        this.assembled = input.getBooleanOr("assembled", false);
        this.running = input.getBooleanOr("running", false);
        this.progress = input.getIntOr("progress", 0);
        this.maxProgress = input.getIntOr("maxProgress", 0);
        this.temperature = input.getIntOr("temperature", 0);
        this.maxTemperature = input.getIntOr("maxTemperature", this.maxTemperature);
        this.machineTier = Math.max(MultiblockMachineTier.MK1.level(), input.getIntOr("machineTier", this.machineTier));
        this.storedEnergy = input.getLongOr("storedEnergy", 0L);
        this.maxStoredEnergy = input.getLongOr("maxStoredEnergy", 0L);
        this.safeMode = input.getBooleanOr("safeMode", true);
        this.overclocked = input.getBooleanOr("overclocked", false);

        this.parts.clear();
        input.listOrEmpty("parts", BlockPos.CODEC).forEach(this.parts::add);
        this.upgrades.readFromNBT(input, "upgrades");
        loadDisplayedRecipes(input);

        this.structureDirty = true;
        this.scanCooldown = 0;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void saveChanges() {
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public boolean isGuiAssembled() {
        return this.assembled;
    }

    @Override
    public boolean isGuiRunning() {
        return this.running;
    }

    @Override
    public int getGuiProgress() {
        return this.progress;
    }

    @Override
    public int getGuiMaxProgress() {
        return this.maxProgress;
    }

    @Override
    public int getGuiTemperature() {
        return this.temperature;
    }

    @Override
    public int getGuiMaxTemperature() {
        return this.maxTemperature;
    }

    @Override
    public int getGuiMachineTier() {
        return this.machineTier;
    }

    @Override
    public long getGuiStoredEnergy() {
        return this.storedEnergy;
    }

    @Override
    public long getGuiMaxEnergy() {
        return this.maxStoredEnergy;
    }

    @Override
    public int getGuiActiveParallels() {
        return this.running ? 1 : 0;
    }

    @Override
    public int getGuiMaxParallels() {
        return 1;
    }

    @Override
    public boolean isGuiSafeMode() {
        return this.safeMode;
    }

    @Override
    public boolean isGuiOverclocked() {
        return this.overclocked;
    }

    @Override
    public void toggleSafeMode() {
        if (hasOngoingWork()) {
            return;
        }
        this.safeMode = !this.safeMode;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public void toggleOverclock() {
        if (hasOngoingWork()) {
            return;
        }
        this.overclocked = !this.overclocked;
        this.setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public List<UniversalDisplayedRecipe> getDisplayedRecipes() {
        return List.copyOf(this.displayedRecipes);
    }

    protected void rebuildDisplayedRecipes() {
        this.displayedRecipes.clear();
        if (this.running || this.progress > 0 || this.maxProgress > 0) {
            String label = Component.translatable(getControllerTranslationKey()).getString();
            if (label.endsWith(" Controller")) {
                label = label.substring(0, label.length() - " Controller".length());
            }
            this.displayedRecipes.add(new UniversalDisplayedRecipe(
                    ItemStack.EMPTY,
                    net.neoforged.neoforge.fluids.FluidStack.EMPTY,
                    Component.literal(label),
                    1,
                    this.progress,
                    this.maxProgress));
        }
    }

    private void saveDisplayedRecipes(final ValueOutput output) {
        final var recipeList = output.childrenList("displayedRecipes");
        for (final UniversalDisplayedRecipe recipe : this.displayedRecipes) {
            final ValueOutput recipeTag = recipeList.addChild();
            if (!recipe.itemIcon().isEmpty()) {
                final Identifier itemId = BuiltInRegistries.ITEM.getKey(recipe.itemIcon().getItem());
                recipeTag.putString("itemIcon", itemId.toString());
            }
            if (!recipe.fluidIcon().isEmpty()) {
                final Identifier fluidId = BuiltInRegistries.FLUID.getKey(recipe.fluidIcon().getFluid());
                recipeTag.putString("fluidIcon", fluidId.toString());
            }
            recipeTag.putString("label", recipe.label().getString());
            recipeTag.putLong("outputAmount", recipe.outputAmount());
            recipeTag.putInt("progress", recipe.progress());
            recipeTag.putInt("maxProgress", recipe.maxProgress());
        }
    }

    private void loadDisplayedRecipes(final ValueInput input) {
        this.displayedRecipes.clear();
        for (final ValueInput recipeTag : input.childrenListOrEmpty("displayedRecipes")) {
            ItemStack itemIcon = ItemStack.EMPTY;
            net.neoforged.neoforge.fluids.FluidStack fluidIcon = net.neoforged.neoforge.fluids.FluidStack.EMPTY;

            final String itemIconId = recipeTag.getStringOr("itemIcon", "");
            if (!itemIconId.isEmpty()) {
                final Identifier itemId = Identifier.tryParse(itemIconId);
                if (itemId != null && BuiltInRegistries.ITEM.containsKey(itemId)) {
                    itemIcon = new ItemStack(BuiltInRegistries.ITEM.getValue(itemId));
                }
            }

            final String fluidIconId = recipeTag.getStringOr("fluidIcon", "");
            if (!fluidIconId.isEmpty()) {
                final Identifier fluidId = Identifier.tryParse(fluidIconId);
                if (fluidId != null && BuiltInRegistries.FLUID.containsKey(fluidId)) {
                    fluidIcon = new net.neoforged.neoforge.fluids.FluidStack(BuiltInRegistries.FLUID.getValue(fluidId), 1);
                }
            }

            this.displayedRecipes.add(new UniversalDisplayedRecipe(
                    itemIcon,
                    fluidIcon,
                    Component.literal(recipeTag.getStringOr("label", "")),
                    recipeTag.getLongOr("outputAmount", 0L),
                    recipeTag.getIntOr("progress", 0),
                    recipeTag.getIntOr("maxProgress", 0)));
        }
    }
}
