package com.tm.calemiutils.item;

import com.tm.calemiutils.block.base.BlockItemBase;
import com.tm.calemiutils.util.helper.StringHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCoin extends BlockItemBase {

    public final int value;

    public ItemCoin(int value, Block coinStack) {
        super(coinStack, true);
        this.value = value;
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        tooltipList.add(new StringTextComponent(TextFormatting.GRAY + "Value (1): " + TextFormatting.GOLD + StringHelper.printCurrency(value)));

        if (stack.getCount() > 1) {
            tooltipList.add(new StringTextComponent(TextFormatting.GRAY + "Value (" + stack.getCount() + "): " + TextFormatting.GOLD + StringHelper.printCurrency(value * stack.getCount())));
        }
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {

        ItemStack stack = context.getItem();

        if (context.getPlayer().isCreative() || stack.getCount() >= 8) {
            return tryPlace(new BlockItemUseContext(context));
        }

        return ActionResultType.FAIL;
    }

    @Override
    protected boolean placeBlock(BlockItemUseContext context, BlockState state) {
        context.getItem().shrink(7);
        return context.getWorld().setBlockState(context.getPos(), state, 26);
    }
}
