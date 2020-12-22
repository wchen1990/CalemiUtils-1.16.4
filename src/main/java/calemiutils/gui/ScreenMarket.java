package calemiutils.gui;

import calemiutils.CUReference;
import calemiutils.CalemiUtils;
import calemiutils.config.MarketItemsFile;
import calemiutils.gui.base.ButtonRect;
import calemiutils.gui.base.GuiScreenBase;
import calemiutils.gui.base.MarketButton;
import calemiutils.gui.base.MarketTab;
import calemiutils.init.InitItems;
import calemiutils.init.InitSounds;
import calemiutils.item.ItemWallet;
import calemiutils.packet.PacketMarketOptions;
import calemiutils.packet.PacketMarketTrade;
import calemiutils.tileentity.TileEntityBank;
import calemiutils.tileentity.TileEntityMarket;
import calemiutils.util.UnitChatMessage;
import calemiutils.util.helper.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class ScreenMarket extends GuiScreenBase {

    private final TileEntityMarket market;

    private MarketTab buyTab, sellTab;
    private MarketTab activeTab;

    private ButtonRect buyBtn, automateBtn;
    private ButtonRect purchaseAmountDecBtn, purchaseAmountIncBtn;

    public ScreenMarket(PlayerEntity player, TileEntityMarket market) {
        super(player, Hand.MAIN_HAND);
        this.market = market;
    }

    private UnitChatMessage getUnitChatMessage() {
        return new UnitChatMessage("Market", player);
    }

    private ItemStack getCurrentWalletStack() {
        return CurrencyHelper.getCurrentWalletStack(player);
    }

    private PayType getPaymentTypeFromPrice(int totalValue) {

        TileEntityBank bank = market.getBank();
        ItemStack walletStack = getCurrentWalletStack();

        if (bank != null) {

            if (CurrencyHelper.canWithdrawFromBank(bank, totalValue)) {
                return PayType.BANK;
            }
        }

        if (!walletStack.isEmpty()) {

            if (CurrencyHelper.canWithdrawFromWallet(walletStack, totalValue)) {
                return PayType.WALLET;
            }
        }

        return PayType.UNDEFINED;
    }

    private PayType getPaymentTypeFromSpace(int totalValue) {

        TileEntityBank bank = market.getBank();
        ItemStack walletStack = getCurrentWalletStack();

        if (bank != null) {

            if (CurrencyHelper.canDepositToBank(bank, totalValue)) {
                return PayType.BANK;
            }
        }

        if (!walletStack.isEmpty()) {

            if (CurrencyHelper.canDepositToWallet(walletStack, totalValue)) {
                return PayType.WALLET;
            }
        }
        return PayType.UNDEFINED;
    }

    private int getCurrencyFromPayType(PayType type) {

        if (type == PayType.BANK) {
            return market.getBank().storedCurrency;
        }

        if (type == PayType.WALLET) {
            return ItemWallet.getBalance(getCurrentWalletStack());
        }

        return 0;
    }

    @Override
    protected void init() {
        super.init();

        if (minecraft != null) {

            buyTab = new MarketTab(market.marketItemsToBuy, getScreenX() - 53, getScreenY() - 52, "Buy Items", getScreenX() - 90, getScreenY() - 38, itemRenderer);
            sellTab = new MarketTab(market.marketItemsToSell, getScreenX() + 5, getScreenY() - 52, "Sell Items", getScreenX() - 90, getScreenY() - 38, itemRenderer);

            for (int index = 0; index < market.marketItemsToBuy.size(); index++) {
                int selectedIndex = index;
                MarketButton button = buyTab.addButton(index, (btn) -> setSelectedIndex(selectedIndex));
                if (!button.getRenderedStack().isEmpty()) addButton(button);
            }

            for (int index = 0; index < market.marketItemsToSell.size(); index++) {
                int selectedIndex = index;
                MarketButton button = sellTab.addButton(index, (btn) -> setSelectedIndex(selectedIndex));
                if (!button.getRenderedStack().isEmpty()) addButton(button);
            }

            setBuyMode(market.buyMode);
            setSelectedIndex(market.selectedIndex);

            buyBtn = addButton(new ButtonRect(getScreenX(), getGuiSizeY(), 56, "", (btn) -> trade()));
            automateBtn = addButton(new ButtonRect(getScreenX() - (75 / 2), getScreenY() + 60, 75, "", (btn) -> setAutomationMode(!market.automationMode)));

            purchaseAmountDecBtn = addButton(new ButtonRect(getScreenX(), getScreenY(), 16, "-", (btn) -> changePurchaseAmount(true)));
            purchaseAmountIncBtn = addButton(new ButtonRect(getScreenX(), getScreenY(), 16, "+", (btn) -> changePurchaseAmount(false)));
        }
    }

    private void setBuyMode(boolean value) {

        sellTab.enableButtons(false);
        buyTab.enableButtons(false);

        market.buyMode = value;
        CalemiUtils.network.sendToServer(new PacketMarketOptions("syncBuyMode", market.getPos(), value, false, 0, 0));

        if (market.buyMode) activeTab = buyTab;
        else activeTab = sellTab;

        activeTab.enableButtons(true);
    }

    private void setAutomationMode(boolean value) {
        market.automationMode = value;
        CalemiUtils.network.sendToServer(new PacketMarketOptions("syncAutomationMode", market.getPos(), false, value, 0, 0));
    }

    private void setSelectedIndex(int index) {
        market.selectedIndex = index;
        CalemiUtils.network.sendToServer(new PacketMarketOptions("syncSelectedIndex", market.getPos(), false, false, index, 0));
    }

    private void changePurchaseAmount(boolean decrease) {

        int multiplier = (decrease ? -1 : 1) * calemiutils.util.helper.MathHelper.getShiftCtrlInt(1, 16, 32, 64);
        int amount = MathHelper.clamp(market.purchaseAmount + multiplier, 1, 64);

        market.purchaseAmount = amount;
        CalemiUtils.network.sendToServer(new PacketMarketOptions("syncPurchaseAmount", market.getPos(), false, false, 0, amount));
    }

    private void trade() {

        if (minecraft == null) {
            return;
        }

        if (!market.automationMode) {

            if (market.getSelectedMarketItem() != null && !market.getSelectedItemStack().isEmpty()) {

                MarketItemsFile.MarketItem marketItem = market.getSelectedMarketItem();
                ItemStack selectedStack = market.getSelectedItemStack();

                ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);

                if (!walletStack.isEmpty() || market.getBank() != null) {

                    int totalValue = marketItem.value * market.purchaseAmount;
                    PayType payTypeFromPrice = getPaymentTypeFromPrice(totalValue);
                    PayType payTypeFromSpace = getPaymentTypeFromSpace(totalValue);
                    int currency = getCurrencyFromPayType(payTypeFromPrice);

                    if (market.buyMode) {

                        if (payTypeFromPrice == PayType.WALLET) {
                            CurrencyHelper.withdrawFromWallet(walletStack, totalValue);
                        }

                        else if (payTypeFromPrice == PayType.BANK) {
                            CurrencyHelper.withdrawFromBank(market.getBank(), totalValue);
                        }

                        if (payTypeFromPrice != PayType.UNDEFINED) {
                            minecraft.getSoundHandler().play(SimpleSound.master(InitSounds.COIN.get(), 1, 0.1F));
                            CalemiUtils.network.sendToServer(new PacketMarketTrade(market.getPos(), payTypeFromPrice == PayType.BANK));
                        }

                        if (payTypeFromPrice == PayType.UNDEFINED) getUnitChatMessage().printMessage(TextFormatting.RED, "You don't have enough money!");
                    }

                    else {

                        if (InventoryHelper.countItems(player.inventory, true, selectedStack) >= marketItem.amount * market.purchaseAmount) {

                            if (payTypeFromSpace == PayType.WALLET) {
                                CurrencyHelper.depositToWallet(walletStack, totalValue);
                            }

                            else if (payTypeFromSpace == PayType.BANK) {
                                CurrencyHelper.depositToBank(market.getBank(), totalValue);
                            }

                            if (payTypeFromSpace != PayType.UNDEFINED) {
                                minecraft.getSoundHandler().play(SimpleSound.master(InitSounds.COIN.get(), 1, 0.1F));
                                CalemiUtils.network.sendToServer(new PacketMarketTrade(market.getPos(), payTypeFromSpace == PayType.BANK));
                            }
                        }

                        else getUnitChatMessage().printMessage(TextFormatting.RED, "You don't have the required Items!");
                    }
                }

                else getUnitChatMessage().printMessage(TextFormatting.RED, "You need to have a Wallet or a connected Bank!");
            }
        }
    }

    @Override
    public void drawGuiBackground(MatrixStack matrixStack, int mouseX, int mouseY) {

        if (minecraft == null) {
            return;
        }

        GL11.glPushMatrix();

        GL11.glEnable(GL11.GL_BLEND);

        //Renders title banner.
        Minecraft.getInstance().getTextureManager().bindTexture(CUReference.GUI_TEXTURES);
        ScreenHelper.drawRect(0, getScreenY() - 76, 0, 1, 50, width, 18);

        //Renders title.
        ScreenHelper.drawCenteredString(matrixStack, "Market", getScreenX(), getScreenY() - 71, 100, 0xFFFFFF);

        GL11.glPopMatrix();

        int xOffset = 40;
        int yOffset = getScreenY() - 36;

        MarketItemsFile.MarketItem marketItem = market.getSelectedMarketItem();
        ItemStack selectedStack = market.getSelectedItemStack();

        //Checks if the selected offer exists.
        if (marketItem != null && !selectedStack.isEmpty()) {

            int x = (int) ((width / (market.automationMode ? 2.3F : 4)) - xOffset);

            String name = TextFormatting.UNDERLINE + (StringHelper.printCommas(marketItem.amount * market.purchaseAmount)) + "x " + TextFormatting.UNDERLINE + TextFormatting.getTextWithoutFormattingCodes(selectedStack.getDisplayName().getString());
            int nameWidth = minecraft.fontRenderer.getStringWidth(name) - 1;

            activeTab.getMarketButtons().get(market.selectedIndex).renderSelectionBox();

            //Renders selected stack.
            ScreenHelper.drawItemStack(itemRenderer, selectedStack, x - 8, yOffset - 2);
            //Renders selected stack's name.
            ScreenHelper.drawCenteredString(matrixStack, name, x, yOffset + 18, 0, 0xFFFFFF);

            ScreenHelper.drawCenteredString(matrixStack, (market.buyMode ? "Cost " : "Sell ") + TextFormatting.GOLD + StringHelper.printCurrency(marketItem.value * market.purchaseAmount), x, yOffset + 32, 0, 0xFFFFFF);

            ScreenHelper.drawCenteredString(matrixStack, market.purchaseAmount + "x", x, yOffset + 46, 0, 0xFFFFFF);

            purchaseAmountDecBtn.visible = true;
            purchaseAmountDecBtn.setPosition(x - (purchaseAmountDecBtn.rect.width / 2) - 20, yOffset + 42);

            purchaseAmountIncBtn.visible = true;
            purchaseAmountIncBtn.setPosition(x - (purchaseAmountIncBtn.rect.width / 2) + 20, yOffset + 42);

            buyBtn.visible = true;
            buyBtn.setPosition(x - (buyBtn.rect.width / 2), yOffset + 60);
            buyBtn.setMessage(new StringTextComponent(market.buyMode ? "Purchase" : "Sell"));

            buyBtn.visible = !market.automationMode;
        }

        else {
            purchaseAmountDecBtn.visible = false;
            purchaseAmountIncBtn.visible = false;
            buyBtn.visible = false;
        }

        //Checks if there is a Bank connected.
        if (market.getBank() != null) {
            int x = (int) (((width / 4) * (market.automationMode ? 2.3F : 3)) + xOffset);
            ScreenHelper.drawItemStack(itemRenderer, new ItemStack(InitItems.BANK.get()), x - 8, yOffset);
            ScreenHelper.drawCenteredString(matrixStack, "Balance: " + TextFormatting.GOLD + StringHelper.printCurrency(market.getBank().storedCurrency), x, yOffset + 18, 0, 0xFFFFFF);
            yOffset += 35;
        }

        //Checks if the Player has a Wallet.
        if (!getCurrentWalletStack().isEmpty()) {
            int x = (int) (((width / 4) * (market.automationMode ? 2.3F : 3)) + xOffset);
            ScreenHelper.drawItemStack(itemRenderer, getCurrentWalletStack(), x - 8, yOffset);
            ScreenHelper.drawCenteredString(matrixStack, "Balance: " + TextFormatting.GOLD + StringHelper.printCurrency(ItemWallet.getBalance(getCurrentWalletStack())), x, yOffset + 18, 0, 0xFFFFFF);
        }

        automateBtn.setMessage(new StringTextComponent("Automate: " + (market.automationMode ? "ON" : "OFF")));

        if (!market.automationMode) {

            buyTab.renderTab(matrixStack);
            sellTab.renderTab(matrixStack);

            if (activeTab != null) {
                activeTab.renderSelectedTab();
                activeTab.updateButtons();

                for (MarketButton button : activeTab.getMarketButtons()) {
                    button.active = true;
                }
            }
        }

        else {

            for (MarketButton button : activeTab.getMarketButtons()) {
                button.active = false;
            }

            purchaseAmountDecBtn.visible = false;
            purchaseAmountIncBtn.visible = false;
        }
    }

    @Override
    public void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        if (market.getSelectedMarketItem() != null && !market.getSelectedItemStack().isEmpty()) {
            ScreenHelper.drawHoveringTextBox(matrixStack, mouseX, mouseY, 100, purchaseAmountDecBtn.rect, "Shift: 16, Ctrl: 32, Shift + Ctrl: 64");
            ScreenHelper.drawHoveringTextBox(matrixStack, mouseX, mouseY, 100, purchaseAmountIncBtn.rect, "Shift: 16, Ctrl: 32, Shift + Ctrl: 64");
        }
    }

    @Override
    public boolean mouseClicked(double x, double y, int i) {
        super.mouseClicked(x, y, i);

        if (minecraft != null) {

            if (!market.automationMode) {

                if (buyTab.getRect().contains((int) x, (int) y)) {
                    minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1));
                    setBuyMode(true);
                    if (buyTab.getMarketButtons().size() > 0) setSelectedIndex(buyTab.getMarketButtons().get(0).getMarketListIndex());
                }

                if (sellTab.getRect().contains((int) x, (int) y)) {
                    minecraft.getSoundHandler().play(SimpleSound.master(SoundEvents.UI_BUTTON_CLICK, 1));
                    setBuyMode(false);
                    if (sellTab.getMarketButtons().size() > 0) setSelectedIndex(sellTab.getMarketButtons().get(0).getMarketListIndex());
                }
            }
        }

        return false;
    }

    @Override
    public int getGuiSizeX() {
        return 0;
    }

    @Override
    public int getGuiSizeY() {
        return 0;
    }

    @Override
    public String getGuiTextureName() {
        return null;
    }

    @Override
    public boolean canCloseWithInvKey() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public enum PayType {

        UNDEFINED(0),
        BANK(1),
        WALLET(2);

        final int index;

        PayType(int index) {
            this.index = index;
        }

    }
}
