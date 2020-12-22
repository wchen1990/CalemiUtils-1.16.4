package calemiutils.block;

import calemiutils.block.base.BlockInventoryContainerBase;
import calemiutils.init.InitItems;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.item.ItemWallet;
import calemiutils.tileentity.TileEntityTradingPost;
import calemiutils.util.Location;
import calemiutils.util.UnitChatMessage;
import calemiutils.util.helper.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class BlockTradingPost extends BlockInventoryContainerBase {

    private static final VoxelShape AABB = Block.makeCuboidShape(0, 0, 0, 16, 5, 16);

    public BlockTradingPost () {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(-1.0F, 3600000.0F).notSolid().variableOpacity());
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Used to buy and sell Items.", true);
        LoreHelper.addControlsLore(tooltip, "Show Trade Info", LoreHelper.Type.SNEAK_USE, true);
        LoreHelper.addControlsLore(tooltip, "Open Inventory", LoreHelper.Type.USE_WRENCH);
        LoreHelper.addControlsLore(tooltip, "Buy Item", LoreHelper.Type.USE_WALLET);
    }

    /**
     * This will handle purchasing, selling, and opening the gui.
     */
    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        Location location = new Location(world, pos);

        ItemStack heldStack = player.getHeldItem(hand);
        ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);

        TileEntity te = location.getTileEntity();

        //Makes sure the Block is a Trading Post.
        if (te instanceof TileEntityTradingPost) {

            TileEntityTradingPost tePost = (TileEntityTradingPost) te;
            UnitChatMessage message = tePost.getUnitName(player);

            //If the Player is crouching and holding a Security Wrench, open the GUI.
            if (!player.isCrouching() && heldStack.getItem() == InitItems.SECURITY_WRENCH.get()) {

                if (tePost.adminMode) {
                    if (player.isCreative()) return super.onBlockActivated(state, world, pos, player, hand, result);
                    else if (!world.isRemote) message.printMessage(TextFormatting.RED, "Admin Posts can only be opened in creative mode.");
                }

                else return super.onBlockActivated(state, world, pos, player, hand, result);
            }

            //Else, if the Player is not crouching and has a Wallet, handle a possible trade.
            else if (!player.isCrouching() && !walletStack.isEmpty()) {
                handleTrade(message, world, player, tePost);
            }

            //Otherwise, handle printing what the owner is trading.
            else if (!world.isRemote) {

                if (tePost.hasValidTradeOffer) {

                    if (tePost.adminMode) message.printMessage(TextFormatting.GREEN, (tePost.buyMode ? "Buying " : "Selling ") + StringHelper.printCommas(tePost.amountForSale) + "x " + tePost.getStackForSale().getDisplayName().getString() + " for " + (tePost.salePrice > 0 ? (StringHelper.printCurrency(tePost.salePrice)) : "free"));
                    else message.printMessage(TextFormatting.GREEN, tePost.getSecurityProfile().getOwnerName() + " is " + (tePost.buyMode ? "buying " : "selling ") + StringHelper.printCommas(tePost.amountForSale) + "x " + tePost.getStackForSale().getDisplayName().getString() + " for " + (tePost.salePrice > 0 ? (StringHelper.printCurrency(tePost.salePrice)) : "free"));

                    message.printMessage(TextFormatting.GREEN, "Hold a wallet in your inventory to make a purchase.");
                }

                else {
                    message.printMessage(TextFormatting.RED, "There is nothing to trade!");
                }
            }
        }

        return ActionResultType.SUCCESS;
    }

    /**
     * Handles the trading system. Decides if the trade is valid and if it is a sell or a purchase.
     */
    private void handleTrade (UnitChatMessage message, World world, PlayerEntity player, TileEntityTradingPost tePost) {

        ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);
        ItemWallet wallet = (ItemWallet) walletStack.getItem();

        //Checks if there is a connected bank OR if the price is free OR if the Trading Post is in admin mode.
        if (tePost.getBank() != null || tePost.salePrice <= 0 || tePost.adminMode) {

            //Checks if the trade is set up properly
            if (tePost.hasValidTradeOffer) {

                //If the Trading Post is in buy mode, handle a sell.
                if (tePost.buyMode) {
                    handleSell(message, walletStack, world, player, tePost);
                }

                //If not, handle a purchase.
                else handlePurchase(message, walletStack, world, player, tePost);
            }

            else if (!world.isRemote) message.printMessage(TextFormatting.RED, "The trade is not set up properly!");
        }

        else if (!world.isRemote) message.printMessage(TextFormatting.RED, "There is no active connected Bank!");
    }

    /**
     * Handles the selling system.
     */
    private void handleSell (UnitChatMessage message, ItemStack walletStack, World world, PlayerEntity player, TileEntityTradingPost tePost) {

        //Checks if the player has the required amount of items.
        if (InventoryHelper.countItems(player.inventory, true, tePost.getStackForSale()) >= tePost.amountForSale) {

            //Generates the base Item Stack to purchase.
            ItemStack stackForSale = new ItemStack(tePost.getStackForSale().getItem(), tePost.amountForSale);

            //Sets any NBT to the purchased item.
            if (tePost.getStackForSale().hasTag()) {
                stackForSale.setTag(tePost.getStackForSale().getTag());
            }

            //Checks if the Trading Post can fit the amount of items being bought.
            if (InventoryHelper.canInsertStack(stackForSale, tePost.getInventory()) || tePost.adminMode) {

                //Checks if the player's current Wallet can fit added funds.
                if (CurrencyHelper.canDepositToWallet(walletStack, tePost.salePrice)) {

                    //Checks if the connected Bank has enough funds to spend. Bypasses this check if in admin mode
                    if (CurrencyHelper.canWithdrawFromBank(tePost.getBank(), tePost.salePrice) || tePost.salePrice <= 0 || tePost.adminMode) {

                        //Removes the Items from the player.
                        InventoryHelper.consumeStack(player.inventory, tePost.amountForSale, true, tePost.getStackForSale());

                        //Checks if not in admin mode.
                        if (!tePost.adminMode) {

                            //Adds Items to the Trading Post
                            InventoryHelper.insertOverflowingStack(tePost.getInventory(), stackForSale);

                            //Subtracts funds from the connected Bank
                            CurrencyHelper.withdrawFromBank(tePost.getBank(), tePost.salePrice);
                        }

                        //Adds funds to the Player's current wallet.
                        CurrencyHelper.depositToWallet(walletStack, tePost.salePrice);

                        SoundHelper.playCoin(world, player);
                    }

                    else if (!world.isRemote) message.printMessage(TextFormatting.RED, "The connected Bank is out of money!");
                }

                else if (!world.isRemote) message.printMessage(TextFormatting.RED, "Your Wallet is full of money!");
            }

            else if (!world.isRemote) message.printMessage(TextFormatting.RED, "The Trading Post's stock is full!");
        }

        else if (!world.isRemote) message.printMessage(TextFormatting.RED, "You do not have the required item(s) the Trading Post is looking for!");
    }

    /**
     * Handles the purchasing system.
     */
    private void handlePurchase (UnitChatMessage message, ItemStack walletStack, World world, PlayerEntity player, TileEntityTradingPost tePost) {

        //Checks if the Trading Post has enough stock. Bypasses this check if in admin mode.
        if (tePost.getStock() >= tePost.amountForSale || tePost.adminMode) {

            //Checks if the Player has enough funds in his current Wallet.
            if (CurrencyHelper.canWithdrawFromWallet(walletStack, tePost.salePrice)) {

                //Checks if the connected Bank can store the possible funds.
                if (CurrencyHelper.canDepositToBank(tePost.getBank(), tePost.salePrice) || tePost.salePrice <= 0 || tePost.adminMode) {

                    //Generates the base Item Stack to purchase.
                    ItemStack stackForSale = new ItemStack(tePost.getStackForSale().getItem(), tePost.amountForSale);

                    //Sets any NBT to the purchased item.
                    if (tePost.getStackForSale().hasTag()) {
                        stackForSale.setTag(tePost.getStackForSale().getTag());
                    }

                    if (!world.isRemote) {

                        //Generates and spawns the purchased Items.
                        ItemHelper.spawnOverflowingStackAtEntity(player.world, player, stackForSale);

                        //Adds funds to the connected Bank.
                        if (!tePost.adminMode) CurrencyHelper.depositToBank(tePost.getBank(), tePost.salePrice);

                        tePost.markForUpdate();
                        tePost.write(tePost.getTileData());
                    }

                    //Subtracts funds from the Player's current Wallet.
                    CurrencyHelper.withdrawFromWallet(walletStack, tePost.salePrice);

                    //Removes the amount of Items for sale.
                    if (!tePost.adminMode) InventoryHelper.consumeStack(tePost.getInventory(), tePost.amountForSale, true, tePost.getStackForSale());

                    SoundHelper.playCoin(world, player);
                }

                else if (!world.isRemote) message.printMessage(TextFormatting.RED, "Full of money!");
            }

            else if (!world.isRemote) message.printMessage(TextFormatting.RED, "You don't have enough money!");
        }

        else if (!world.isRemote) message.printMessage(TextFormatting.RED, "There is not enough items in stock!");
    }

    /**
     * Handles enabling admin mode for the Trading Post.
     */
    @Override
    public void onBlockPlacedBy (World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

        if (placer instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) placer;

            if (player.isCreative() && !player.isCrouching()) {

                Location location = new Location(worldIn, pos);
                TileEntity te = location.getTileEntity();

                if (te instanceof TileEntityTradingPost) {

                    TileEntityTradingPost tePost = (TileEntityTradingPost) te;
                    tePost.adminMode = true;
                    if (!worldIn.isRemote) tePost.getUnitName(player).printMessage(TextFormatting.GREEN, "Admin Mode is enabled for this Block. Sneak place this Block to disable it.");
                }
            }
        }
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity (IBlockReader worldIn) {
        return InitTileEntityTypes.TRADING_POST.get().create();
    }

    /*
        Methods for Blocks that are not full and solid cubes.
     */

    @Override
    public VoxelShape getShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    @Override
    public VoxelShape getCollisionShape (BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return AABB;
    }

    @Override
    public boolean propagatesSkylightDown (BlockState state, IBlockReader world, BlockPos pos) {
        return true;
    }
}
