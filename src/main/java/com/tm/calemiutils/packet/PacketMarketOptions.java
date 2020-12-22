package com.tm.calemiutils.packet;

import com.tm.calemiutils.tileentity.TileEntityMarket;
import com.tm.calemiutils.util.Location;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMarketOptions {

    private String command;
    private BlockPos pos;
    private boolean buyMode;
    private boolean automationMode;
    private int selectedIndex;
    private int purchaseAmount;

    public PacketMarketOptions () {}

    public PacketMarketOptions (String command, BlockPos pos, boolean buyMode, boolean automationMode, int selectedIndex, int purchaseAmount) {
        this.command = command;
        this.pos = pos;
        this.buyMode = buyMode;
        this.automationMode = automationMode;
        this.selectedIndex = selectedIndex;
        this.purchaseAmount = purchaseAmount;
    }

    public PacketMarketOptions (PacketBuffer buf) {
        command = buf.readString(20).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        buyMode = buf.readBoolean();
        automationMode = buf.readBoolean();
        selectedIndex = buf.readInt();
        purchaseAmount = buf.readInt();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeString(command, 20);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeBoolean(buyMode);
        buf.writeBoolean(automationMode);
        buf.writeInt(selectedIndex);
        buf.writeInt(purchaseAmount);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.world, pos);

                if (location.getTileEntity() instanceof TileEntityMarket) {

                    TileEntityMarket market = (TileEntityMarket) location.getTileEntity();

                    if (command.equalsIgnoreCase("syncBuyMode")) {
                        market.buyMode = buyMode;
                    }

                    else if (command.equalsIgnoreCase("syncAutomationMode")) {
                        market.automationMode = automationMode;
                    }

                    else if (command.equalsIgnoreCase("syncSelectedIndex")) {
                        market.selectedIndex = selectedIndex;
                    }

                    else if (command.equalsIgnoreCase("syncPurchaseAmount")) {
                        market.purchaseAmount = purchaseAmount;
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
