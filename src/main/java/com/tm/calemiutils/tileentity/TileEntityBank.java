package com.tm.calemiutils.tileentity;

import com.github.talrey.createdeco.Registration;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.gui.ScreenBank;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.inventory.ContainerBank;
import com.tm.calemiutils.item.ItemCoin;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.security.SecurityProfile;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkBank;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkUnit;
import com.tm.calemiutils.tileentity.base.TileEntityInventoryBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.VeinScan;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class TileEntityBank extends TileEntityInventoryBase implements ICurrencyNetworkBank, ISecurity {

    private final SecurityProfile profile = new SecurityProfile();
    public final List<Location> connectedUnits = new ArrayList<>();
    public int storedCurrency = 0;
    private VeinScan scan;

    public TileEntityBank () {
        super(InitTileEntityTypes.BANK.get());
    }

    @Override
    public void tick () {
        super.tick();

        if (world == null) {
            return;
        }

        if (getLocation() != null && scan == null) {
            scan = new VeinScan(getLocation());
        }

        if (scan != null) {

            if (world.getGameTime() % 40 == 0) {

                connectedUnits.clear();

                boolean foundAnotherBank = false;

                scan.reset();
                scan.startNetworkScan(getConnectedDirections());

                for (Location location : scan.buffer) {

                    if (!location.equals(getLocation()) && location.getTileEntity() instanceof TileEntityBank) {
                        foundAnotherBank = true;
                    }

                    if (location.getTileEntity() instanceof ICurrencyNetworkUnit) {

                        ICurrencyNetworkUnit unit = (ICurrencyNetworkUnit) location.getTileEntity();

                        connectedUnits.add(location);

                        if (unit.getBankLocation() == null) {

                            unit.setBankLocation(getLocation());
                        }
                    }
                }

                enable = !foundAnotherBank;
            }
        }

        if (!world.isRemote) {

            Item bankSlotCoinItem = getInventory().getStackInSlot(0).getItem();
            if (
                Registration.COIN_ITEM.values().stream().anyMatch((itemEntry) -> itemEntry.get() == bankSlotCoinItem)
                || Registration.COINSTACK_ITEM.values().stream().anyMatch((itemEntry) -> itemEntry.get() == bankSlotCoinItem)
            ) {

                int value = 0;
                if (bankSlotCoinItem == Registration.COIN_ITEM.get("Zinc").get())
                    value = 1;
                else if (bankSlotCoinItem == Registration.COIN_ITEM.get("Copper").get())
                    value = 5;
                else if (bankSlotCoinItem == Registration.COIN_ITEM.get("Iron").get())
                    value = 10;
                else if (bankSlotCoinItem == Registration.COIN_ITEM.get("Brass").get())
                    value = 20;
                else if (bankSlotCoinItem == Registration.COIN_ITEM.get("Gold").get())
                    value = 50;
                else if (bankSlotCoinItem == Registration.COIN_ITEM.get("Netherite").get())
                    value = 100;
                else if (bankSlotCoinItem == Registration.COINSTACK_ITEM.get("Zinc").get())
                    value = 4;
                else if (bankSlotCoinItem == Registration.COINSTACK_ITEM.get("Copper").get())
                    value = 20;
                else if (bankSlotCoinItem == Registration.COINSTACK_ITEM.get("Iron").get())
                    value = 40;
                else if (bankSlotCoinItem == Registration.COINSTACK_ITEM.get("Brass").get())
                    value = 80;
                else if (bankSlotCoinItem == Registration.COINSTACK_ITEM.get("Gold").get())
                    value = 200;
                else if (bankSlotCoinItem == Registration.COINSTACK_ITEM.get("Netherite").get())
                    value = 400;

                int amountToAdd = value;
                int stackSize = 0;

                for (int i = 0; i < getInventory().getStackInSlot(0).getCount(); i++) {

                    if (canDeposit(amountToAdd)) {
                        stackSize++;
                        amountToAdd += value;
                    }
                }

                if (stackSize != 0) {

                    depositCurrency(stackSize * value);
                    getInventory().decrStackSize(0, stackSize);
                }
            }
        }
    }

    @Override
    public int getStoredCurrency () {
        return storedCurrency;
    }

    @Override
    public int getMaxCurrency () {
        return CUConfig.economy.bankCurrencyCapacity.get();
    }

    @Override
    public void setCurrency (int amount) {

        int setAmount = amount;

        if (amount > getMaxCurrency()) {
            setAmount = getMaxCurrency();
        }

        storedCurrency = setAmount;
        markForUpdate();
    }

    @Override
    public boolean canDeposit(int depositAmount) {
        int storedAmount = storedCurrency;
        return storedAmount + depositAmount <= getMaxCurrency();
    }

    @Override
    public boolean canWithdraw(int withdrawAmount) {
        return storedCurrency >= withdrawAmount;
    }

    @Override
    public void depositCurrency(int depositAmount) {
        int newAmount = storedCurrency + depositAmount;
        if (newAmount > CUConfig.economy.bankCurrencyCapacity.get()) newAmount = CUConfig.economy.bankCurrencyCapacity.get();
        setCurrency(newAmount);
    }

    @Override
    public void withdrawCurrency(int withdrawAmount) {
        int newAmount = storedCurrency - withdrawAmount;
        if (newAmount < 0) newAmount = 0;
        setCurrency(newAmount);
    }

    @Override
    public int getSizeInventory () {
        return 2;
    }

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    @Override
    public Direction[] getConnectedDirections () {
        return Direction.values();
    }

    @Override
    public ITextComponent getDefaultName () {
        return new StringTextComponent("Bank");
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerBank(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenBank(getTileContainer(windowId, playerInv), playerInv, getDefaultName());
    }
}
