package com.tm.calemiutils.item.base;

import com.tm.calemiutils.main.CalemiUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;

/**
 * The base class for Items.
 */
public class ItemBase extends Item {

    private Rarity rarity = Rarity.COMMON;
    private boolean hasEffect = false;

    public ItemBase () {
        this(new Item.Properties().group(CalemiUtils.TAB));
    }

    public ItemBase (Item.Properties properties) {
        super(properties);
    }

    public ItemBase setEffect () {
        hasEffect = true;
        return this;
    }

    public ItemBase setRarity (Rarity rarity) {
        this.rarity = rarity;
        return this;
    }

    @Override
    public Rarity getRarity(ItemStack stack) {
        return rarity;
    }

    @Override
    public boolean hasEffect (ItemStack stack) {
        return hasEffect;
    }
}
