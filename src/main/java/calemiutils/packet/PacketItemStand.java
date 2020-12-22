package calemiutils.packet;

import calemiutils.block.BlockItemStand;
import calemiutils.tileentity.TileEntityItemStand;
import calemiutils.util.Location;
import calemiutils.util.helper.PacketHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketItemStand {

    private String command;
    private BlockPos pos;
    private int displayId;
    private Vector3f translation, rotation, spin, scale, pivot;

    public PacketItemStand () {}

    /**
     * Handles syncing the options of the Item Stand.
     * @param command Used to determine the type of packet to send.
     * @param pos The Block position of the Item Stand.
     * @param displayId The type of display.
     * @param translation The translation option.
     * @param rotation The rotation option.
     * @param spin The spin option.
     * @param scale The scale option.
     * @param pivot The pivot option.
     */
    public PacketItemStand (String command, BlockPos pos, int displayId, Vector3f translation, Vector3f rotation, Vector3f spin, Vector3f scale, Vector3f pivot) {
        this.command = command;
        this.pos = pos;
        this.displayId = displayId;
        this.translation = translation;
        this.rotation = rotation;
        this.spin = spin;
        this.scale = scale;
        this.pivot = pivot;
    }

    /**
     * Use this constructor for syncing display.
     */
    public PacketItemStand (String command, BlockPos pos, int displayId) {
        this(command, pos, displayId, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0));
    }

    /**
     * Use this constructor for syncing options.
     */
    public PacketItemStand (String command, BlockPos pos, Vector3f translation, Vector3f rotation, Vector3f spin, Vector3f scale, Vector3f pivot) {
        this(command, pos, 0, translation, rotation, spin, scale, pivot);
    }

    public PacketItemStand (PacketBuffer buf) {
        command = buf.readString(11).trim();
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.displayId = buf.readInt();
        translation = PacketHelper.readVector(buf);
        rotation = PacketHelper.readVector(buf);
        spin = PacketHelper.readVector(buf);
        scale = PacketHelper.readVector(buf);
        pivot = PacketHelper.readVector(buf);
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeString(command, 11);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeInt(displayId);
        PacketHelper.writeVector(buf, translation);
        PacketHelper.writeVector(buf, rotation);
        PacketHelper.writeVector(buf, spin);
        PacketHelper.writeVector(buf, scale);
        PacketHelper.writeVector(buf, pivot);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.world, pos);

                //Checks if the Tile Entity is an Item Stand.
                if (location.getTileEntity() instanceof TileEntityItemStand) {

                    TileEntityItemStand stand = (TileEntityItemStand) location.getTileEntity();

                    //Handles syncing the display of the Item Stand.
                    if (command.equalsIgnoreCase("syncdisplay")) {
                        location.setBlock(stand.getBlockState().with(BlockItemStand.DISPLAY_ID, displayId));
                        location.world.setTileEntity(location.getBlockPos(), stand);
                    }

                    //Handles syncing the options of the Item Stand.
                    if (command.equalsIgnoreCase("syncoptions")) {
                        stand.translation = translation;
                        stand.rotation = rotation;
                        stand.spin = spin;
                        stand.scale = scale;
                        stand.pivot = pivot;
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
