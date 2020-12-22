package com.tm.calemiutils.event;

import com.tm.calemiutils.init.InitItems;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AddTradesEvent {

    /**
     * Handles adding new villager trades.
     */
    @SubscribeEvent
    public void onVillagerTrade (VillagerTradesEvent event) {

        if (event.getType() == VillagerProfession.LIBRARIAN) {
            event.getTrades().get(1).add((entity, random) -> new MerchantOffer(new ItemStack(InitItems.COIN_QUARTER.get(), 1), new ItemStack(Items.EMERALD, 1), 128, 0, 0.05F));
        }
    }
}
