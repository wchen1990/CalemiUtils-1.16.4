package calemiutils.block;

import calemiutils.block.base.BlockBase;
import calemiutils.config.CUConfig;
import calemiutils.init.InitItems;
import calemiutils.util.Location;
import calemiutils.util.VeinScan;
import calemiutils.util.helper.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class BlockScaffold extends BlockBase {

    public BlockScaffold () {
        super(Block.Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(0.1F).harvestLevel(0).harvestTool(ToolType.PICKAXE).notSolid().variableOpacity());
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Temporary block used for getting to the hard to reach places!", true);
        LoreHelper.addControlsLore(tooltip, "On block side - Teleport to the top. On block top - Teleport to bottom.", LoreHelper.Type.USE_OPEN_HAND, true);
        LoreHelper.addControlsLore(tooltip, "Break all connected scaffolds", LoreHelper.Type.SNEAK_BREAK_BLOCK);
        LoreHelper.addControlsLore(tooltip, "Place Scaffold in a line", LoreHelper.Type.LEFT_CLICK_BLOCK);
        LoreHelper.addControlsLore(tooltip, "Place Scaffold downwards", LoreHelper.Type.SNEAK_LEFT_CLICK_BLOCK);
    }

    /**
     * Handles teleportation.
     */
    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        //Prevents client side.
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }

        ItemStack heldStack = player.getHeldItemMainhand();

        //Checks if the held stack is not a Block.
        if (!(heldStack.getItem() instanceof BlockItem)) {

            Location clickedLocation = new Location(world, pos);

            if (CUConfig.misc.scaffoldMaxHeightTp.get() == 0) {
                return ActionResultType.FAIL;
            }

            //Handles the teleportation up.
            if (result.getFace() != Direction.UP) {

                //Iterates through all Blocks above the Scaffold. The size is controlled by the config option.
                for (int i = 0; i < CUConfig.misc.scaffoldMaxHeightTp.get(); i++) {

                    Location nextLocation = new Location(clickedLocation, Direction.UP, i);
                    Location nextLocationUp = new Location(clickedLocation, Direction.UP, i + 1);

                    //If the current Location is not a Scaffold, stop all iterating and check further.
                    if (nextLocation.getBlock() != this) {

                        //Checks if the current Location is safe to teleport to.
                        if (EntityHelper.canTeleportAt((ServerPlayerEntity) player, nextLocation)) {
                            EntityHelper.teleportPlayer((ServerPlayerEntity) player, nextLocation);
                            return ActionResultType.SUCCESS;
                        }

                        return ActionResultType.FAIL;
                    }
                }
            }

            //Handles the teleportation down.
            else {

                //Iterates through all Blocks below the Scaffold. The size is controlled by the config option.
                for (int i = 1; i < CUConfig.misc.scaffoldMaxHeightTp.get(); i++) {

                    Location nextLocation = new Location(clickedLocation, Direction.DOWN, i);
                    Location nextLocationDown = new Location(clickedLocation, Direction.DOWN, i + 1);

                    //If the current Location is not a Scaffold, stop all iterating and check further.
                    if (nextLocationDown.getBlock() != this) {

                        //Iterates through all the horizontal Locations.
                        for (Direction dir : DirectionHelper.HORIZONTAL_DIRECTIONS) {

                            Location sqrLocation = new Location(nextLocation, dir);

                            //If the current Location is not a Scaffold, stop all iterating and check further.
                            if (sqrLocation.getBlock() != this) {

                                if (EntityHelper.canTeleportAt((ServerPlayerEntity) player, sqrLocation)) {
                                    EntityHelper.teleportPlayer((ServerPlayerEntity) player, sqrLocation);
                                    return ActionResultType.SUCCESS;
                                }
                            }
                        }

                        return ActionResultType.FAIL;
                    }
                }
            }
        }

        return ActionResultType.FAIL;
    }

    /**
     * Handles the Scaffold placement system.
     */
    @Override
    public void onBlockClicked (BlockState state, World world, BlockPos pos, PlayerEntity player) {

        //If the Player is crouching, then place the block horizontally.
        if (!player.isCrouching()) {
            BlockHelper.placeBlockRow(world, player, this, pos, DirectionHelper.getPlayerHorizontalDirection(player));
        }

        //If the Player is not crouching, then place the block downwards.
        else {
            BlockHelper.placeBlockRow(world, player, this, pos, Direction.DOWN);
        }
    }

    /**
     * Handles the Scaffold breaking system.
     */
    @Override
    public void onBlockHarvested (World world, BlockPos pos, BlockState state, PlayerEntity player) {

        //Prevents client side.
        if (world.isRemote) {
            return;
        }

        Location location = new Location(world, pos);

        //If the Player is Crouching, then break all connected Scaffolds.
        if (player.isCrouching()) {

            //Starts a scan of all connected Scaffolds.
            VeinScan scan = new VeinScan(location, this);
            scan.startScan();

            //Iterates through the Locations generated by the scan.
            for (Location nextLocation : scan.buffer) {

                //Set the current Block at the Location to air.
                nextLocation.setBlockToAir();

                //If the Player is not in Creative Mode, then give them the drops of the Block.
                if (!player.abilities.isCreativeMode) {
                    ItemHelper.spawnStackAtLocation(world, new Location(player), new ItemStack(InitItems.IRON_SCAFFOLD.get()));
                }
            }
        }

        //If the Player is not Crouching, then only break on Scaffold.
        else {

            //Set the current Block at the Location to air.
            location.setBlockToAir();

            //If the Player is not in Creative Mode, then give them the drops of the Block.
            if (!player.abilities.isCreativeMode) {
                ItemHelper.spawnStackAtLocation(world, new Location(player), new ItemStack(InitItems.IRON_SCAFFOLD.get()));
            }
        }
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible (BlockState centerBlockState, BlockState otherStateBlock, Direction dir) {
        return otherStateBlock.getBlock() == this || super.isSideInvisible(centerBlockState, otherStateBlock, dir);
    }

    @Override
    public boolean propagatesSkylightDown (BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }
}
