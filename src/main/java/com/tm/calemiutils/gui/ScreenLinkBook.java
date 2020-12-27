package com.tm.calemiutils.gui;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.gui.base.ButtonRect;
import com.tm.calemiutils.gui.base.GuiScreenBase;
import com.tm.calemiutils.gui.base.TextFieldRect;
import com.tm.calemiutils.item.ItemLinkBookLocation;
import com.tm.calemiutils.packet.PacketLinkBook;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.ScreenHelper;
import com.tm.calemiutils.util.helper.SoundHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenLinkBook extends GuiScreenBase {

    private final ItemStack bookStack;
    private final boolean isBookInHand;
    private TextFieldRect nameField;
    private ButtonRect resetBookBtn;
    private ButtonRect teleportBtn;

    public ScreenLinkBook (PlayerEntity player, Hand hand, ItemStack bookStack, boolean isBookInHand) {
        super(player, hand);
        this.bookStack = bookStack;
        this.isBookInHand = isBookInHand;
    }

    /**
     * Gets the ItemLinkBookLocation, returns null if missing.
     */
    private ItemLinkBookLocation getBook () {

        if (bookStack.getItem() instanceof ItemLinkBookLocation) {
            return (ItemLinkBookLocation) bookStack.getItem();
        }

        return null;
    }

    @Override
    protected void init () {
        super.init();

        if (minecraft != null) {

            minecraft.keyboardListener.enableRepeatEvents(true);

            if (isBookInHand) {

                nameField = new TextFieldRect(minecraft.fontRenderer, getScreenX() - 80 - 8, getScreenY() - 50 - 8, 160, 32, "");
                children.add(nameField);

                if (bookStack != null) {

                    if (bookStack.hasDisplayName()) {
                        nameField.setText(bookStack.getDisplayName().getString());
                    }
                }

                //Set Name
                addButton(new ButtonRect(getScreenX() + 80 - 4, getScreenY() - 50 - 8, 16, "+", (btn) -> setName(nameField.getText())));

                //Bind Location
                addButton(new ButtonRect(getScreenX() - 50, getScreenY() + 35 - 8, 100, "Bind Location", (btn) -> bindLocation()));

                //Reset Book
                resetBookBtn = addButton(new ButtonRect(getScreenX() - 50, getScreenY() + 70 - 8, 100, "Reset Book", (btn) -> resetBook()));
            }
        }

        //Teleport
        teleportBtn = addButton(new ButtonRect(getScreenX() - 50, getScreenY() - 8, 100, "Teleport", (btn) -> teleport()));
    }

    @Override
    public void tick () {
        super.tick();

        Location location = ItemLinkBookLocation.getLinkedLocation(player.world, bookStack);

        teleportBtn.active = location != null;
        if (isBookInHand) resetBookBtn.active = location != null;
    }

    /**
     * Sets the name of the Link Book to the given text and syncs it.
     */
    private void setName (String name) {

        if (isBookInHand && getBook() != null) {
            CalemiUtils.network.sendToServer(new PacketLinkBook("name", hand, name));
            ItemLinkBookLocation.bindName(bookStack, name);
        }
    }

    /**
     * Binds the current Location to the Link Book and syncs it.
     */
    private void bindLocation () {
        setName(nameField.getText());

        BlockPos pos = new BlockPos((int) Math.floor(player.getPosition().getX()), (int) Math.floor(player.getPosition().getY()), (int) Math.floor(player.getPosition().getZ()));
        Location location = new Location(player.world, pos);

        CalemiUtils.network.sendToServer(new PacketLinkBook("bind", hand, pos));
        ItemLinkBookLocation.bindLocation(bookStack, player, location, true);
    }

    /**
     * Reset all data of the Link Book and syncs it.
     */
    private void resetBook () {
        setName(nameField.getText());

        setName("");
        CalemiUtils.network.sendToServer(new PacketLinkBook("reset", hand));
        ItemLinkBookLocation.resetLocation(bookStack, player);
        nameField.setText("");
    }

    /**
     * Teleports the Player to the Link Book's linked Location.
     */
    private void teleport () {

        //Checks if the Link Book exists and is linked.
        if (getBook() != null && ItemLinkBookLocation.isLinked(bookStack)) {

            Location location = ItemLinkBookLocation.getLinkedLocation(player.world, bookStack);

            //Checks if the Location exists.
            if (location != null) {

                BlockPos pos = location.getBlockPos();
                float yaw = ItemLinkBookLocation.getLinkedRotation(bookStack);
                String dimName = ItemLinkBookLocation.getLinkedDimensionName(bookStack);

                SoundHelper.playWarp(player.world, player, location);
                SoundHelper.playWarp(player.world, player);

                CalemiUtils.network.sendToServer(new PacketLinkBook("teleport", hand, pos, yaw, dimName));

                player.closeScreen();
            }
        }
    }

    @Override
    public void drawGuiBackground (MatrixStack matrixStack, int mouseX, int mouseY) {

        //If the book is in hand, render the name field.
        if (isBookInHand) {
            nameField.render(matrixStack, mouseX, mouseY, 0);
            ScreenHelper.drawCenteredString(matrixStack, "Name Book", getScreenX(), getScreenY() - 67, 0, 0xFFFFFF);
        }

        //Checks the Link Book exists
        if (getBook() != null) {

            CompoundNBT nbt = ItemHelper.getNBT(bookStack);

            Location location = ItemLinkBookLocation.getLinkedLocation(player.world, bookStack);
            String string = "Not set";

            //If the Link Book is linked and has a existing Location, set the string to the Location's details.
            if (ItemLinkBookLocation.isLinked(bookStack) && location != null) {
                ScreenHelper.drawCenteredString(matrixStack, bookStack.getDisplayName().getString(), getScreenX(), getScreenY() - 28, 0, 0xFFFFFF);
                string = nbt.getString("DimName") + " " + location.toString();
            }

            //Render the Link Book's linked Location's details. Shows "Not set" if the Link Book is not linked.
            ScreenHelper.drawCenteredString(matrixStack, string, getScreenX(), getScreenY() - 18, 0, 0xFFFFFF);
        }
    }

    @Override
    public void drawGuiForeground (MatrixStack matrixStack, int mouseX, int mouseY) {}

    @Override
    public int getGuiSizeX () {
        return 0;
    }

    @Override
    public int getGuiSizeY () {
        return 0;
    }

    @Override
    public String getGuiTextureName () {
        return null;
    }

    @Override
    public boolean canCloseWithInvKey () {
        return !isBookInHand || !nameField.isFocused();
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }
}
