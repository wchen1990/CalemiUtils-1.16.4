package calemiutils.util.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.MathHelper;

public class DirectionHelper {

    public static final Direction[] HORIZONTAL_DIRECTIONS = {Direction.SOUTH, Direction.WEST, Direction.NORTH, Direction.EAST};

    /**
     * Gets the horizontal Direction the Player is looking towards.
     */
    public static Direction getPlayerHorizontalDirection(PlayerEntity player) {
        int face = MathHelper.floor((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return HORIZONTAL_DIRECTIONS[face];
    }
}

