package calemiutils.util.helper;

import calemiutils.util.Location;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHelper {

    //The maximum size of Blocks to search in a row.
    private static final int MAX_ROW_SIZE = 64;

    /**
     * Places a block in the next available place in a row.
     * @param block The Block that gets placed.
     * @param originPos The origin of the search.
     * @param rowDirection The Direction of the row.
     */
    public static void placeBlockRow(World world, PlayerEntity player, Block block, BlockPos originPos, Direction rowDirection) {

        Location location = new Location(world, originPos);
        ItemStack currentStack = player.getHeldItemMainhand();

        //Checks if the held stack is a Block.
        if (currentStack.getItem() == Item.getItemFromBlock(block)) {

            //Iterates through every possible placement in a line.
            for (int i = 0; i < MAX_ROW_SIZE; i++) {

                Location nextLocation = new Location(location, rowDirection, i);

                //Checks if a different Block/AIR has been found.
                if (nextLocation.getBlock() != block) {

                    //Checks if the Block be placed here.
                    if (nextLocation.isBlockValidForPlacing()) {

                        nextLocation.setBlock(block);
                        SoundHelper.playBlockPlaceSound(world, player, nextLocation.getForgeBlockState(), nextLocation);
                        InventoryHelper.consumeStack(player.inventory, 1, false, new ItemStack(block));
                    }

                    break;
                }
            }
        }
    }

    public static boolean canPlaceTorchAt (Location location) {

        if (!location.isBlockValidForPlacing()) {
            return false;
        }

        if (location.getLightValue() > 7) {
            return false;
        }

        if (location.getBlock() instanceof FlowingFluidBlock) {
            return false;
        }

        Location locationDown = new Location(location, Direction.DOWN);

        return locationDown.isFullCube();
    }
}
