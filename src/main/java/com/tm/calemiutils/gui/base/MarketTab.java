package com.tm.calemiutils.gui.base;

import com.tm.calemiutils.CUReference;
import com.tm.calemiutils.config.MarketItemsFile;
import com.tm.calemiutils.util.helper.ScreenHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;

import java.util.ArrayList;
import java.util.List;

public class MarketTab {

    private final List<MarketButton> marketButtons;
    private final List<MarketItemsFile.MarketItem> marketList;

    private final ScreenRect rect;
    private final String name;
    private final int marketButtonsX;
    private final int marketButtonsY;
    private final ItemRenderer itemRender;

    public MarketTab(List<MarketItemsFile.MarketItem> marketList, int tabX, int tabY, String name, int marketButtonsX, int marketButtonsY, ItemRenderer itemRender) {
        this.marketList = marketList;
        marketButtons = new ArrayList<>();
        this.rect = new ScreenRect(tabX, tabY, 48, 12);
        this.name = name;
        this.marketButtonsX = marketButtonsX;
        this.marketButtonsY = marketButtonsY;
        this.itemRender = itemRender;
    }

    public ScreenRect getRect() {
        return rect;
    }

    public List<MarketButton> getMarketButtons() {
        return marketButtons;
    }

    public MarketButton addButton(int index, Button.IPressable pressable) {

        if (index <= 50) {
            MarketButton button = new MarketButton(marketList, index, 0, 0, itemRender, pressable);
            button.active = false;
            marketButtons.add(button);
            return button;
        }

        return null;
    }

    public void updateButtons() {

        int count = 0;

        for (MarketButton button : marketButtons) {

            if (button.active) {

                int rowSize = 10;

                int xPos = (marketButtonsX) + ((count % rowSize) * 18);
                int yPos = (marketButtonsY) + ((count / rowSize) * 18);
                int size = 16;

                button.setRect(new ScreenRect(xPos, yPos, size, size));
                count++;
            }
        }
    }

    public void enableButtons(boolean value) {

        for (MarketButton button : marketButtons) {
            button.active = value;
        }
    }

    public void renderTab(MatrixStack matrixStack) {
        ScreenHelper.drawCenteredString(matrixStack, name, rect.x + rect.width / 2, rect.y, 0, 0xFFFFFF);
    }

    public void renderSelectedTab() {
        Minecraft.getInstance().getTextureManager().bindTexture(CUReference.GUI_TEXTURES);
        ScreenHelper.drawRect(rect.x, rect.y + 9, 0, 0, 100, rect.width - 1, 1);
    }
}
