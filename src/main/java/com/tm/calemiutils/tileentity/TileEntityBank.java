package com.tm.calemiutils.tileentity;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.gui.ScreenBank;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.inventory.ContainerBank;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.security.SecurityProfile;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkBank;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkUnit;
import com.tm.calemiutils.tileentity.base.TileEntityInventoryBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.VeinScan;
import com.tm.calemiutils.util.helper.ItemHelper;
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
                CUConfig.coins.stream().anyMatch((coinStr) -> ItemHelper.getItemFromString(coinStr) == bankSlotCoinItem)
                || CUConfig.coinStacks.stream().anyMatch((coinStackStr) -> ItemHelper.getItemFromString(coinStackStr) == bankSlotCoinItem)
            ) {
                int netheriteValue = CUConfig.coinValues.netherite.get();
                int goldValue = CUConfig.coinValues.gold.get();
                int brassValue = CUConfig.coinValues.brass.get();
                int ironValue = CUConfig.coinValues.iron.get();
                int copperValue = CUConfig.coinValues.copper.get();
                int zincValue = CUConfig.coinValues.zinc.get();

                int value = 0;
                if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:zinc_coin"))
                    value = zincValue;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:copper_coin"))
                    value = copperValue;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:iron_coin"))
                    value = ironValue;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:brass_coin"))
                    value = brassValue;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:gold_coin"))
                    value = goldValue;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:netherite_coin"))
                    value = netheriteValue;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:zinc_coinstack"))
                    value = zincValue * 4;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:copper_coinstack"))
                    value = copperValue * 4;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:iron_coinstack"))
                    value = ironValue * 4;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:brass_coinstack"))
                    value = brassValue * 4;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:gold_coinstack"))
                    value = goldValue * 4;
                else if (bankSlotCoinItem == ItemHelper.getItemFromString("createdeco:netherite_coinstack"))
                    value = netheriteValue * 4;

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
