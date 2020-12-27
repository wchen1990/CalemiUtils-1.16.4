package com.tm.calemiutils.gui;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.gui.base.ButtonRect;
import com.tm.calemiutils.gui.base.ContainerScreenBase;
import com.tm.calemiutils.gui.base.FakeSlot;
import com.tm.calemiutils.inventory.ContainerTradingPost;
import com.tm.calemiutils.packet.PacketTradingPost;
import com.tm.calemiutils.tileentity.TileEntityTradingPost;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkBank;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.ScreenHelper;
import com.tm.calemiutils.util.helper.StringHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class ScreenTradingPost extends ContainerScreenBase<ContainerTradingPost> {

    private final TileEntityTradingPost tePost;
    private final int upY = 40;
    private final int downY = 59;
    private ButtonRect sellModeBtn;
    private FakeSlot fakeSlot;

    public ScreenTradingPost (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, new StringTextComponent("Wallet"));
        tePost = (TileEntityTradingPost) getTileEntity();
    }

    @Override
    protected void init () {
        super.init();

        //Subtract Amount
        addButton(new ButtonRect(getScreenX() + 50, getScreenY() + upY, 16, "-", (btn) -> {
            int i = com.tm.calemiutils.util.helper.MathHelper.getShiftCtrlInt(1, 10, 100, 1000);
            changeAmount(-i);
        }));

        //Add Amount
        addButton(new ButtonRect(getScreenX() + 110, getScreenY() + upY, 16, "+", (btn) -> {
            int i = com.tm.calemiutils.util.helper.MathHelper.getShiftCtrlInt(1, 10, 100, 1000);
            changeAmount(i);
        }));

        //Subtract Price
        addButton(new ButtonRect(getScreenX() + 50, getScreenY() + downY, 16, "-", (btn) -> {
            int i = com.tm.calemiutils.util.helper.MathHelper.getShiftCtrlInt(1, 10, 100, 1000);
            changePrice(-i);
        }));

        //Add Price
        addButton(new ButtonRect(getScreenX() + 110, getScreenY() + downY, 16, "+", (btn) -> {
            int i = com.tm.calemiutils.util.helper.MathHelper.getShiftCtrlInt(1, 10, 100, 1000);
            changePrice(i);
        }));

        //Reset Amount
        addButton(new ButtonRect(getScreenX() + 128, getScreenY() + upY, 16, "R", (btn) -> resetAmount()));

        //Reset Price
        addButton(new ButtonRect(getScreenX() + 128, getScreenY() + downY, 16, "R", (btn) -> resetPrice()));

        sellModeBtn = addButton(new ButtonRect(getScreenX() + 21, getScreenY() + 19, 38, tePost.buyMode ? "Buying" : "Selling", (btn) -> toggleMode()));
        if (CUConfig.misc.tradingPostBroadcasts.get()) addButton(new ButtonRect(getScreenX() + 105, getScreenY() + 19, 60, "Broadcast", (btn) -> broadcast()));

        fakeSlot = addButton(new FakeSlot(getScreenX() + 80, getScreenY() + 19, itemRenderer, (btn) -> setFakeSlot()));
        fakeSlot.setItemStack(tePost.getStackForSale());
    }

    /**
     * Called when a "-" or "+" amount button is pressed.
     * Adds to or subtracts from the amount value and syncs it.
     */
    private void changeAmount (int change) {
        int value = MathHelper.clamp(tePost.amountForSale + change, 1, 64);
        tePost.amountForSale = value;
        CalemiUtils.network.sendToServer(new PacketTradingPost("syncoptions", tePost.getPos(), value, tePost.salePrice));
    }

    /**
     * Called when a "-" or "+" price button is pressed.
     * Adds to or subtracts from the price value and syncs it.
     */
    private void changePrice (int change) {
        int value = MathHelper.clamp(tePost.salePrice + change, 0, 9999);
        tePost.salePrice = value;
        CalemiUtils.network.sendToServer(new PacketTradingPost("syncoptions", tePost.getPos(), tePost.amountForSale, value));
    }

    /**
     * Called when a "R" amount button is pressed.
     * Resets the amount value and syncs it.
     */
    private void resetAmount () {
        tePost.amountForSale = 0;
        CalemiUtils.network.sendToServer(new PacketTradingPost("syncoptions", tePost.getPos(), 0, tePost.salePrice));
    }

    /**
     * Called when a "R" price button is pressed.
     * Resets the price value and syncs it.
     */
    private void resetPrice () {
        tePost.salePrice = 0;
        CalemiUtils.network.sendToServer(new PacketTradingPost("syncoptions", tePost.getPos(), tePost.amountForSale, 0));
    }

    /**
     * Called when a sellModeBtn is pressed.
     * Toggles the current mode and syncs it.
     */
    private void toggleMode () {
        boolean mode = !tePost.buyMode;
        CalemiUtils.network.sendToServer(new PacketTradingPost("syncmode", tePost.getPos(), mode));
        tePost.buyMode = mode;
    }

    /**
     * Called when a broadcastBtn is pressed.
     * Sends a message to everyone containing information about the Trading Post.
     */
    private void broadcast () {
        CalemiUtils.network.sendToServer(new PacketTradingPost("broadcast", tePost.getPos()));
    }

    /**
     * Called when a fakeSlot button is pressed.
     * Sets fakeSlot's icon to the hovered Stack and syncs it.
     */
    private void setFakeSlot () {

        ItemStack stack = new ItemStack(playerInventory.getItemStack().getItem(), 1);
        if (playerInventory.getItemStack().hasTag()) stack.setTag(playerInventory.getItemStack().getTag());

        CalemiUtils.network.sendToServer(new PacketTradingPost("syncstack", tePost.getPos(), ItemHelper.getStringFromStack(stack), stack.hasTag() ? stack.getTag().toString() : ""));
        tePost.setStackForSale(stack);
        fakeSlot.setItemStack(stack);
    }

    @Override
    protected void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX) {

        if (minecraft != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 50);

            // Titles
            minecraft.fontRenderer.drawString(matrixStack, "Amount", getScreenX() + 10, getScreenY() + upY + 4, TEXT_COLOR_GRAY);
            minecraft.fontRenderer.drawString(matrixStack, "Price", getScreenX() + 10, getScreenY() + downY + 4, TEXT_COLOR_GRAY);

            ScreenHelper.drawCenteredString(matrixStack, StringHelper.printCommas(tePost.amountForSale), getScreenX() + getGuiSizeX() / 2, getScreenY() + upY + 4, 0, TEXT_COLOR_GRAY);
            ScreenHelper.drawCenteredString(matrixStack, StringHelper.printCommas(tePost.salePrice), getScreenX() + getGuiSizeX() / 2, getScreenY() + downY + 4, 0, TEXT_COLOR_GRAY);

            GL11.glPopMatrix();

            sellModeBtn.setMessage(new StringTextComponent(tePost.buyMode ? "Buying" : "Selling"));
        }
    }

    @Override
    protected void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        if (tePost.getBank() != null) {
            GL11.glColor3f(1, 1, 1);
            addCurrencyTab(matrixStack, mouseX, mouseY, ((ICurrencyNetworkBank) tePost.getBank()).getStoredCurrency(), ((ICurrencyNetworkBank) tePost.getBank()).getMaxCurrency());
        }

        GL11.glDisable(GL11.GL_LIGHTING);
        fakeSlot.renderButton(matrixStack, mouseX, mouseY, 150);

        addInfoIcon(0);
        addInfoHoveringText(matrixStack, mouseX, mouseY, "Button Click Info", "Shift: 10, Ctrl: 100, Shift + Ctrl: 1,000");
    }

    @Override
    public int getGuiSizeY () {
        return 232;
    }

    @Override
    public String getGuiTextureName () {
        return "trading_post";
    }
}

