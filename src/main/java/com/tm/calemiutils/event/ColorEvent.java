package com.tm.calemiutils.event;

import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.item.base.ItemPencilColored;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class ColorEvent {

    /**
     * Registers the coloring of the Pencil.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onColorRegister(final ColorHandlerEvent.Item event) {
        event.getItemColors().register(new ItemPencilColored(), InitItems.PENCIL.get());
    }
}
