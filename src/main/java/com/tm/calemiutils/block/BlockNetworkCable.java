package com.tm.calemiutils.block;

import com.tm.calemiutils.tileentity.base.INetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;

import java.util.ArrayList;
import java.util.List;

public class BlockNetworkCable extends BlockNetworkCableOpaque {

    private static final BooleanProperty UP = BooleanProperty.create("up");
    private static final BooleanProperty DOWN = BooleanProperty.create("down");
    private static final BooleanProperty NORTH = BooleanProperty.create("north");
    private static final BooleanProperty EAST = BooleanProperty.create("east");
    private static final BooleanProperty SOUTH = BooleanProperty.create("south");
    private static final BooleanProperty WEST = BooleanProperty.create("west");
    private static final BooleanProperty DOWNUP = BooleanProperty.create("downup");
    private static final BooleanProperty NORTHSOUTH = BooleanProperty.create("northsouth");
    private static final BooleanProperty EASTWEST = BooleanProperty.create("eastwest");

    private static final VoxelShape CORE_AABB = makeCuboidShape(5, 5, 5, 11, 11, 11);
    private static final VoxelShape DOWN_AABB = makeCuboidShape(5, 0, 5, 11, 11, 11);
    private static final VoxelShape UP_AABB = makeCuboidShape(5, 5, 5, 11, 16, 11);
    private static final VoxelShape NORTH_AABB = makeCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape EAST_AABB = makeCuboidShape(5, 5, 5, 16, 11, 11);
    private static final VoxelShape SOUTH_AABB = makeCuboidShape(5, 5, 5, 11, 11, 16);
    private static final VoxelShape WEST_AABB = makeCuboidShape(0, 5, 5, 11, 11, 11);
    private static final VoxelShape DOWNUP_AABB = makeCuboidShape(5, 0, 5, 11, 16, 11);
    private static final VoxelShape NORTHSOUTH_AABB = makeCuboidShape(5, 5, 0, 11, 11, 16);
    private static final VoxelShape EASTWEST_AABB = makeCuboidShape(0, 5, 5, 16, 11, 11);

    public BlockNetworkCable () {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(-1.0F, 3600000.0F).harvestLevel(0).notSolid().variableOpacity());
        setDefaultState(stateContainer.getBaseState().with(UP, false).with(DOWN, false).with(NORTH, false).with(EAST, false).with(SOUTH, false).with(WEST, false).with(DOWNUP, false).with(NORTHSOUTH, false).with(EASTWEST, false));
    }

    /**
     * Checks if the Block at the given pos can connect to the Block given by the Direction.
     */
    private boolean canCableConnectTo (IBlockReader world, BlockPos pos, Direction facing) {
        BlockPos otherPos = pos.offset(facing);
        Block block = world.getBlockState(pos).getBlock();
        Block otherBlock = world.getBlockState(otherPos).getBlock();

        return canBeConnectedTo(world, otherPos, facing.getOpposite());
    }

    /**
     * Checks if the INetwork at the given pos contains a connectable position that is the same as the given.
     */
    public boolean canBeConnectedTo(IBlockReader world, BlockPos pos, Direction facing) {

        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof INetwork) {

            INetwork network = (INetwork) tileEntity;

            for (Direction dir : network.getConnectedDirections()) {

                if (facing == dir) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
        Methods for Block properties.
     */

    @Override
    public BlockState getStateForPlacement (BlockItemUseContext context) {
        return getState(context.getWorld(), context.getPos());
    }

    private BlockState getState (IWorld world, BlockPos pos) {

        boolean down = canCableConnectTo(world, pos, Direction.DOWN);
        boolean up = canCableConnectTo(world, pos, Direction.UP);
        boolean north = canCableConnectTo(world, pos, Direction.NORTH);
        boolean east = canCableConnectTo(world, pos, Direction.EAST);
        boolean south = canCableConnectTo(world, pos, Direction.SOUTH);
        boolean west = canCableConnectTo(world, pos, Direction.WEST);

        boolean downup = down && up && (!north && !east && !south && !west);
        boolean northsouth = north && south && (!down && !up && !east && !west);
        boolean eastwest = east && west && (!north && !south && !down && !up);

        if (downup || northsouth || eastwest) {
            down = false;
            up = false;
            north = false;
            east = false;
            south = false;
            west = false;
        }

        return getDefaultState().with(DOWN, down).with(UP, up).with(NORTH, north).with(EAST, east).with(SOUTH, south).with(WEST, west).with(DOWNUP, downup).with(NORTHSOUTH, northsouth).with(EASTWEST, eastwest);
    }

    @Override
    public BlockState updatePostPlacement (BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos pos, BlockPos facingPos) {
        return getState(world, pos);
    }

    @Override
    protected void fillStateContainer (StateContainer.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, DOWNUP, NORTHSOUTH, EASTWEST);
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    private VoxelShape getCollision (BlockState state) {

        List<VoxelShape> collidingBoxes = new ArrayList<>();

        if (state.get(DOWN)) collidingBoxes.add(DOWN_AABB);
        if (state.get(UP)) collidingBoxes.add(UP_AABB);
        if (state.get(NORTH)) collidingBoxes.add(NORTH_AABB);
        if (state.get(EAST)) collidingBoxes.add(EAST_AABB);
        if (state.get(SOUTH)) collidingBoxes.add(SOUTH_AABB);
        if (state.get(WEST)) collidingBoxes.add(WEST_AABB);

        if (state.get(DOWNUP)) collidingBoxes.add(DOWNUP_AABB);
        if (state.get(NORTHSOUTH)) collidingBoxes.add(NORTHSOUTH_AABB);
        if (state.get(EASTWEST)) collidingBoxes.add(EASTWEST_AABB);

        VoxelShape[] shapes = new VoxelShape[collidingBoxes.size()];

        for (int i = 0; i < shapes.length; i++) {
            shapes[i] = collidingBoxes.get(i);
        }

        return VoxelShapes.or(CORE_AABB, shapes);
    }

    @Override
    public VoxelShape getShape (BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getCollision(state);
    }

    @Override
    public VoxelShape getCollisionShape (BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getCollision(state);
    }

    @Override
    public boolean propagatesSkylightDown (BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }
}
