package com.tm.calemiutils.event;

import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.tileentity.base.TileEntityBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.SecurityHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SecurityEvent {

    /**
     * Sets the owner of a secured Block when placed.
     */
    @SubscribeEvent
    public void onBlockPlace (BlockEvent.EntityPlaceEvent event) {

        TileEntity tileEntity = event.getWorld().getTileEntity(event.getPos());

        //Checks if the Entity is a Player and the Location is a TileEntityBase and implements ISecurity.
        if (event.getEntity() instanceof PlayerEntity && tileEntity instanceof TileEntityBase && tileEntity instanceof ISecurity) {

            ISecurity security = (ISecurity) tileEntity;

            security.getSecurityProfile().setOwner((PlayerEntity) event.getEntity());
            ((TileEntityBase) tileEntity).markForUpdate();
        }
    }

    @SubscribeEvent
    public void onBlockBreak (BlockEvent.BreakEvent event) {

        Location location = new Location(event.getPlayer().world, event.getPos());

        if (!SecurityHelper.canUseSecuredBlock(location, event.getPlayer(), true)) {
            event.setCanceled(true);
        }
    }
}
