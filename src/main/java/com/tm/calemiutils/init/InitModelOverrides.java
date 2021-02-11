package com.tm.calemiutils.init;

import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;

public class InitModelOverrides {

    public static void init() {
        ItemModelsProperties.registerProperty(InitItems.BLUEPRINT_ITEM.get(), new ResourceLocation("color"), (stack, world, player) -> stack.getDamage());
    }
}
