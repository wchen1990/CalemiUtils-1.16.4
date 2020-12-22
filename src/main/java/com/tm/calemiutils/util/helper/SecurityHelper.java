package com.tm.calemiutils.util.helper;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.UnitChatMessage;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

public class SecurityHelper {

    public static boolean openSecuredBlock (Location location, PlayerEntity player, boolean printError) {

        TileEntity tileEntity = location.getTileEntity();

        if (tileEntity instanceof ISecurity) {

            ISecurity security = (ISecurity) tileEntity;

            if (security.getSecurityProfile().isOwner(player.getName().getString()) || player.isCreative() || !CUConfig.misc.useSecurity.get()) {
                return true;
            }

            else if (printError) printErrorMessage(location, player);

            return false;
        }

        return true;
    }

    public static void printErrorMessage (Location location, PlayerEntity player) {

        if (player.world.isRemote) {
            UnitChatMessage message = new UnitChatMessage(location.getBlock().getTranslatedName().getString(), player);
            message.printMessage(TextFormatting.RED, "This unit doesn't belong to you!");
        }
    }
}
