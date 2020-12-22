package com.tm.calemiutils.block.base;

import com.tm.calemiutils.CalemiUtils;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * The base class for Items that place Blocks.
 */
public class BlockItemBase extends BlockItem {

    public BlockItemBase (Block block, boolean onCreativeTab) {
        super(block, onCreativeTab ? new Item.Properties().group(CalemiUtils.TAB) : new Item.Properties());
    }

    public BlockItemBase (Block block) {
        this(block, true);
    }
}
