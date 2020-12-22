package calemiutils.event;

import calemiutils.item.ItemCoin;
import calemiutils.util.helper.SoundHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CoinPickupSoundEvent {

    /**
     * Handles adding sound effect when coin is picked up.
     */
    @SubscribeEvent
    public void onItemPickup (PlayerEvent.ItemPickupEvent event) {

        if (event.getEntity() instanceof PlayerEntity) {

            if (event.getStack().getItem() instanceof ItemCoin) {

                SoundHelper.playCoin(event.getOriginalEntity().world, (PlayerEntity) event.getEntity());
            }
        }
    }
}
