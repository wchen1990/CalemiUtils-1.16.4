package com.tm.calemiutils.block;

import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.LoreHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockNetworkGate extends BlockNetworkCableOpaque {

    public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");

    public BlockNetworkGate () {
        setDefaultState(stateContainer.getBaseState().with(CONNECTED, true));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Used to enable and disable networks branches by redstone signal.", true);
    }

    /**
     * Changes the Block's "CONNECTED" property by the value.
     */
    public static void setState (boolean value, World worldIn, BlockPos pos) {

        TileEntity tileentity = worldIn.getTileEntity(pos);
        Location location = new Location(worldIn, pos);

        location.setBlock(InitItems.NETWORK_GATE.get().getDefaultState().with(CONNECTED, value));

        if (tileentity != null) {
            tileentity.validate();
            worldIn.setTileEntity(pos, tileentity);
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity (IBlockReader worldIn) {
        return InitTileEntityTypes.NETWORK_GATE.get().create();
    }

    /*
        Methods for Block properties.
     */

    @Override
    public BlockState getStateForPlacement (BlockItemUseContext context) {
        return stateContainer.getBaseState().with(CONNECTED, true);
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(CONNECTED);
    }
}
