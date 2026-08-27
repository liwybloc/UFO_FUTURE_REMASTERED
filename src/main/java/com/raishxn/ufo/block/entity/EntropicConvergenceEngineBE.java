package com.raishxn.ufo.block.entity;

import appeng.api.upgrades.IUpgradeInventory;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.api.upgrades.UpgradeInventories;
import appeng.block.AEBaseEntityBlock;
import appeng.blockentity.crafting.CraftingBlockEntity;
import com.raishxn.ufo.api.multiblock.FieldTieredCubeValidator;
import com.raishxn.ufo.api.multiblock.IEntropicMachineController;
import com.raishxn.ufo.api.multiblock.MultiblockMachineTier;
import com.raishxn.ufo.init.ModBlockEntities;
import com.raishxn.ufo.screen.EntropicConvergenceEngineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntropicConvergenceEngineBE extends CraftingBlockEntity
        implements IEntropicMachineController, IUniversalMultiblockController, IUpgradeableObject, MenuProvider {

    private static final long[] STORAGE_BY_TIER = {
            0L,
            4_611_686_018_427_387_904L,
            8_646_911_284_551_352_320L,
            Long.MAX_VALUE
    };
    private static final int[] COPROCESSORS_BY_TIER = {
            0,
            250_000_000,
            1_000_000_000,
            Integer.MAX_VALUE
    };

    private final IUpgradeInventory upgrades;
    private final EntropicConvergenceCalculator calc = new EntropicConvergenceCalculator(this);
    private final List<BlockPos> parts = new ArrayList<>();
    private final Set<BlockPos> partSet = new HashSet<>();
    private boolean structureAssembled;
    private int machineTier = MultiblockMachineTier.MK1.level();
    @Nullable
    private BlockPos anchorPos;

    public EntropicConvergenceEngineBE(final BlockPos pos, final BlockState state) {
        this(ModBlockEntities.ENTROPIC_CONVERGENCE_ENGINE_BE.get(), pos, state);
    }

    public EntropicConvergenceEngineBE(final BlockEntityType<? extends EntropicConvergenceEngineBE> type, final BlockPos pos, final BlockState state) {
        super(type, pos, state);
        this.upgrades = UpgradeInventories.forMachine(state.getBlock().asItem(), 4, this::saveChanges);
    }

    @Override
    public void scanStructure(final Level level) {
        if (level instanceof final ServerLevel serverLevel) {
            this.calc.calculateMultiblock(serverLevel, this.worldPosition);
        }
    }

    @Override
    public void onReady() {
        this.getMainNode().create(getLevel(), getBlockEntity().getBlockPos());

        final BlockState currentState = getBlockState();
        if (currentState.getBlock() instanceof final AEBaseEntityBlock<?> block) {
            final BlockState newState = block.getBlockEntityBlockState(currentState, this);
            if (currentState != newState) {
                this.markForUpdate();
            }
        }

        this.getMainNode().setVisualRepresentation(this.getItemFromBlockEntity());
        if (this.level instanceof final ServerLevel serverLevel) {
            this.calc.calculateMultiblock(serverLevel, this.worldPosition);
        }
    }

    @Override
    public void updateMultiBlock(final BlockPos changedPos) {
        if (this.level instanceof final net.minecraft.server.level.ServerLevel serverLevel) {
            this.calc.updateMultiblockAfterNeighborUpdate(serverLevel, this.worldPosition, changedPos);
        }
    }

    @Override
    public long getStorageBytes() {
        if (!this.structureAssembled || !this.isAnchor()) {
            return 0L;
        }
        return STORAGE_BY_TIER[Math.max(1, Math.min(3, this.machineTier))];
    }

    @Override
    public int getAcceleratorThreads() {
        if (!this.structureAssembled || !this.isAnchor()) {
            return 0;
        }
        return COPROCESSORS_BY_TIER[Math.max(1, Math.min(3, this.machineTier))];
    }

    @Override
    public boolean isAssembled() {
        return this.structureAssembled;
    }

    @Override
    public boolean canProxyInteract(final BlockPos pos) {
        return pos.equals(this.worldPosition) || this.partSet.contains(pos);
    }

    @Override
    public boolean isNetworkConnected() {
        final var node = this.getActionableNode();
        return node != null && node.getGrid() != null && node.isActive();
    }

    @Override
    public void markStructureDirty() {
        if (this.level instanceof final ServerLevel serverLevel) {
            this.calc.calculateMultiblock(serverLevel, this.worldPosition);
        }
    }

    @Override
    public void addPart(final BlockPos partPos) {
        final BlockPos immutable = partPos.immutable();
        if (this.partSet.add(immutable)) {
            this.parts.add(immutable);
        }
    }

    @Override
    public void removePart(final BlockPos partPos) {
        this.partSet.remove(partPos);
        this.parts.remove(partPos);
        this.markStructureDirty();
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
        return Component.translatable("block.ufo.entropic_convergence_engine");
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(final int id, @NotNull final Inventory playerInventory, @NotNull final Player player) {
        return new EntropicConvergenceEngineMenu(id, playerInventory, this);
    }

    @Override
    public boolean isGuiAssembled() {
        return this.structureAssembled;
    }

    @Override
    public boolean isGuiRunning() {
        return this.isFormed() && isNetworkConnected();
    }

    @Override
    public int getGuiProgress() {
        return 0;
    }

    @Override
    public int getGuiMaxProgress() {
        return 1;
    }

    @Override
    public int getGuiTemperature() {
        return 0;
    }

    @Override
    public int getGuiMaxTemperature() {
        return 1;
    }

    @Override
    public int getGuiMachineTier() {
        return this.machineTier;
    }

    @Override
    public long getGuiStoredEnergy() {
        return getStorageBytes();
    }

    @Override
    public long getGuiMaxEnergy() {
        return getStorageBytes();
    }

    @Override
    public int getGuiActiveParallels() {
        return this.structureAssembled ? getAcceleratorThreads() : 0;
    }

    @Override
    public int getGuiMaxParallels() {
        return this.structureAssembled ? getAcceleratorThreads() : 1;
    }

    @Override
    public boolean isGuiSafeMode() {
        return true;
    }

    @Override
    public boolean isGuiOverclocked() {
        return false;
    }

    @Override
    public void toggleSafeMode() {
    }

    @Override
    public void toggleOverclock() {
    }

    @Override
    public List<UniversalDisplayedRecipe> getDisplayedRecipes() {
        return List.of();
    }

    @Override
    public IUpgradeInventory getUpgrades() {
        return this.upgrades;
    }

    @Override
    public void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.putBoolean("StructureAssembled", this.structureAssembled);
        output.putInt("MachineTier", this.machineTier);
        if (this.anchorPos != null) {
            output.store("AnchorPos", BlockPos.CODEC, this.anchorPos);
        }
        final var parts = output.list("EntropicParts", BlockPos.CODEC);
        this.parts.forEach(parts::add);
    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.structureAssembled = input.getBooleanOr("StructureAssembled", false);
        this.machineTier = Math.max(MultiblockMachineTier.MK1.level(), input.getIntOr("MachineTier", MultiblockMachineTier.MK1.level()));
        this.anchorPos = input.read("AnchorPos", BlockPos.CODEC).map(BlockPos::immutable).orElse(null);

        this.parts.clear();
        this.partSet.clear();
        for (final BlockPos pos : input.listOrEmpty("EntropicParts", BlockPos.CODEC)) {
            final BlockPos immutable = pos.immutable();
            this.parts.add(immutable);
            this.partSet.add(immutable);
        }
    }

    @Override
    public void disconnect(final boolean update) {
        super.disconnect(update);
        clearCalculatedStructure();
    }

    public void clearCalculatedStructure() {
        this.structureAssembled = false;
        this.machineTier = MultiblockMachineTier.MK1.level();
        this.anchorPos = null;
        this.parts.clear();
        this.partSet.clear();
    }

    public void applyCalculatedStructure(final appeng.me.cluster.implementations.CraftingCPUCluster cluster,
                                         final FieldTieredCubeValidator.ValidationResult result) {
        this.structureAssembled = true;
        this.machineTier = result.machineTier();
        this.anchorPos = result.origin().immutable();
        this.parts.clear();
        this.partSet.clear();

        for (final BlockPos shellPos : result.shellPositions()) {
            if (!shellPos.equals(this.worldPosition)) {
                final BlockPos immutable = shellPos.immutable();
                this.parts.add(immutable);
                this.partSet.add(immutable);
            }
        }

        this.updateStatus(cluster);
        this.setChanged();
    }

    private boolean isAnchor() {
        return this.worldPosition.equals(this.anchorPos);
    }
}
