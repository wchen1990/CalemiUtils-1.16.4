package com.tm.calemiutils.tileentity;

import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.security.SecurityProfile;
import com.tm.calemiutils.tileentity.base.INetwork;
import com.tm.calemiutils.tileentity.base.TileEntityBase;
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
