package com.tm.calemiutils.util.helper;

import com.tm.calemiutils.util.Location;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class RayTraceHelper {

    public static BlockTrace RayTraceBlock(World world, PlayerEntity player, Hand hand) {

        Vector3d playerPosVec = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
        Vector3d playerLookVec = player.getLookVec();
        Direction playerLookDir = Direction.getFacingFromVector(playerLookVec.x, playerLookVec.y, playerLookVec.z);

        BlockRayTraceResult rayTrace = world.rayTraceBlocks(new RayTraceContext(playerPosVec, playerPosVec.add(playerLookVec.scale(5)), RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        ItemUseContext itemUseContext = new ItemUseContext(player, hand, rayTrace);
        BlockItemUseContext blockUseContext = new BlockItemUseContext(itemUseContext);

        if (rayTrace.getType() == RayTraceResult.Type.BLOCK) {

            BlockPos pos = blockUseContext.getPos();
            Location locationOffset = new Location(world, pos);

            BlockPos difference = locationOffset.getBlockPos().subtract(itemUseContext.getPos());
            Direction blockSide = Direction.getFacingFromVector(difference.getX(), difference.getY(), difference.getZ());

            Location locationReal = locationOffset.translate(blockSide.getOpposite(), 1);

            return new BlockTrace(locationOffset, blockSide);
        }

        return null;
    }

    public static class BlockTrace {

        private final Location hit;
        private final Direction hitSide;

        public BlockTrace(Location hit, Direction hitSide) {
            this.hit = hit;
            this.hitSide = hitSide;
        }

        public Location getHit () {
            return hit;
        }

        public Direction getHitSide () {
            return hitSide;
        }
    }
}
