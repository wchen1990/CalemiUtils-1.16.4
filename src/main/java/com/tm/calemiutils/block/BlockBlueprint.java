package com.tm.calemiutils.block;

import com.tm.calemiutils.block.base.BlockColoredBase;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.UnitChatMessage;
import com.tm.calemiutils.util.VeinScan;
import com.tm.calemiutils.util.helper.InventoryHelper;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.SoundHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeBlockState;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

public class BlockBlueprint extends BlockColoredBase {

    public BlockBlueprint () {
        super(Block.Properties.create(Material.GLASS).sound(SoundType.STONE).hardnessAndResistance(0.1F).harvestLevel(0).notSolid().variableOpacity());
    }

    /**
     * Handles the Blueprint's replacement system.
     */
    @Override
    public void onBlockClicked (BlockState state, World world, BlockPos pos, PlayerEntity player) {

        Location location = new Location(world, pos);
        ItemStack heldStack = player.getHeldItemMainhand();
        UnitChatMessage message = new UnitChatMessage("Blueprint", player);

        //Creates a scanner which will search through multiple Blueprints.
        VeinScan scan = new VeinScan(location, location.getForgeBlockState());
        scan.startScan();

        //Checking if there the held stack exists and that it's not a Blueprint.
        if (!heldStack.isEmpty() && heldStack.getItem() != Item.getItemFromBlock(this)) {

            //Checking if the held stack is a Block.
            if (heldStack.getItem() instanceof BlockItem) {
                replaceAllBlocks(world, player, location, heldStack, scan, message);
            }
        }

        //Checking if the held stack is air and if the Player is crouching.
        else if (!world.isRemote && player.isCrouching() && heldStack.isEmpty()) {

            //Checking if the VeinScan list exceeds the config option. If so, print the max scan size.
            if (scan.buffer.size() >= CUConfig.blockScans.veinScanMaxSize.get()) {
                message.printMessage(TextFormatting.GREEN, "There are " + CUConfig.blockScans.veinScanMaxSize + "+ connected Blueprints");
            }

            else message.printMessage(TextFormatting.GREEN, "There are " + ItemHelper.countByStacks(scan.buffer.size()) + " connected Blueprints");
        }
    }

    /**
     * Replaces all scanned Locations with a given held stack.
     *
     * @param location  The Location of the clicked Blueprint
     * @param heldStack The held ItemStack (assumes its a Block).
     * @param scan      used for its list of scanned Locations.
     */
    private void replaceAllBlocks (World world, PlayerEntity player, Location location, ItemStack heldStack, VeinScan scan, UnitChatMessage message) {

        BlockState heldBlockState = Block.getBlockFromItem(heldStack.getItem()).getDefaultState();

        //Checks if the held Block State can be placed within a Blueprint.
        if (canPlaceBlockInBlueprint(heldBlockState)) {

            //Handles replacing only one Block.
            if (player.isCrouching()) {
                replaceBlock(location, player, heldBlockState);
                InventoryHelper.consumeStack(player.inventory, 1, false, heldStack);
                SoundHelper.playBlockPlaceSound(world, player, Block.getBlockFromItem(heldStack.getItem()).getDefaultState(), location);
            }

            //Handles replacing every block in list.
            else {

                int itemCount = InventoryHelper.countItems(player.inventory, false, heldStack);

                if (itemCount >= scan.buffer.size()) {

                    int amountToConsume = 0;

                    for (Location nextLocation : scan.buffer) {
                        amountToConsume++;
                        replaceBlock(nextLocation, player, heldBlockState);
                    }

                    if (amountToConsume > 0) {

                        SoundHelper.playDing(player.world, player);
                        SoundHelper.playBlockPlaceSound(world, player, Block.getBlockFromItem(heldStack.getItem()).getDefaultState(), location);

                        if (!world.isRemote) message.printMessage(TextFormatting.GREEN, "Placed " + ItemHelper.countByStacks(amountToConsume));
                        InventoryHelper.consumeStack(player.inventory, amountToConsume, false, heldStack);
                    }
                }

                else if (!world.isRemote) {

                    message.printMessage(TextFormatting.RED, "You don't have enough blocks of that type!");
                    message.printMessage(TextFormatting.RED, "You're missing: " + ItemHelper.countByStacks((scan.buffer.size() - itemCount)));
                }
            }
        }
    }

    /**
     * Checks if the given Block State can be placed without any errors.
     *
     * @param forgeState The checked Block State.
     */
    private boolean canPlaceBlockInBlueprint (IForgeBlockState forgeState) {

        BlockState state = forgeState.getBlockState();

        if (state.getBlock() instanceof ChestBlock) return false;

        return state.getMaterial() != Material.PLANTS && state.getMaterial() != Material.AIR && state.getMaterial() != Material.ANVIL && state.getMaterial() != Material.CACTUS && state.getMaterial() != Material.CAKE && state.getMaterial() != Material.CARPET && state.getMaterial() != Material.PORTAL;
    }

    /**
     * Replaces a single Blueprint with the given Block State.
     *
     * @param location The Location of the replacement.
     * @param state    The Block State of that replaces the Blueprint.
     */
    private void replaceBlock (Location location, PlayerEntity player, BlockState state) {

        if (!player.world.isRemote) {
            location.setBlock(state, player);
            ForgeEventFactory.onBlockPlace(player, BlockSnapshot.create(player.world.getDimensionKey(), player.world, location.getBlockPos()), Direction.UP);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        world.setBlockState(pos, state.with(COLOR, DyeColor.byId(stack.getDamage())));
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        ItemStack stack = new ItemStack(this);
        stack.setDamage(state.get(COLOR).getId());
        return stack;
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isSideInvisible (BlockState centerBlockState, BlockState otherStateBlock, Direction dir) {
        return (otherStateBlock.getBlock() == this && centerBlockState.get(COLOR).getId() == otherStateBlock.get(COLOR).getId()) || super.isSideInvisible(centerBlockState, otherStateBlock, dir);
    }

    @Override
    public boolean propagatesSkylightDown (BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 1.0F;
    }
}
