package com.tm.calemiutils.util.helper;

import com.tm.calemiutils.tileentity.TileEntityBank;
import com.tm.calemiutils.util.Location;

public class NetworkHelper {

    public static TileEntityBank getConnectedBank (Location unitLocation, Location bankLocation) {

        if (bankLocation != null && bankLocation.getTileEntity() instanceof TileEntityBank) {

            TileEntityBank bank = (TileEntityBank) bankLocation.getTileEntity();

            if (bank.enable) {

                if (bank.connectedUnits.contains(unitLocation)) {
                    return bank;
                }
            }
        }

        return null;
    }
}
