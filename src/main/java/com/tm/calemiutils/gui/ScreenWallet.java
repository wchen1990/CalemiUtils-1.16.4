package com.tm.calemiutils.gui;

import com.github.talrey.createdeco.Registration;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.gui.base.ButtonRect;
import com.tm.calemiutils.gui.base.ContainerScreenBase;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.inventory.ContainerWallet;
import com.tm.calemiutils.item.ItemCoin;
import com.tm.calemiutils.item.ItemWallet;
import com.tm.calemiutils.packet.PacketWallet;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.tm.calemiutils.util.helper.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

@OnlyIn(Dist.CLIENT)
public class ScreenWallet extends ContainerScreenBase<ContainerWallet> {

    public ScreenWallet (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, new StringTextComponent("Wallet"));
    }

    /**
     * Gets the current Wallet Stack, returns empty if missing and closes the screen.
     */
    private ItemStack getCurrentWalletStack () {

        ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);

        if (!walletStack.isEmpty()) {
            return walletStack;
        }

        else {
            player.closeScreen();
            return ItemStack.EMPTY;
        }
    }

    @Override
    protected void init () {
        super.init();

        for (int index = 0; index < 6; index++) {

            int id = index;
            int xOffset = 150 + ((index > 2) ? 38 : 0);
            int yOffset = ((index % 3) * 18);

            addButton(new ButtonRect(getScreenX() + xOffset, getScreenY() + 24 + yOffset, 16, "+", (btn) -> addMoney(id)));
        }
    }

    @Override
    public int getGuiSizeX () {
        return 210;
    }

    /**
     * Called when a "+" button is pressed.
     * Adds money to the Player from the Wallet.
     */
    private void addMoney (int id) {

        ItemStack walletStack = getCurrentWalletStack();

        //Checks if there is a current Wallet.
        if (!walletStack.isEmpty()) {

            ItemWallet walletItem = (ItemWallet) walletStack.getItem();

            int netheriteValue = CUConfig.coinValues.netherite.get();
            int goldValue = CUConfig.coinValues.gold.get();
            int brassValue = CUConfig.coinValues.brass.get();
            int ironValue = CUConfig.coinValues.iron.get();
            int copperValue = CUConfig.coinValues.copper.get();
            int zincValue = CUConfig.coinValues.zinc.get();

            int price = zincValue;
            if (id == 1) price = copperValue;
            else if (id == 2) price = ironValue;
            else if (id == 3) price = brassValue;
            else if (id == 4) price = goldValue;
            else if (id == 5) price = netheriteValue;

            int multiplier = MathHelper.getShiftCtrlInt(1, 16, 64, 9 * 64);
            price *= multiplier;

            //If the Wallet's balance can afford the requested amount, give it to the player and sync the current balance.
            if (ItemWallet.getBalance(walletStack) >= price) {

                CalemiUtils.network.sendToServer(new PacketWallet(id, multiplier));
                CompoundNBT nbt = ItemHelper.getNBT(walletStack);
                nbt.putInt("balance", nbt.getInt("balance") - price);
            }
        }
    }

    @Override
    public void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        GL11.glDisable(GL11.GL_LIGHTING);
        addInfoIcon(0);
        addInfoHoveringText(matrixStack, mouseX, mouseY, "Button Click Info", "Shift: 16, Ctrl: 64, Shift + Ctrl: 64 * 9");

        // Info boxes for coins
        int netheriteValue = CUConfig.coinValues.netherite.get();
        int goldValue = CUConfig.coinValues.gold.get();
        int brassValue = CUConfig.coinValues.brass.get();
        int ironValue = CUConfig.coinValues.iron.get();
        int copperValue = CUConfig.coinValues.copper.get();
        int zincValue = CUConfig.coinValues.zinc.get();
        String currencyName = CUConfig.economy.currencyName.get();

        addHoveringText(matrixStack, mouseX, mouseY, getScreenX() + 131, getScreenY() + 24, 16, 16, "Zinc Coin", zincValue + " " + currencyName);
        addHoveringText(matrixStack, mouseX, mouseY, getScreenX() + 131, getScreenY() + 42, 16, 16, "Copper Coin", copperValue + " " + currencyName);
        addHoveringText(matrixStack, mouseX, mouseY, getScreenX() + 131, getScreenY() + 60, 16, 16, "Iron Coin", ironValue + " " + currencyName);
        addHoveringText(matrixStack, mouseX, mouseY, getScreenX() + 169, getScreenY() + 24, 16, 16, "Brass Coin", brassValue + " " + currencyName);
        addHoveringText(matrixStack, mouseX, mouseY, getScreenX() + 169, getScreenY() + 42, 16, 16, "Gold Coin", goldValue + " " + currencyName);
        addHoveringText(matrixStack, mouseX, mouseY, getScreenX() + 169, getScreenY() + 60, 16, 16, "Netherite Coin", netheriteValue + " " + currencyName);
    }

    @Override
    public void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX) {
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(Registration.COIN_ITEM.get("Zinc").get()), getScreenX() + 131, getScreenY() + 24);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(Registration.COIN_ITEM.get("Copper").get()), getScreenX() + 131, getScreenY() + 42);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(Registration.COIN_ITEM.get("Iron").get()), getScreenX() + 131, getScreenY() + 60);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(Registration.COIN_ITEM.get("Brass").get()), getScreenX() + 169, getScreenY() + 24);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(Registration.COIN_ITEM.get("Gold").get()), getScreenX() + 169, getScreenY() + 42);
        ScreenHelper.drawItemStack(itemRenderer, new ItemStack(Registration.COIN_ITEM.get("Netherite").get()), getScreenX() + 169, getScreenY() + 60);

        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor4f(1, 1, 1, 1);

        ItemStack stack = getCurrentWalletStack();

        if (!stack.isEmpty()) {
            ScreenHelper.drawCenteredString(matrixStack, StringHelper.printCommas(ItemHelper.getNBT(stack).getInt("balance")), getScreenX() + getGuiSizeX() / 2 - 25, getScreenY() + 42, 0, TEXT_COLOR_GRAY);
            ScreenHelper.drawCenteredString(matrixStack, CUConfig.economy.currencyName.get(), getScreenX() + getGuiSizeX() / 2 - 25, getScreenY() + 51, 0, TEXT_COLOR_GRAY);
        }
    }

    @Override
    public String getGuiTextureName () {
        return "wallet";
    }
}
