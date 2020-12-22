package com.tm.calemiutils.tileentity;

import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.tileentity.base.TileEntityBase;

public class TileEntityMobBeacon extends TileEntityBase {

    public TileEntityMobBeacon () {
        super(InitTileEntityTypes.MOB_BEACON.get());
    }
}
