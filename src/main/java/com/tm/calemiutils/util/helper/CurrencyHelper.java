package com.tm.calemiutils.util.helper;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.item.ItemWallet;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkBank;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class CurrencyHelper {

    /**
     * Used to find the Player's Wallet. If multiple, chooses by priority.
     */
    public static ItemStack getCurrentWalletStack (PlayerEntity player) {

        //Priority #1 - Held mainhand.
        if (player.getHeldItemMainhand().getItem() instanceof ItemWallet) {
            return player.getHeldItemMainhand();
        }

        //Priority #2 - Held offhand.
        if (player.getHeldItemOffhand().getItem() instanceof ItemWallet) {
            return player.getHeldItemOffhand();
        }

        //Priority #3 - Curios slot.
        if (CalemiUtils.curiosLoaded) {

            if (CuriosApi.getCuriosHelper().findEquippedCurio(InitItems.WALLET.get(), player).isPresent()) {
                return CuriosApi.getCuriosHelper().findEquippedCurio(InitItems.WALLET.get(), player).get().right;
            }
        }

        //Priority #4 - Inventory (lowest slot id wins).
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {

            ItemStack stack = player.inventory.getStackInSlot(i);

            if (stack.getItem() instanceof ItemWallet) {
                return stack;
            }
        }

        //No Wallet was found.
        return ItemStack.EMPTY;
    }

    /**
     * Bank currency methods.
     */

    public static boolean canDepositToBank(ICurrencyNetworkBank bank, int depositAmount) {
        if (bank == null) return false;
        return bank.getStoredCurrency() + depositAmount <= bank.getMaxCurrency();
    }

    public static boolean canWithdrawFromBank(ICurrencyNetworkBank bank, int withdrawAmount) {
        if (bank == null) return false;
        return bank.getStoredCurrency() >= withdrawAmount;
    }

    public static void depositToBank(ICurrencyNetworkBank bank, int depositAmount) {
        if (bank == null) return;
        bank.depositCurrency(depositAmount);
    }

    public static void withdrawFromBank(ICurrencyNetworkBank bank, int withdrawAmount) {
        if (bank == null) return;
        bank.withdrawCurrency(withdrawAmount);
    }

    /**
     * Wallet currency methods.
     */

    public static boolean canDepositToWallet(ItemStack walletStack, int depositAmount) {

        if (walletStack.getItem() instanceof ItemWallet) {
            return ItemWallet.getBalance(walletStack) + depositAmount <= CUConfig.wallet.walletCurrencyCapacity.get();
        }

        return false;
    }

    public static boolean canWithdrawFromWallet(ItemStack walletStack, int withdrawAmount) {

        if (walletStack.getItem() instanceof ItemWallet) {
            return ItemWallet.getBalance(walletStack) >= withdrawAmount;
        }

        return false;
    }

    public static void depositToWallet(ItemStack walletStack, int depositAmount) {

        if (walletStack.getItem() instanceof ItemWallet) {
            ItemWallet.depositCurrency(walletStack, depositAmount);
        }
    }

    public static void withdrawFromWallet(ItemStack walletStack, int withdrawAmount) {

        if (walletStack.getItem() instanceof ItemWallet) {
            ItemWallet.withdrawCurrency(walletStack, withdrawAmount);
        }
    }
}
