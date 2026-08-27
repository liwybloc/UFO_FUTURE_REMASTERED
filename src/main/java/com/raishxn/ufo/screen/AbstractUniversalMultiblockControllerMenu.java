package com.raishxn.ufo.screen;

import com.raishxn.ufo.block.entity.IUniversalMultiblockController;
import com.raishxn.ufo.block.entity.UniversalDisplayedRecipe;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.menu.guisync.GuiSync;
import appeng.menu.implementations.UpgradeableMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public abstract class AbstractUniversalMultiblockControllerMenu<T extends BlockEntity & IUniversalMultiblockController & IUpgradeableObject> extends UpgradeableMenu<T> {
    protected final ContainerLevelAccess levelAccess;

    @GuiSync(20)
    protected int assembled = 0;
    @GuiSync(21)
    protected int running = 0;
    @GuiSync(22)
    protected int progress = 0;
    @GuiSync(23)
    protected int maxProgress = 0;
    @GuiSync(24)
    protected int temperature = 0;
    @GuiSync(25)
    protected int maxTemperature = 1000;
    @GuiSync(26)
    protected int safeMode = 1;
    @GuiSync(27)
    protected int overclocked = 0;
    @GuiSync(28)
    protected int machineTier = 1;
    @GuiSync(29)
    protected int storedEnergyLow = 0;
    @GuiSync(30)
    protected int storedEnergyMidLow = 0;
    @GuiSync(31)
    protected int storedEnergyMidHigh = 0;
    @GuiSync(32)
    protected int storedEnergyHigh = 0;
    @GuiSync(33)
    protected int maxEnergyLow = 0;
    @GuiSync(34)
    protected int maxEnergyMidLow = 0;
    @GuiSync(35)
    protected int maxEnergyMidHigh = 0;
    @GuiSync(36)
    protected int maxEnergyHigh = 0;
    @GuiSync(37)
    protected int activeParallels = 0;
    @GuiSync(38)
    protected int maxParallels = 1;

    protected AbstractUniversalMultiblockControllerMenu(final net.minecraft.world.inventory.MenuType<?> menuType, final int id, final Inventory playerInventory,
                                                        final T blockEntity, final ContainerLevelAccess levelAccess) {
        super(menuType, id, playerInventory, blockEntity);
        this.levelAccess = levelAccess;
    }

    public T getBlockEntity() {
        return this.getHost();
    }

    @Override
    protected void standardDetectAndSendChanges() {
        if (isServerSide()) {
            this.assembled = this.getHost().isGuiAssembled() ? 1 : 0;
            this.running = this.getHost().isGuiRunning() ? 1 : 0;
            this.progress = this.getHost().getGuiProgress();
            this.maxProgress = this.getHost().getGuiMaxProgress();
            this.temperature = this.getHost().getGuiTemperature();
            this.maxTemperature = this.getHost().getGuiMaxTemperature();
            this.safeMode = this.getHost().isGuiSafeMode() ? 1 : 0;
            this.overclocked = this.getHost().isGuiOverclocked() ? 1 : 0;
            this.machineTier = this.getHost().getGuiMachineTier();
            final long storedEnergy = this.getHost().getGuiStoredEnergy();
            final long maxEnergy = this.getHost().getGuiMaxEnergy();
            this.storedEnergyLow = splitLong(storedEnergy, 0);
            this.storedEnergyMidLow = splitLong(storedEnergy, 16);
            this.storedEnergyMidHigh = splitLong(storedEnergy, 32);
            this.storedEnergyHigh = splitLong(storedEnergy, 48);
            this.maxEnergyLow = splitLong(maxEnergy, 0);
            this.maxEnergyMidLow = splitLong(maxEnergy, 16);
            this.maxEnergyMidHigh = splitLong(maxEnergy, 32);
            this.maxEnergyHigh = splitLong(maxEnergy, 48);
            this.activeParallels = this.getHost().getGuiActiveParallels();
            this.maxParallels = this.getHost().getGuiMaxParallels();
        }
        super.standardDetectAndSendChanges();
    }

    public boolean isAssembled() {
        return this.assembled == 1;
    }

    public boolean isRunning() {
        return this.running == 1;
    }

    public int getProgress() {
        return this.progress;
    }

    public int getMaxProgress() {
        return this.maxProgress;
    }

    public int getTemperature() {
        return this.temperature;
    }

    public int getMaxTemperature() {
        return Math.max(1, this.maxTemperature);
    }

    public boolean isSafeMode() {
        return this.safeMode == 1;
    }

    public boolean isOverclocked() {
        return this.overclocked == 1;
    }

    public int getMachineTier() {
        return Math.max(1, this.machineTier);
    }

    public long getStoredEnergy() {
        return assembleLong(this.storedEnergyLow, this.storedEnergyMidLow, this.storedEnergyMidHigh, this.storedEnergyHigh);
    }

    public long getMaxEnergy() {
        return assembleLong(this.maxEnergyLow, this.maxEnergyMidLow, this.maxEnergyMidHigh, this.maxEnergyHigh);
    }

    public int getActiveParallels() {
        return Math.max(0, this.activeParallels);
    }

    public int getMaxParallels() {
        return Math.max(1, this.maxParallels);
    }

    public List<UniversalDisplayedRecipe> getDisplayedRecipes() {
        return this.getHost().getDisplayedRecipes();
    }

    private static int splitLong(final long value, final int shift) {
        return (int) ((value >> shift) & 0xFFFFL);
    }

    private static long assembleLong(final int low, final int midLow, final int midHigh, final int high) {
        return ((long) low & 0xFFFFL)
                | (((long) midLow & 0xFFFFL) << 16)
                | (((long) midHigh & 0xFFFFL) << 32)
                | (((long) high & 0xFFFFL) << 48);
    }

    protected abstract Block getValidBlock();
}
