package calemiutils.block;

import calemiutils.block.base.BlockBase;
import calemiutils.util.Location;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class BlockCoinStack extends BlockBase {

    private static final VoxelShape AABB = Block.makeCuboidShape(1, 0, 1, 15, 16, 15);

    public BlockCoinStack () {
        super(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(1).doesNotBlockMovement().notSolid().variableOpacity());
    }

    @Override
    public void tick(BlockState state, ServerWorld serverWorld, BlockPos pos, Random rand) {
        if (serverWorld.isAreaLoaded(pos, 1)) {
            if (!state.isValidPosition(serverWorld, pos)) {
                serverWorld.destroyBlock(pos, true);
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {

        if (!stateIn.isValidPosition(world, currentPos)) {
            world.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
        }

        return super.updatePostPlacement(stateIn, facing, facingState, world, currentPos, facingPos);
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {

        if (world instanceof World) {
            Location location = new Location((World)world, pos);
            Location locationDown = location.translate(Direction.DOWN, 1);
            return locationDown.getBlock() == this || locationDown.doesBlockHaveCollision();
        }

        return true;
    }

    @Override
    public VoxelShape getShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    @Override
    public VoxelShape getCollisionShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public boolean propagatesSkylightDown (BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }
}
