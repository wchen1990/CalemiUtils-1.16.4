package com.tm.calemiutils.packet;

import com.github.talrey.createdeco.Registration;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.item.ItemCoin;
import com.tm.calemiutils.item.ItemWallet;
import com.tm.calemiutils.util.helper.CurrencyHelper;
import com.tm.calemiutils.util.helper.ItemHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketWallet {

    private int buttonId;
    private int multiplier;

    public PacketWallet () {}

    /**
     * Used to handle withdrawal from the Wallet.
     * @param buttonId The id of the button.
     * @param multiplier The multiplier; from shift-clicking & ctrl-clicking.
     */
    public PacketWallet (int buttonId, int multiplier) {
        this.buttonId = buttonId;
        this.multiplier = multiplier;
    }

    public PacketWallet (PacketBuffer buf) {
        buttonId = buf.readInt();
        multiplier = buf.readInt();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeInt(buttonId);
        buf.writeInt(multiplier);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);

                //Checks if the Wallet exists.
                if (walletStack != null) {

                    ItemWallet wallet = (ItemWallet) walletStack.getItem();

                    int netheriteValue = CUConfig.coinValues.netherite.get();
                    int goldValue = CUConfig.coinValues.gold.get();
                    int brassValue = CUConfig.coinValues.brass.get();
                    int ironValue = CUConfig.coinValues.iron.get();
                    int copperValue = CUConfig.coinValues.copper.get();
                    int zincValue = CUConfig.coinValues.zinc.get();

                    Item item = Registration.COIN_ITEM.get("Zinc").get();
                    int price = zincValue;

                    if (buttonId == 1) {
                        item = Registration.COIN_ITEM.get("Copper").get();
                        price = copperValue;
                    }

                    else if (buttonId == 2) {
                        item = Registration.COIN_ITEM.get("Iron").get();
                        price = ironValue;
                    }

                    else if (buttonId == 3) {
                        item = Registration.COIN_ITEM.get("Brass").get();
                        price = brassValue;
                    }

                    else if (buttonId == 4) {
                        item = Registration.COIN_ITEM.get("Gold").get();
                        price = goldValue;
                    }

                    else if (buttonId == 5) {
                        item = Registration.COIN_ITEM.get("Netherite").get();
                        price = netheriteValue;
                    }

                    price *= multiplier;

                    //Handles syncing the new balance to the server & spawning the coins.
                    if (!walletStack.isEmpty()) {
                        CurrencyHelper.withdrawFromWallet(walletStack, price);
                        ItemHelper.spawnStackAtEntity(player.world, player, new ItemStack(item, multiplier));
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
