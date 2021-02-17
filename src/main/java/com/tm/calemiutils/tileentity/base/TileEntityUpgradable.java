package com.tm.calemiutils.tileentity.base;

import com.tm.calemiutils.util.helper.MathHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.container.Slot;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;

import java.util.ArrayList;
import java.util.List;

public abstract class TileEntityUpgradable extends TileEntityInventoryBase implements IProgress, IRange {

    private final CUItemHandler upgradeInventory;
    public final List<Slot> upgradeSlots = new ArrayList<>();

    public int currentProgress;
    public int currentRange;

    protected TileEntityUpgradable (TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
        this.upgradeInventory = new CUItemHandler(2, upgradeSlots);
    }

    protected abstract int getRangeSlot ();
    protected abstract int getSpeedSlot ();
    protected abstract int getScaledRangeMin ();
    protected abstract int getScaledRangeMax ();
    protected abstract int getScaledSpeedMin ();
    protected abstract int getScaledSpeedMax ();

    public CUItemHandler getUpgradeInventory() {
        return this.upgradeInventory;
    }

    public int getScaledRange () {
        return getScaledSlot(getRangeSlot(), getScaledRangeMin(), getScaledRangeMax());
    }

    private int getScaledSpeed () {
        return getScaledSlot(getSpeedSlot(), getScaledSpeedMin(), getScaledSpeedMax());
    }

    private int getScaledSlot (int slot, int min, int max) {
        int difference = max - min;
        return min + MathHelper.scaleInt(getUpgradeInventory().getStackInSlot(slot).getCount(), 5, difference);
    }

    protected void tickProgress () {
        currentProgress += getScaledSpeed();
    }

    protected boolean isDoneAndReset () {

        if (currentProgress >= getMaxProgress()) {
            currentProgress = 0;
            return true;
        }

        return false;
    }

    @Override
    public int getCurrentProgress () {
        return currentProgress;
    }

    public void setCurrentProgress (int value) {
        currentProgress = value;
    }

    @Override
    public int getCurrentRange () {
        return currentRange;
    }

    @Override
    public void read (BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        currentProgress = nbt.getInt("currentProgress");
        currentRange = nbt.getInt("currentRange");
    }

    @Override
    public CompoundNBT write (CompoundNBT nbt) {
        super.write(nbt);
        nbt.putInt("currentProgress", currentProgress);
        nbt.putInt("currentRange", currentRange);
        return nbt;
    }
}
