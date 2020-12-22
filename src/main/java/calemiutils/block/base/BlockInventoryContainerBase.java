package calemiutils.block.base;

import calemiutils.tileentity.base.TileEntityInventoryBase;
import calemiutils.tileentity.base.TileEntityUpgradable;
import calemiutils.util.Location;
import calemiutils.util.helper.InventoryHelper;
import calemiutils.util.helper.SecurityHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * The base class for Blocks that have Inventories.
 */
public abstract class BlockInventoryContainerBase extends BlockContainerBase {

    /**
     * @param properties The specific properties for the Block. (Creative Tab, hardness, material, etc.)
     */
    public BlockInventoryContainerBase (Properties properties) {
        super(properties);
    }

    /**
     * Drops all contents when the Block is broken or replaced.
     */
    @Override
    public void onReplaced (BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {

        if (state.getBlock() != newState.getBlock()) {

            Location location = new Location(world, pos);
            TileEntity tileEntity = location.getTileEntity();

            if (tileEntity instanceof TileEntityInventoryBase) {

                TileEntityInventoryBase inv = (TileEntityInventoryBase) tileEntity;
                InventoryHelper.breakInventory(world, inv.getInventory(), location);

                if (tileEntity instanceof TileEntityUpgradable) {

                    TileEntityUpgradable upgradeInv = (TileEntityUpgradable) tileEntity;
                    InventoryHelper.breakInventory(world, upgradeInv.getUpgradeInventory(), location);
                }
            }

            super.onReplaced(state, world, pos, newState, isMoving);
        }
    }

    /**
     * Opens the gui of the Block.
     */
    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        //Prevents client side.
        if (world.isRemote) {
            return ActionResultType.SUCCESS;
        }

        Location location = new Location(world, pos);
        TileEntity tileEntity = location.getTileEntity();

        if (player instanceof ServerPlayerEntity && tileEntity instanceof INamedContainerProvider) {

            if (SecurityHelper.openSecuredBlock(location, player, true)) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, pos);
            }
        }

        return ActionResultType.SUCCESS;
    }

    /**
     * Sets the Tile Entity's custom name if the held Item Stack has a custom name.
     */
    @Override
    public void onBlockPlacedBy (World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        Location location = new Location(world, pos);
        TileEntity tileEntity = location.getTileEntity();

        if (tileEntity instanceof TileEntityInventoryBase) {

            TileEntityInventoryBase inv = (TileEntityInventoryBase) tileEntity;

            if (stack.hasDisplayName()) {

                inv.setCustomName(stack.getDisplayName());
            }
        }
    }
}
