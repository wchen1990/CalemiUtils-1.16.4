package com.tm.calemiutils.packet;

import com.tm.calemiutils.tileentity.TileEntityBlueprintFiller;
import com.tm.calemiutils.util.Location;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketBlueprintFiller {

    private String command;
    private BlockPos pos;
    private boolean enable;
    private ItemStack filter;
    private int filterIndex;

    public PacketBlueprintFiller() {}

    /**
     * Handles syncing the state of the "enable" and "scan" value to the Blueprint Filler.
     * @param enable The Tile Entity's enable state.
     * @param pos The Block position of the Tile Entity.
     */
    public PacketBlueprintFiller(String command, BlockPos pos, boolean enable, ItemStack filter, int filterIndex) {
        this.command = command;
        this.pos = pos;
        this.enable = enable;
        this.filter = filter;
        this.filterIndex = filterIndex;
    }

    public PacketBlueprintFiller(String command, BlockPos pos) {
        this(command, pos, false, ItemStack.EMPTY, 0);
    }

    public PacketBlueprintFiller(String command, BlockPos pos, boolean enable) {
        this(command, pos, enable, ItemStack.EMPTY, 0);
    }

    public PacketBlueprintFiller(String command, BlockPos pos, ItemStack filter, int filterIndex) {
        this(command, pos, false, filter, filterIndex);
    }

    public PacketBlueprintFiller(PacketBuffer buf) {
        command = buf.readString(11).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        enable = buf.readBoolean();
        filter = buf.readItemStack();
        filterIndex = buf.readInt();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeString(command, 11);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeBoolean(enable);
        buf.writeItemStack(filter);
        buf.writeInt(filterIndex);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.world, pos);

                //Checks if the Tile Entity at the given Location is a Tile Entity Base.
                if (location.getTileEntity() instanceof TileEntityBlueprintFiller) {

                    TileEntityBlueprintFiller filler = (TileEntityBlueprintFiller) location.getTileEntity();

                    if (command.equalsIgnoreCase("syncenable")) {
                        filler.setEnable(enable);
                    }

                    else if (command.equalsIgnoreCase("startscan")) {
                        filler.startScan();
                    }

                    else if (command.equalsIgnoreCase("syncfilter")) {
                        filler.setFilter(filterIndex, filter);
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
