package calemiutils.tileentity.base;

import calemiutils.security.ISecurity;
import calemiutils.util.Location;
import calemiutils.util.UnitChatMessage;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nullable;

public abstract class TileEntityBase extends TileEntity implements ITickableTileEntity {

    public boolean enable;

    public TileEntityBase (final TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        enable = true;
    }

    @Override
    public void tick () {

    }

    protected UnitChatMessage getUnitName (PlayerEntity player) {
        return new UnitChatMessage(getLocation().getBlock().getTranslatedName().getString(), player);
    }

    public Location getLocation () {
        return new Location(world, pos);
    }

    public void markForUpdate () {

        if (world != null) {
            markDirty();
            world.addBlockEvent(getPos(), getBlockState().getBlock(), 1, 1);
            world.notifyBlockUpdate(getPos(), getBlockState(), getBlockState(), 0);
            world.notifyNeighborsOfStateChange(getPos(), getBlockState().getBlock());
        }
    }

    @Override
    public void onDataPacket (NetworkManager net, SUpdateTileEntityPacket pkt) {
        read(getBlockState(), pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        read(state, tag);
    }

    @Override
    public void read (BlockState state, CompoundNBT nbt) {

        if (this instanceof ISecurity) {

            ISecurity security = (ISecurity) this;
            security.getSecurityProfile().readFromNBT(nbt);
        }

        if (this instanceof ICurrencyNetworkBank) {

            ICurrencyNetworkBank currency = (ICurrencyNetworkBank) this;
            currency.setCurrency(nbt.getInt("currency"));
        }

        enable = nbt.getBoolean("enable");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write (CompoundNBT nbt) {

        if (this instanceof ISecurity) {

            ISecurity security = (ISecurity) this;
            security.getSecurityProfile().writeToNBT(nbt);
        }

        if (this instanceof ICurrencyNetworkBank) {

            ICurrencyNetworkBank currency = (ICurrencyNetworkBank) this;
            nbt.putInt("currency", currency.getStoredCurrency());
        }

        nbt.putBoolean("enable", enable);
        return super.write(nbt);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket () {
        CompoundNBT nbtTagCompound = new CompoundNBT();
        write(nbtTagCompound);
        int tileEntityType = 64;
        return new SUpdateTileEntityPacket(getPos(), tileEntityType, nbtTagCompound);
    }

    @Override
    public CompoundNBT getUpdateTag () {
        CompoundNBT nbt = new CompoundNBT();
        write(nbt);
        return nbt;
    }

    @Override
    public CompoundNBT getTileData () {
        CompoundNBT nbt = new CompoundNBT();
        write(nbt);
        return nbt;
    }
}
