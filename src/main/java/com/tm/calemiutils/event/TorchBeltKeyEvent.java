package com.tm.calemiutils.event;

import com.tm.calemiutils.init.InitKeyBindings;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.packet.PacketToggleTorchBelt;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TorchBeltKeyEvent {

    /**
     * Handles all key events.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onKeyInput (InputEvent.KeyInputEvent event) {

        //Checks if the Torch Belt key is pressed.
        if (InitKeyBindings.toggleTorchBeltButton.isPressed()) {

            PlayerEntity player = Minecraft.getInstance().player;

            //Checks if the Player exists.
            if (player != null) {
                CalemiUtils.network.sendToServer(new PacketToggleTorchBelt());
            }
        }
    }
}