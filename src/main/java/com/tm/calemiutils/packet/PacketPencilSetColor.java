package com.tm.calemiutils.packet;

import com.tm.calemiutils.item.ItemPencil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketPencilSetColor {

    private int colorId;
    private boolean offHand;

    public PacketPencilSetColor () {}

    /**
     * Used to sync the color data of the Pencil.
     * @param colorId The color id from which a color can be created from.
     * @param hand The hand the Pencil is held.
     */
    public PacketPencilSetColor (int colorId, Hand hand) {
        this.colorId = colorId;
        this.offHand = (hand != Hand.MAIN_HAND);
    }

    public PacketPencilSetColor (PacketBuffer buf) {
        colorId = buf.readInt();
        offHand = buf.readBoolean();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeInt(colorId);
        buf.writeBoolean(offHand);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            Hand hand = Hand.MAIN_HAND;
            if (offHand) hand = Hand.OFF_HAND;

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                final ItemStack stack = player.getHeldItem(hand);

                //Checks if the held item is a Pencil.
                if (stack.getItem() instanceof ItemPencil) {

                    ItemPencil pencil = (ItemPencil) stack.getItem();
                    pencil.setColorById(stack, colorId);
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
