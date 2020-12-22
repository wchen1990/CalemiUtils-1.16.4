package com.tm.calemiutils.packet;

import com.tm.calemiutils.config.MarketItemsFile;
import com.tm.calemiutils.tileentity.TileEntityBank;
import com.tm.calemiutils.tileentity.TileEntityMarket;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.CurrencyHelper;
import com.tm.calemiutils.util.helper.InventoryHelper;
import com.tm.calemiutils.util.helper.ItemHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketMarketTrade {

    private BlockPos pos;
    private boolean fromBank;

    public PacketMarketTrade () {}

    public PacketMarketTrade (BlockPos pos, boolean fromBank) {
        this.pos = pos;
        this.fromBank = fromBank;
    }

    public PacketMarketTrade (PacketBuffer buf) {
        pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        fromBank = buf.readBoolean();
    }

    public void toBytes (PacketBuffer buf) {
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
        buf.writeBoolean(fromBank);
    }

    public void handle (Supplier<NetworkEvent.Context> ctx) {

        ctx.get().enqueueWork(() -> {

            ServerPlayerEntity player = ctx.get().getSender();

            if (player != null) {

                Location location = new Location(player.world, pos);

                if (location.getTileEntity() instanceof TileEntityMarket) {

                    TileEntityMarket market = (TileEntityMarket) location.getTileEntity();

                    if (market.getSelectedMarketItem() != null && !market.getSelectedItemStack().isEmpty()) {

                        MarketItemsFile.MarketItem marketItem = market.getSelectedMarketItem();

                        ItemStack walletStack = CurrencyHelper.getCurrentWalletStack(player);
                        TileEntityBank bank = market.getBank();

                        int totalAmount = marketItem.amount * market.purchaseAmount;
                        int totalValue = marketItem.value * market.purchaseAmount;

                        ItemStack selectedStack = market.getSelectedItemStack();
                        selectedStack.setCount(totalAmount);

                        if (market.buyMode) {

                            if (fromBank) CurrencyHelper.withdrawFromBank(bank, totalValue);
                            else CurrencyHelper.withdrawFromWallet(walletStack, totalValue);

                            ItemHelper.spawnOverflowingStackAtEntity(player.world, player, selectedStack);
                        }

                        else {

                            if (fromBank) CurrencyHelper.depositToBank(bank, totalValue);
                            else CurrencyHelper.depositToWallet(walletStack, totalValue);

                            InventoryHelper.consumeStack(player.inventory, totalAmount, true, selectedStack);
                        }
                    }
                }
            }
        });

        ctx.get().setPacketHandled(true);
    }
}
