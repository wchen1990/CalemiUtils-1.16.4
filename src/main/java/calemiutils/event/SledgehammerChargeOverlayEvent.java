package calemiutils.event;

import calemiutils.item.ItemSledgehammer;
import calemiutils.util.helper.MathHelper;
import calemiutils.util.helper.ScreenHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SledgehammerChargeOverlayEvent {

    /**
     * Handles the Sledgehammer's charge overlay.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void render (RenderGameOverlayEvent.Post event) {

        //Checks if the current render is on the "TEXT" layer.
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {

            Minecraft mc = Minecraft.getInstance();
            World world = mc.world;
            ClientPlayerEntity player = mc.player;

            //Checks if the Player exists.
            if (player != null) {

                ItemStack activeItemStack = player.getActiveItemStack();

                int scaledWidth = mc.getMainWindow().getScaledWidth();
                int scaledHeight = mc.getMainWindow().getScaledHeight();
                int midX = scaledWidth / 2;
                int midY = scaledHeight / 2;

                //Checks if the active Item is a Sledgehammer. Active Items are the ones being used; like charging a bow.
                if (activeItemStack.getItem() instanceof ItemSledgehammer) {

                    ItemSledgehammer sledgehammer = (ItemSledgehammer) activeItemStack.getItem();

                    int currentChargeTime = net.minecraft.util.math.MathHelper.clamp(player.getItemInUseMaxCount(), 0, player.getItemInUseMaxCount());
                    int chargeTime = sledgehammer.chargeTime;
                    int hammerIconWidth = 13;
                    int scaledChargeTime = MathHelper.scaleInt(currentChargeTime, chargeTime, hammerIconWidth);

                    ScreenHelper.bindGuiTextures();

                    //If the Sledgehammer is charging, render the loading charge overlay.
                    if (currentChargeTime < chargeTime) {

                        ScreenHelper.drawRect(midX - 7, midY - 11, 0, 87, 0, hammerIconWidth, 4);
                        ScreenHelper.drawRect(midX - 7, midY - 11, hammerIconWidth, 87, 5, scaledChargeTime, 4);
                    }

                    //If the Sledgehammer is ready, render the flashing ready overlay.
                    else {

                        if (player.world.getGameTime() % 5 > 1) {
                            ScreenHelper.drawRect(midX - 7, midY - 11, hammerIconWidth * 2, 87, 10, hammerIconWidth, 4);
                        }
                    }
                }
            }
        }
    }
}
