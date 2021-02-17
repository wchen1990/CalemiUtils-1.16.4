package com.tm.calemiutils.tileentity;

import com.tm.calemiutils.block.BlockBlueprint;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.gui.ScreenBlueprintFiller;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.inventory.ContainerBlueprintFiller;
import com.tm.calemiutils.tileentity.base.TileEntityUpgradable;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.InventoryHelper;
import com.tm.calemiutils.util.helper.ItemHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TileEntityBlueprintFiller extends TileEntityUpgradable {

    private boolean scanning = false;

    private final List<Location> scannedBlueprint = new ArrayList<>();
    private final List<Location> nextToScan = new ArrayList<>();

    private final int[] amounts = new int[16];

    private final ItemStack[] filters = new ItemStack[16];

    public TileEntityBlueprintFiller () {
        super(InitTileEntityTypes.BLUEPRINT_FILLER.get());
        enable = false;
        Arrays.fill(filters, ItemStack.EMPTY);
    }

    public boolean isScanning() {
        return scanning;
    }

    private void setScan(boolean value) {
        this.scanning = value;
    }

    public void startScan() {
        enable = false;
        setScan(true);
        getScannedBlueprint().clear();
        nextToScan.clear();
        nextToScan.add(getLocation());
        Arrays.fill(amounts, 0);
    }

    public void setEnable(boolean value) {

        if (value) {
            setScan(false);
        }

        enable = value;
    }

    public List<Location> getScannedBlueprint() {
        return scannedBlueprint;
    }

    public ItemStack getFilter(int index) {

        if (index < filters.length) {
            return filters[index];
        }

        return ItemStack.EMPTY;
    }

    public void setFilter(int index, ItemStack stack) {

        if (index < filters.length) {
            filters[index] = stack;
        }
    }

    public int[] getAmounts() {
        return amounts;
    }

    @Override
    public void tick () {

        if (world == null) {
            return;
        }

        if (isScanning() && world.getGameTime() % 5 == 0) {

            if (nextToScan.isEmpty()) {

                updateAmounts();
                setScan(false);
            }

            else {

                List<Location> nextToScanBuffer = new ArrayList<>(nextToScan);
                nextToScan.clear();

                for (Location toScan : nextToScanBuffer) {
                    scanConnectedBlocks(toScan);
                }
            }
        }

        else if (enable) {

            if (getScannedBlueprint().isEmpty()) {
                setEnable(false);
            }

            tickProgress();

            if (isDoneAndReset()) {

                IInventory inv = getConnectedInventory();

                if (inv != null) {

                    for (int i = 0; i < 64; i++) {

                        if (getScannedBlueprint().size() <= 0) {
                            setEnable(false);
                            return;
                        }

                        Location location = getScannedBlueprint().get(getScannedBlueprint().size() - 1);

                        if (location.getBlock() != InitItems.BLUEPRINT.get()) {
                            setEnable(false);
                            getScannedBlueprint().remove(getScannedBlueprint().size() - 1);
                            continue;
                        }

                        int colorIndex = location.getBlockState().get(BlockBlueprint.COLOR).getId();
                        ItemStack filter = getFilter(colorIndex);

                        if (filter.getItem() instanceof BlockItem && InventoryHelper.countItems(inv, false, filter) > 0) {

                            InventoryHelper.consumeStack(inv, 1,false, filter);
                            getScannedBlueprint().remove(getScannedBlueprint().size() - 1);
                            location.setBlock(((BlockItem) filter.getItem()).getBlock());
                        }

                        else setEnable(false);
                    }
                }

                else setEnable(false);
            }
        }
    }

    private void scanConnectedBlocks(Location location) {

        for (Direction dir : Direction.values()) {

            Location nextLocation = new Location(location, dir);

            if (getScannedBlueprint().size() >= getScaledRange()) {

                setScan(false);
                return;
            }

            if (!getScannedBlueprint().contains(nextLocation) && nextLocation.getBlock() != null) {

                if (nextLocation.getBlock() == InitItems.BLUEPRINT.get()) {
                    getScannedBlueprint().add(nextLocation);
                    nextToScan.add(nextLocation);
                }
            }
        }
    }

    private IInventory getConnectedInventory() {

        Location location = getLocation().translate(Direction.UP, 1);

        if (location.getTileEntity() != null && !(location.getTileEntity() instanceof TileEntityBank) && location.getTileEntity() instanceof IInventory) {
            return (IInventory) location.getTileEntity();
        }

        return null;
    }

    private void updateAmounts() {

        for (Location location : getScannedBlueprint()) {

            if (location.getBlock() == InitItems.BLUEPRINT.get()) {
                amounts[location.getBlockState().get(BlockBlueprint.COLOR).getId()]++;
            }
        }
    }

    @Override
    public int getRangeSlot () {
        return 1;
    }

    @Override
    public int getSizeInventory () {
        return 0;
    }

    @Override
    public int getScaledRangeMin () {
        return 10000;
    }

    @Override
    public int getScaledRangeMax () {
        return CUConfig.misc.blueprintFillerMaxScan.get();
    }

    @Override
    public int getSpeedSlot () {
        return 0;
    }

    @Override
    public int getScaledSpeedMin () {
        return 3;
    }

    @Override
    public int getScaledSpeedMax () {
        return 15;
    }

    @Override
    public int getMaxProgress () {
        return 200;
    }

    @Override
    public ITextComponent getDefaultName () {
        return new StringTextComponent("Blueprint Filler");
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerBlueprintFiller(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenBlueprintFiller(getTileContainer(windowId, playerInv), playerInv, getDefaultName());
    }

    @Override
    public void read (BlockState state, CompoundNBT nbt) {

        super.read(state, nbt);

        scanning = nbt.getBoolean("scanning");

        for (int i = 0; i < filters.length; i++) {
            filters[i] = ItemHelper.getStackFromString(nbt.getString("stack_" + i));
        }
    }

    @Override
    public CompoundNBT write (CompoundNBT nbt) {

        super.write(nbt);

        nbt.putBoolean("scanning", scanning);

        for (int i = 0; i < filters.length; i++) {
            nbt.putString("stack_" + i, ItemHelper.getStringFromStack(filters[i]));
        }

        return nbt;
    }
}
