package calemiutils.event;

import calemiutils.security.ISecurity;
import calemiutils.tileentity.base.TileEntityBase;
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
}
