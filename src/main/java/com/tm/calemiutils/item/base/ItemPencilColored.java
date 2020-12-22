package com.tm.calemiutils.item.base;

import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.item.ItemPencil;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;

/**
 * Used by the Pencil to register its colors.
 */
public class ItemPencilColored implements IItemColor {

    @Override
    public int getColor (ItemStack stack, int tintLayer) {

        ItemPencil pencil = (ItemPencil) InitItems.PENCIL.get();

        if (tintLayer == 1) {
            int colorMeta = pencil.getColorId(stack);
            DyeColor dye = DyeColor.byId(colorMeta);

            return dye.getColorValue();
        }

        return 0xFFFFFF;
    }
}
