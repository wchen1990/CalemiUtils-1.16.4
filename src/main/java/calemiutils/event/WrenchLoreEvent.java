package calemiutils.event;

import calemiutils.util.helper.ItemHelper;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WrenchLoreEvent {

    /**
     * Handles adding Lore to an Item storing currency.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onLoreEvent (ItemTooltipEvent event) {

        if (event.getItemStack().getTag() != null) {

            int currency = ItemHelper.getNBT(event.getItemStack()).getInt("currency");

            if (currency != 0) {
                event.getToolTip().add(new StringTextComponent(""));
                LoreHelper.addCurrencyLore(event.getToolTip(), currency);
            }
        }
    }
}
