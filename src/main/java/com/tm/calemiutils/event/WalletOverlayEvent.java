package com.tm.calemiutils.event;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.item.ItemWallet;
import com.tm.calemiutils.util.helper.CurrencyHelper;
import com.tm.calemiutils.util.helper.ScreenHelper;
import com.tm.calemiutils.util.helper.StringHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class WalletOverlayEvent {

    /**
     * Handles the Wallet overlay that displays how much currency it has.
     */
    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void render (RenderGameOverlayEvent.Post event) {

        boolean isTextLayer = event.getType() == RenderGameOverlayEvent.ElementType.TEXT;
        boolean isHotbarLayer = event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR;

        //Checks if the current render is on the "TEXT" or "HOTBAR" layer.
        if (isTextLayer || isHotbarLayer) {

            Minecraft mc = Minecraft.getInstance();
            World world = mc.world;
            ClientPlayerEntity player = mc.player;

            int scaledWidth = mc.getMainWindow().getScaledWidth();
            int scaledHeight = mc.getMainWindow().getScaledHeight();
            int midX = scaledWidth / 2;
            int midY = scaledHeight / 2;

            //Checks if the Player exists and is not in Spectator mode.
            if (player != null && !player.isSpectator()) {

                ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);

                //Checks if the config option is true and the Player is not looking at a screen.
                if (CUConfig.wallet.walletOverlay.get() && !walletStack.isEmpty() && mc.currentScreen == null && !mc.gameSettings.showDebugInfo) {

                    CUConfig.WalletOverlayPosition walletPosition = CUConfig.WalletOverlayPosition.byName(CUConfig.wallet.walletOverlayPosition.get());

                    int currency = ItemWallet.getBalance(walletStack);
                    String currencyStr = StringHelper.printCurrency(currency);

                    int xOffsetStr = 0;
                    int xOffsetItem = 0;
                    int yOffset = 0;

                    //If the wallet position is "BOTTOM_LEFT", adds the appropriate offsets.
                    if (walletPosition == CUConfig.WalletOverlayPosition.BOTTOM_LEFT) {
                        yOffset = scaledHeight - 16;
                    }

                    //If the wallet position is "TOP_RIGHT", adds the appropriate offsets.
                    else if (walletPosition == CUConfig.WalletOverlayPosition.TOP_RIGHT) {
                        xOffsetStr = scaledWidth - Minecraft.getInstance().fontRenderer.getStringWidth(currencyStr) - 41;
                        xOffsetItem = scaledWidth - 20;
                    }

                    //If the wallet position is "BOTTOM_RIGHT", adds the appropriate offsets.
                    else if (walletPosition == CUConfig.WalletOverlayPosition.BOTTOM_RIGHT) {
                        yOffset = scaledHeight - 16;
                        xOffsetStr = scaledWidth - Minecraft.getInstance().fontRenderer.getStringWidth(currencyStr) - 41;
                        xOffsetItem = scaledWidth - 20;
                    }

                    //Draw string on the TEXT layer.
                    if (isTextLayer) Minecraft.getInstance().fontRenderer.drawString(event.getMatrixStack(), currencyStr, 21 + xOffsetStr, 4 + yOffset, 0xFFFFFFFF);
                    //Draw Item on the HOTBAR layer.
                    else ScreenHelper.drawItemStack(Minecraft.getInstance().getItemRenderer(), walletStack, 2 + xOffsetItem, yOffset);
                }
            }
        }
    }
}
