package calemiutils.item;

import calemiutils.CalemiUtils;
import calemiutils.gui.ScreenLinkBook;
import calemiutils.item.base.ItemBase;
import calemiutils.tileentity.TileEntityBookStand;
import calemiutils.tileentity.base.TileEntityInventoryBase;
import calemiutils.util.Location;
import calemiutils.util.UnitChatMessage;
import calemiutils.util.helper.EntityHelper;
import calemiutils.util.helper.InventoryHelper;
import calemiutils.util.helper.ItemHelper;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemLinkBookLocation extends ItemBase {

    public ItemLinkBookLocation () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {

        CompoundNBT nbt = ItemHelper.getNBT(stack);
        Location location = getLinkedLocation(worldIn, stack);

        LoreHelper.addInformationLore(tooltip, "Creates a link to teleport to.", true);
        LoreHelper.addControlsLore(tooltip, "Open Gui", LoreHelper.Type.USE, true);
        LoreHelper.addBlankLine(tooltip);

        String locationStr = "Not set";

        if (location != null) {
            locationStr = (location.x + ", " + location.y + ", " + location.z);
        }

        tooltip.add(new StringTextComponent("[Location] " + TextFormatting.AQUA + locationStr));
        tooltip.add(new StringTextComponent("[Dimension] " + TextFormatting.AQUA + (nbt.getBoolean("linked") ? nbt.getString("DimName") : "Not set")));
    }

    private static UnitChatMessage getUnitChatMessage (PlayerEntity player) {
        return new UnitChatMessage("Location Link Book", player);
    }

    /**
     * Checks if the given Link Book ItemStack's location has been set.
     */
    public static boolean isLinked (ItemStack bookStack) {
        return ItemHelper.getNBT(bookStack).getBoolean("linked");
    }

    /**
     * @return the linked Location if set.
     */
    public static Location getLinkedLocation (World world, ItemStack bookStack) {

        CompoundNBT nbt = ItemHelper.getNBT(bookStack);

        if (isLinked(bookStack)) {
            return new Location(world, nbt.getInt("X"), nbt.getInt("Y"), nbt.getInt("Z"));
        }

        return null;
    }

    /**
     * @return the linked rotation
     */
    public static float getLinkedRotation (ItemStack bookStack) {

        CompoundNBT nbt = ItemHelper.getNBT(bookStack);

        if (isLinked(bookStack)) {
            return nbt.getFloat("Rot");
        }

        return 0;
    }

    /**
     * @return the linked Dimension if set.
     */
    public static String getLinkedDimensionName (ItemStack bookStack) {

        CompoundNBT nbt = ItemHelper.getNBT(bookStack);

        if (nbt.getBoolean("linked")) {
            return nbt.getString("DimName");
        }

        return "";
    }

    /**
     * Resets all data from a given Link Book ItemStack
     */
    public static void resetLocation (ItemStack bookStack, PlayerEntity player) {

        ItemHelper.getNBT(bookStack).putBoolean("linked", false);

        CompoundNBT nbt = ItemHelper.getNBT(bookStack);

        nbt.remove("X");
        nbt.remove("Y");
        nbt.remove("Z");
        nbt.remove("Rot");
        nbt.remove("DimName");

        if (!player.world.isRemote) {
            getUnitChatMessage(player).printMessage(TextFormatting.GREEN, "Cleared Book");
        }
    }

    /**
     * Sets the given Link Book ItemStack's linked Location to the given location.
     */
    public static void bindLocation (ItemStack bookStack, PlayerEntity player, Location location, boolean printMessage) {

        ItemHelper.getNBT(bookStack).putBoolean("linked", true);

        CompoundNBT nbt = ItemHelper.getNBT(bookStack);

        nbt.putInt("X", location.x);
        nbt.putInt("Y", location.y);
        nbt.putInt("Z", location.z);
        nbt.putFloat("Rot", player.rotationYawHead);
        nbt.putString("DimName", player.world.getDimensionKey().getLocation().toString());

        if (!player.world.isRemote && printMessage) {
            getUnitChatMessage(player).printMessage(TextFormatting.GREEN, "Linked location to " + location.toString());
        }
    }

    /**
     * Sets the given Link Book ItemStack's display name to the given string.
     */
    public static void bindName (ItemStack bookStack, String name) {

        if (!name.isEmpty()) {
            bookStack.setDisplayName(new StringTextComponent(name));
        }

        else bookStack.clearCustomName();
    }

    /**
     * Teleports the given player to the given location. Only happens if they are in the same Dimension.
     */
    public static void teleport (World world, PlayerEntity player, Location location, float yaw, String dimName) {

        //Checks if on server.
        if (!world.isRemote) {

            //Checks if the location of the Player equals the linked dimension.
            if (world.getDimensionKey().getLocation().toString().equalsIgnoreCase(dimName)) {

                //Checks if it's safe to teleport to the link Location.
                if (EntityHelper.canTeleportAt((ServerPlayerEntity) player, location)) {

                    EntityHelper.teleportPlayer((ServerPlayerEntity) player, location, yaw);
                    getUnitChatMessage(player).printMessage(TextFormatting.GREEN, "Teleported you to " + location.toString());
                }

                else getUnitChatMessage(player).printMessage(TextFormatting.RED, "The area needs to be clear!");
            }

            else getUnitChatMessage(player).printMessage(TextFormatting.RED, "You need to be in the same dimension as the linked one!");
        }
    }

    /**
     * Handles places the Link Book into a Book Stand or copying data from it.
     */
    @Override
    public ActionResultType onItemUse (ItemUseContext context) {

        World world = context.getWorld();
        Location location = new Location(world, context.getPos());
        PlayerEntity player = context.getPlayer();

        //Checks if the Player exists.
        if (player != null) {

            Hand hand = context.getHand();
            ItemStack heldItem = player.getHeldItem(hand);

            //Checks if the Tile Entity exists & if its a Book Stand.
            if (location.getTileEntity() != null && location.getTileEntity() instanceof TileEntityBookStand) {

                TileEntityBookStand inv = (TileEntityBookStand) location.getTileEntity();

                //Insert the Link Book into the Book Stand if not crouching.
                if (!player.isCrouching()) {

                    if (InventoryHelper.insertHeldStackIntoSlot(player.getHeldItem(hand), inv.getInventory(), 0, true)) {
                        inv.markForUpdate();
                        return ActionResultType.SUCCESS;
                    }
                }

                //If so, copy the data from the Book Stand's Link Book
                else {

                    ItemStack bookInventory = ((TileEntityInventoryBase)location.getTileEntity()).getInventory().getStackInSlot(0);
                    Location linkedLocation = ItemLinkBookLocation.getLinkedLocation(world, bookInventory);

                    if (!bookInventory.isEmpty() && linkedLocation != null) {

                        bindLocation(heldItem, player, linkedLocation, false);
                        if (bookInventory.hasDisplayName()) bindName(heldItem, bookInventory.getDisplayName().getString());
                        if (world.isRemote) getUnitChatMessage(player).printMessage(TextFormatting.GREEN, "Copied data from Book Stand");
                        return ActionResultType.SUCCESS;
                    }
                }
            }

            else if (world.isRemote) {
                openGui(player, hand, heldItem, true);
                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.FAIL;
    }

    /**
     * Handles opening the GUI.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, PlayerEntity player, Hand hand) {

        ItemStack heldItem = player.getHeldItem(hand);

        if (world.isRemote) {
            openGui(player, hand, heldItem, true);
            return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
        }

        return new ActionResult<>(ActionResultType.FAIL, heldItem);
    }

    @OnlyIn(Dist.CLIENT)
    public void openGui (PlayerEntity player, Hand hand, ItemStack stack, boolean isBookInHand) {
        Minecraft.getInstance().displayGuiScreen(new ScreenLinkBook(player, hand, stack, isBookInHand));
    }

    @Override
    public boolean hasEffect (ItemStack stack) {
        return isLinked(stack);
    }
}
