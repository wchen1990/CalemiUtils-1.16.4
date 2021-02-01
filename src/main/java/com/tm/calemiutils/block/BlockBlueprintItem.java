package com.tm.calemiutils.block;

import com.tm.calemiutils.block.base.BlockItemBase;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.util.helper.ItemHelper;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockBlueprintItem extends BlockItemBase {

    public BlockBlueprintItem() {
        super(InitItems.BLUEPRINT.get(), false);
    }
}
