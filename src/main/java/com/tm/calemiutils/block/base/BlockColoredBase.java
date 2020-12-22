package com.tm.calemiutils.block.base;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.DyeColor;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;

/**
 * The base class for colored Blocks.
 */
public class BlockColoredBase extends Block {

    public static final EnumProperty<DyeColor> COLOR = CUBlockStates.COLOR;

    /**
     * @param properties The specific properties for the Block. (Creative Tab, hardness, material, etc.)
     */
    protected BlockColoredBase (Block.Properties properties) {
        super(properties);
        setDefaultState(this.stateContainer.getBaseState().with(COLOR, DyeColor.BLUE));
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(COLOR);
    }
}
