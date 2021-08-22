package com.tm.calemiutils.inventory;

import com.github.talrey.createdeco.Registration;
import com.tm.calemiutils.init.InitContainerTypes;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.inventory.base.SlotIInventoryFilter;
import com.tm.calemiutils.item.ItemCoin;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.util.helper.CurrencyHelper;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.LogHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ContainerWallet extends ContainerBase {

    public final int selectedSlot;
    private final IInventory stackInv;

    public ContainerWallet (final int windowID, final PlayerInventory playerInventory, IInventory stackInv, int selectedSlot) {
        super(InitContainerTypes.WALLET.get(), windowID, playerInventory, null, 25, 94);

        isItemContainer = true;
        size = 1;

        this.stackInv = stackInv;
        this.selectedSlot = selectedSlot;

        //New Inventory
        addSlot(new SlotIInventoryFilter(stackInv, 0, 25, 42,
            Registration.COIN_ITEM.get("Zinc").get(),
            Registration.COIN_ITEM.get("Copper").get(),
            Registration.COIN_ITEM.get("Iron").get(),
            Registration.COIN_ITEM.get("Brass").get(),
            Registration.COIN_ITEM.get("Gold").get(),
            Registration.COIN_ITEM.get("Netherite").get(),
            Registration.COINSTACK_ITEM.get("Zinc").get(),
            Registration.COINSTACK_ITEM.get("Copper").get(),
            Registration.COINSTACK_ITEM.get("Iron").get(),
            Registration.COINSTACK_ITEM.get("Brass").get(),
            Registration.COINSTACK_ITEM.get("Gold").get(),
            Registration.COINSTACK_ITEM.get("Netherite").get()
        ));
    }

    public static ContainerWallet createClientWallet (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        final int selectedSlot = data.readVarInt();
        return new ContainerWallet(windowId, playerInventory, new Inventory(1), selectedSlot);
    }

    private CompoundNBT getNBT () {
        return ItemHelper.getNBT(getCurrentWalletStack());
    }

    private ItemStack getCurrentWalletStack () {
        return CurrencyHelper.getCurrentWalletStack(playerInventory.player);
    }

    /**
     * Called when a slot is clicked.
     * Handles adding money to Wallet.
     */
    @Override
    public ItemStack slotClick (int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {

        ItemStack returnStack = super.slotClick(slotId, dragType, clickTypeIn, player);
        ItemStack stackInInv = stackInv.getStackInSlot(0);
        ItemStack walletStack = getCurrentWalletStack();

        //Checks if the Stack in the Wallet is a Create Deco Coin.
        if (
            Registration.COIN_ITEM.values().stream().anyMatch((itemEntry) -> itemEntry.get() == stackInInv.getItem())
            || Registration.COINSTACK_ITEM.values().stream().anyMatch((itemEntry) -> itemEntry.get() == stackInInv.getItem())
        ) {
            int value = 0;
            if (stackInInv.getItem() == Registration.COIN_ITEM.get("Zinc").get())
                value = 1;
            else if (stackInInv.getItem() == Registration.COIN_ITEM.get("Copper").get())
                value = 5;
            else if (stackInInv.getItem() == Registration.COIN_ITEM.get("Iron").get())
                value = 10;
            else if (stackInInv.getItem() == Registration.COIN_ITEM.get("Brass").get())
                value = 20;
            else if (stackInInv.getItem() == Registration.COIN_ITEM.get("Gold").get())
                value = 50;
            else if (stackInInv.getItem() == Registration.COIN_ITEM.get("Netherite").get())
                value = 100;
            else if (stackInInv.getItem() == Registration.COINSTACK_ITEM.get("Zinc").get())
                value = 4;
            else if (stackInInv.getItem() == Registration.COINSTACK_ITEM.get("Copper").get())
                value = 20;
            else if (stackInInv.getItem() == Registration.COINSTACK_ITEM.get("Iron").get())
                value = 40;
            else if (stackInInv.getItem() == Registration.COINSTACK_ITEM.get("Brass").get())
                value = 80;
            else if (stackInInv.getItem() == Registration.COINSTACK_ITEM.get("Gold").get())
                value = 200;
            else if (stackInInv.getItem() == Registration.COINSTACK_ITEM.get("Netherite").get())
                value = 400;

            int amountToAdd = 0;
            int stacksToRemove = 0;

            //Iterates through every count of the Stack. Ex: a stack of 32 will iterate 32 times.
            for (int i = 0; i < stackInInv.getCount(); i++) {

                //Checks if the Wallet can fit the added money.
                if (CurrencyHelper.canDepositToWallet(walletStack, value)) {
                    amountToAdd += value;
                    stacksToRemove++;
                }

                else break;
            }

            CurrencyHelper.depositToWallet(walletStack, amountToAdd);
            stackInv.decrStackSize(0, stacksToRemove);
        }

        return returnStack;
    }

    @Override
    public void onContainerClosed (PlayerEntity player) {
        super.onContainerClosed(player);
    }

    @OnlyIn(Dist.CLIENT)
    public int getSize () {
        return size;
    }
}
