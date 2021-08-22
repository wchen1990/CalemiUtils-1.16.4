package com.tm.calemiutils.gui.base;

import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkBank;
import com.tm.calemiutils.tileentity.base.IProgress;
import com.tm.calemiutils.tileentity.base.TileEntityInventoryBase;
import com.tm.calemiutils.tileentity.base.TileEntityUpgradable;
import com.tm.calemiutils.util.helper.MathHelper;
import com.tm.calemiutils.util.helper.ScreenHelper;
import com.tm.calemiutils.util.helper.StringHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerScreenBase<T extends ContainerBase> extends ContainerScreen<Container> {

    protected static final int TEXT_COLOR_GRAY = 0x555555;

    protected final PlayerInventory playerInventory;
    protected final PlayerEntity player;
    private final Container container;

    public int leftTabOffset;
    private int rightTabOffset;

    private int currentProgress;

    protected ContainerScreenBase (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.guiLeft = 0;
        this.guiTop = 0;
        this.xSize = getGuiSizeX();
        this.ySize = getGuiSizeY();
        this.container = container;
        this.playerInventory = playerInventory;
        this.player = playerInventory.player;

        leftTabOffset = 4;
        rightTabOffset = 4;

        playerInventoryTitleY = 1000;
    }

    /**
     * Used to obtain the GUI's texture so it can render it.
     */
    protected abstract String getGuiTextureName ();

    /**
     * Used to render anything in the background layer.
     */
    protected abstract void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX);

    /**
     * Used to render anything in the foreground layer.
     */
    protected abstract void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY);

    /**
     * Used to determine the width of the GUI.
     */
    public int getGuiSizeX () {
        return 176;
    }

    /**
     * Used to determine the height of the GUI.
     */
    public int getGuiSizeY () {
        return 176;
    }

    /**
     * Used to determine the left of the GUI.
     */
    public int getScreenX () {
        return (this.width - getGuiSizeX()) / 2;
    }

    /**
     * Used to determine the top of the GUI.
     */
    public int getScreenY () {
        return (this.height - getGuiSizeY()) / 2;
    }

    /**
     * @return The Tile Entity connected to the GUI.
     */
    public TileEntityInventoryBase getTileEntity () {

        if (container instanceof ContainerBase) {

            ContainerBase containerBase = (ContainerBase) container;

            if (containerBase.tileEntity != null) {
                return containerBase.tileEntity;
            }
        }

        return null;
    }

    /**
     * This methods gets called every tick.
     */
    @Override
    public void tick () {
        super.tick();

        //If the Tile Entity has a progress value, sync it.
        if (getTileEntity() != null && getTileEntity() instanceof TileEntityUpgradable) {
            this.currentProgress = ((TileEntityUpgradable) getTileEntity()).currentProgress;
        }
    }

    /**
     * The base render method. Handles ALL rendering.
     */
    @Override
    public void render (MatrixStack matrixStack, int mouseX, int mouseY, float f) {

        //Renders the transparent black background behind the GUI.
        renderBackground(matrixStack);

        addGraphicsBeforeRendering(matrixStack);

        super.render(matrixStack, mouseX, mouseY, f);

        addGraphicsAfterRendering(matrixStack, mouseX, mouseY);
        drawGuiForeground(matrixStack, mouseX, mouseY);

        renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    /**
     * Handles rendering the inventory texture and its name. This method calls drawGuiBackground to add anything specific.
     */
    @Override
    protected void drawGuiContainerBackgroundLayer (MatrixStack matrixStack, float f, int mouseX, int mouseY) {

        leftTabOffset = 4;
        rightTabOffset = 4;

        ScreenHelper.bindTexture(getGuiTextureName());
        ScreenHelper.drawRect(getScreenX(), getScreenY(), 0, 0, 0, getGuiSizeX(), getGuiSizeY());

        drawGuiBackground(matrixStack, mouseY, mouseX);
    }

    /**
     * Handles rendering anything before the inventory is rendered.
     */
    private void addGraphicsBeforeRendering (MatrixStack matrixStack) {

        ScreenHelper.bindGuiTextures();

        if (getTileEntity() != null) {

            TileEntityInventoryBase tileEntity = getTileEntity();

            //If the Tile Entity is upgradeable, render its slots & progress bar.
            if (tileEntity instanceof TileEntityUpgradable) {

                TileEntityUpgradable tileEntityUpgradable = (TileEntityUpgradable) tileEntity;

                addUpgradeSlot(matrixStack, 0);
                addUpgradeSlot(matrixStack, 1);

                addProgressBar(currentProgress, tileEntityUpgradable.getMaxProgress());
            }
        }
    }

    /**
     * Handles rendering anything after the inventory is rendered.
     */
    private void addGraphicsAfterRendering (MatrixStack matrixStack, int mouseX, int mouseY) {

        GL11.glDisable(GL11.GL_LIGHTING);
        ScreenHelper.bindGuiTextures();

        if (minecraft != null && getTileEntity() != null) {

            TileEntityInventoryBase tileEntity = getTileEntity();

            //If the Tile Entity has security, render its owners name.
            if (tileEntity instanceof ISecurity) {

                ISecurity tileEntitySecurity = (ISecurity) tileEntity;

                String name = tileEntitySecurity.getSecurityProfile().getOwnerName();
                int width = minecraft.fontRenderer.getStringWidth(name) + 7;

                ScreenHelper.drawCappedRect(getScreenX() + (getGuiSizeX() / 2) - (width / 2), getScreenY() + getGuiSizeY() - 1, 0, 218, 0, width, 13, 256, 22);
                ScreenHelper.drawCenteredString(matrixStack, name, getScreenX() + (getGuiSizeX() / 2) + (width % 2 == 0 ? 0 : 1), getScreenY() + getGuiSizeY() + 2, 50, TEXT_COLOR_GRAY);
                GL11.glColor3f(1, 1, 1);
            }

            //If the Tile Entity is a Bank, render its currency.
            if (tileEntity instanceof ICurrencyNetworkBank) {

                ICurrencyNetworkBank tileEntityBank = (ICurrencyNetworkBank) tileEntity;

                GL11.glColor3f(1, 1, 1);
                addCurrencyTab(matrixStack, mouseX, mouseY, tileEntityBank.getStoredCurrency(), tileEntityBank.getMaxCurrency());
            }

            //If the Tile Entity has a progress bar, render it. (Currently Disabled)
            if (tileEntity instanceof IProgress) {

                IProgress tileEntityProgress = (IProgress) tileEntity;
                ContainerBase containerBase = (ContainerBase) container;

                GL11.glColor3f(1, 1, 1);
                //addProgressHoveringText(matrixStack, mouseX, mouseY, currentProgress, tileEntityProgress.getMaxProgress());
            }
        }
    }

    /**
     * Renders an upgrade slot based on the index.
     */
    private void addUpgradeSlot (MatrixStack matrixStack, int index) {

        ScreenHelper.bindGuiTextures();
        addRightInfoTab(matrixStack, "", 15, 22);

        ScreenHelper.bindGuiTextures();
        ScreenHelper.drawRect(getScreenX() + getGuiSizeX() + 1, getScreenY() + 6 + (index * 24), (index * 18), 19, 0, 18, 18);
    }

    /**
     * Renders a progress bar.
     */
    private void addProgressBar (int progress, int maxProgress) {

        ScreenHelper.bindGuiTextures();
        ScreenRect rect = new ScreenRect(getScreenX() - 13, getScreenY() + leftTabOffset, 13, 35);

        int scale = MathHelper.scaleInt(progress, maxProgress, 26);

        ScreenHelper.drawRect(rect.x, rect.y, 0, 37, 0, rect.width, rect.height);
        ScreenHelper.drawRect(getScreenX() - 8, getScreenY() + 30 + leftTabOffset - scale, 13, 62 - scale, 0, 5, scale);
    }

    /**
     * Renders the progress bar's hovering text.
     */
    private void addProgressHoveringText (MatrixStack matrixStack, int mouseX, int mouseY, int progress, int maxProgress) {

        ScreenRect rect = new ScreenRect(getScreenX() - 13, getScreenY() + leftTabOffset, 13, 35);
        ScreenHelper.drawHoveringTextBox(matrixStack, mouseX, mouseY, 170, rect, "Progress: " + MathHelper.scaleInt(progress, maxProgress, 100) + "%");
    }

    /**
     * Renders an info icon.
     */
    protected void addInfoIcon (int index) {

        GL11.glDisable(GL11.GL_LIGHTING);
        ScreenHelper.bindGuiTextures();
        ScreenHelper.drawRect(getScreenX() - 13, getScreenY() + leftTabOffset, (index * 13), 72, 2, 13, 15);
    }

    /**
     * Renders the info icon's hovering text.
     */
    protected void addInfoHoveringText (MatrixStack matrixStack, int mouseX, int mouseY, String... text) {
        ScreenRect rect = new ScreenRect(getScreenX() - 13, getScreenY() + leftTabOffset, 13, 15);
        ScreenHelper.drawHoveringTextBox(matrixStack, mouseX, mouseY, 170, rect, text);
    }

    /**
     * Renders the info icon's hovering text.
     */
    protected void addHoveringText (MatrixStack matrixStack, int mouseX, int mouseY, int posX, int posY, int width, int height, String... text) {
        ScreenRect rect = new ScreenRect(posX, posY, width, height);
        ScreenHelper.drawHoveringTextBox(matrixStack, mouseX, mouseY, 170, rect, text);
    }

    /**
     * Renders a currency tab.
     */
    protected void addCurrencyTab (MatrixStack matrixStack, int mouseX, int mouseY, int currency, int maxCurrency) {

        if (minecraft != null) {

            String fullName = StringHelper.printCommas(currency) + " / " + StringHelper.printCurrency(maxCurrency);

            int fullWidth = minecraft.fontRenderer.getStringWidth(fullName) + 6;

            ScreenRect rect = new ScreenRect(getScreenX() - fullWidth, getScreenY() + leftTabOffset, fullWidth, 15);
            String text = StringHelper.printCurrency(currency);

            if (rect.contains(mouseX, mouseY)) {
                text = fullName;
            }

            addLeftInfoTab(matrixStack, text, 15);
        }
    }

    /**
     * Renders a tab on the left of the inventory.
     */
    private void addLeftInfoTab (MatrixStack matrixStack, String text, int sizeY) {

        if (minecraft != null) {

            int width = minecraft.fontRenderer.getStringWidth(text) + 6;

            ScreenHelper.bindGuiTextures();
            ScreenHelper.drawCappedRect(getScreenX() - width, getScreenY() + leftTabOffset, 0, 218, 10, width, sizeY, 255, 22);

            if (!text.isEmpty()) {
                GL11.glPushMatrix();
                GL11.glColor3f(0.35F, 0.35F, 0.35F);
                GL11.glTranslatef(0, 0, 5);
                minecraft.fontRenderer.drawString(matrixStack, text, getScreenX() - width + 4, getScreenY() + (float) (sizeY / 2) - 3 + leftTabOffset, TEXT_COLOR_GRAY);
                GL11.glColor3f(1, 1, 1);
                GL11.glPopMatrix();
            }

            leftTabOffset += (sizeY + 2);
        }
    }

    /**
     * Renders a tab on the right of the inventory.
     */
    protected void addRightInfoTab (MatrixStack matrixStack, String text, int sizeAdd, int sizeY) {

        if (minecraft != null) {

            int width = minecraft.fontRenderer.getStringWidth(text) + sizeAdd + 7;

            ScreenHelper.bindGuiTextures();
            ScreenHelper.drawCappedRect(getScreenX() + getGuiSizeX() - 1, getScreenY() + rightTabOffset, 0, 218, -40, width, sizeY, 256, 22);

            if (!text.isEmpty()) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0, 0, 5);
                GL11.glColor3f(0.35F, 0.35F, 0.35F);
                minecraft.fontRenderer.drawString(matrixStack, text, getScreenX() + getGuiSizeX() + 4, getScreenY() + (float) (sizeY / 2) - 3 + rightTabOffset, TEXT_COLOR_GRAY);
                GL11.glColor3f(1, 1, 1);
                GL11.glPopMatrix();
            }

            rightTabOffset += (sizeY + 2);
        }
    }
}
