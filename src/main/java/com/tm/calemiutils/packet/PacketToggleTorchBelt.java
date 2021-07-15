package com.tm.calemiutils.packet;

import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.item.ItemTorchBelt;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.util.helper.InventoryHelper;
import com.tm.calemiutils.util.helper.SoundHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkEvent;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;
import java.util.function.Supplier;

public class PacketToggleTorchBelt {

    /**
     * Currently called by the key input.
     * Toggles all Torch Belts in the player's inventory on the server.
     */
    public PacketToggleTorchBelt () {}

    public PacketToggleTorchBelt (PacketBuffer buf) {}

    public void toBytes (PacketBuffer buf) {}

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                List<ItemStack> torchBelts = InventoryHelper.findItems(player.inventory, InitItems.TORCH_BELT.get());

                if (CalemiUtils.curiosLoaded) {

                    if (CuriosApi.getCuriosHelper().findEquippedCurio(InitItems.TORCH_BELT.get(), player).isPresent()) {
                        ItemStack curiosStack = CuriosApi.getCuriosHelper().findEquippedCurio(InitItems.TORCH_BELT.get(), player).get().getRight();
                        torchBelts.add(curiosStack);
                    }
                }

                //Checks if there is at least one Torch Belt.
                if (torchBelts.size() > 0) {

                    boolean active = ItemTorchBelt.isActive(torchBelts.get(0));

                    //Toggle all Torch Belts in the player's inventory.
                    for (ItemStack torchBelt : torchBelts) {
                        ItemTorchBelt.setActive(torchBelt, player.world, player, !active);
                    }

                    SoundHelper.playClick(player.world, player);
                    ItemTorchBelt.getMessage(player).printMessage(TextFormatting.GREEN, "All Torch Belts in your inventory have been turned " + (active ? "off." : "on."));
                }

                else {
                    ItemTorchBelt.getMessage(player).printMessage(TextFormatting.RED, "You are not holding any Torch Belts!");
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
