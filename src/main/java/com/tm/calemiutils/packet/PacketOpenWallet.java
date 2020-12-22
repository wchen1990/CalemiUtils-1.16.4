package com.tm.calemiutils.packet;

import com.tm.calemiutils.inventory.ContainerWallet;
import com.tm.calemiutils.inventory.base.ItemStackInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.function.Supplier;

public class PacketOpenWallet {

    private int slotId;

    public PacketOpenWallet () {}

    /**
     * Currently called by the key input.
     * Opens the Wallet GUI on the server.
     * @param slotId The slot the Wallet is in.
     */
    public PacketOpenWallet (int slotId) {
        this.slotId = slotId;
    }

    public PacketOpenWallet (PacketBuffer buf) {
        slotId = buf.readInt();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeInt(slotId);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                ItemStack stack = player.inventory.getStackInSlot(slotId);

                NetworkHooks.openGui(player, new SimpleNamedContainerProvider(
                    (id, playerInventory, openPlayer) -> new ContainerWallet(id, playerInventory, new ItemStackInventory(stack, 1), player.inventory.currentItem), stack.getDisplayName()),
                    (buffer) -> buffer.writeVarInt(player.inventory.currentItem));
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
