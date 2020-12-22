package com.tm.calemiutils.gui.base;

import com.tm.calemiutils.config.MarketItemsFile;
import com.tm.calemiutils.util.helper.ScreenHelper;
import com.tm.calemiutils.util.helper.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MarketButton extends ItemStackButton {

    private final List<MarketItemsFile.MarketItem> marketList;
    private final int marketListIndex;

    /**
     * A Market button. Used to select a Market offer.
     * @param marketList The type of market list.
     * @param marketListIndex The index of a market list.
     * @param pressable Called when the button is pressed.
     */
    public MarketButton(List<MarketItemsFile.MarketItem> marketList, int marketListIndex, int x, int y, ItemRenderer itemRender, IPressable pressable) {
        super(x, y, itemRender, pressable);
        this.marketList = marketList;
        this.marketListIndex = marketListIndex;
    }

    public int getMarketListIndex() {
        return marketListIndex;
    }

    @Override
    public ItemStack getRenderedStack() {
        return MarketItemsFile.getStackFromList(marketList, marketListIndex);
    }

    public void renderSelectionBox () {

        if (this.visible && this.active) {
            ScreenHelper.bindGuiTextures();
            ScreenHelper.drawRect(rect.x - 1, rect.y - 1, 0, 91, 0, 19, 19);
        }
    }

    @Override
    public String[] getTooltip() {

        MarketItemsFile.MarketItem marketItem = marketList.get(marketListIndex);

        List<String> list = new ArrayList<>();
        List<ITextComponent> lore = getRenderedStack().getTooltip(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL);

        list.add(marketItem.amount + "x " + getRenderedStack().getDisplayName().getString());
        list.add("Value " + TextFormatting.GOLD + StringHelper.printCurrency(marketItem.value));

        if (lore.size() > 1) {

            if (Screen.hasShiftDown()) {

                for (ITextComponent component : lore) {
                    list.add(component.getString());
                }

                list.remove(2);

                StringHelper.removeNullsFromList(list);
                StringHelper.removeCharFromList(list, "Shift", "Ctrl");
            }

            else {
                list.add(TextFormatting.GRAY + "[" + TextFormatting.AQUA + "Shift" + TextFormatting.GRAY + "]" + TextFormatting.GRAY + " Info");
            }
        }

        return StringHelper.getArrayFromList(list);
    }
}
