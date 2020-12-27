package com.tm.calemiutils.item;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.helper.LoreHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemUpgrade extends ItemBase {

    public ItemUpgrade () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(5));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Placed in certain machines to upgrade their abilities.", true);
    }
}
