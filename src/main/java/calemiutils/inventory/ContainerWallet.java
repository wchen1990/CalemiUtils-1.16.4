package calemiutils.inventory;

import calemiutils.init.InitContainerTypes;
import calemiutils.init.InitItems;
import calemiutils.inventory.base.ContainerBase;
import calemiutils.inventory.base.SlotIInventoryFilter;
import calemiutils.item.ItemCoin;
import calemiutils.util.helper.CurrencyHelper;
import calemiutils.util.helper.ItemHelper;
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
        super(InitContainerTypes.WALLET.get(), windowID, playerInventory, null, 8, 94);

        isItemContainer = true;
        size = 1;

        this.stackInv = stackInv;
        this.selectedSlot = selectedSlot;

        //New Inventory
        addSlot(new SlotIInventoryFilter(stackInv, 0, 17, 42, InitItems.COIN_PENNY.get(), InitItems.COIN_NICKEL.get(), InitItems.COIN_QUARTER.get(), InitItems.COIN_DOLLAR.get()));
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

        //Checks if the Stack in the Wallet is a Coin.
        if (stackInInv.getItem() instanceof ItemCoin) {

            ItemCoin currency = ((ItemCoin) stackInInv.getItem());

            int amountToAdd = 0;
            int stacksToRemove = 0;

            //Iterates through every count of the Stack. Ex: a stack of 32 will iterate 32 times.
            for (int i = 0; i < stackInInv.getCount(); i++) {

                //Checks if the Wallet can fit the added money.
                if (CurrencyHelper.canDepositToWallet(walletStack, currency.value)) {
                    amountToAdd += currency.value;
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
