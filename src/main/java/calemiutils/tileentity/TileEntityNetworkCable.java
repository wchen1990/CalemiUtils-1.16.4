package calemiutils.tileentity;

import calemiutils.init.InitTileEntityTypes;
import calemiutils.security.ISecurity;
import calemiutils.security.SecurityProfile;
import calemiutils.tileentity.base.INetwork;
import calemiutils.tileentity.base.TileEntityBase;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;

public class TileEntityNetworkCable extends TileEntityBase implements INetwork, ISecurity {

    private final SecurityProfile profile = new SecurityProfile();

    public TileEntityNetworkCable (TileEntityType type) {
        super(type);
    }

    public TileEntityNetworkCable () {
        super(InitTileEntityTypes.NETWORK_CABLE.get());
    }

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    @Override
    public Direction[] getConnectedDirections () {
        return Direction.values();
    }
}
