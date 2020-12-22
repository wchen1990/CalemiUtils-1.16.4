package com.tm.calemiutils.event;

import com.tm.calemiutils.CalemiUtils;
import com.tm.calemiutils.init.InitKeyBindings;
import com.tm.calemiutils.packet.PacketOpenWallet;
import com.tm.calemiutils.util.helper.CurrencyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WalletKeyEvent {

    /**
     * Handles all key events.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput (InputEvent.KeyInputEvent event) {

        //Checks if the Wallet key is pressed.
        if (InitKeyBindings.openWalletButton.isPressed()) {

            PlayerEntity player = Minecraft.getInstance().player;

            //Checks if the Player exists.
            if (player != null) {
                ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);

                //If the player has a current Wallet, open its GUI.
                if (!walletStack.isEmpty()) {
                    CalemiUtils.network.sendToServer(new PacketOpenWallet());
                }
            }
        }
    }
}