package com.tm.calemiutils.item;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.LoreHelper;
import com.tm.calemiutils.util.helper.SoundHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class ItemMoneyBag extends ItemBase {

    Random random = new Random();

    private final boolean isRich;

    public ItemMoneyBag(boolean isRich) {
        super(new Item.Properties().group(CalemiUtils.TAB));
        this.isRich = isRich;
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Contains an unknown assortment of Coins.", true);
        LoreHelper.addControlsLore(tooltipList, "Open Bag", LoreHelper.Type.USE, true);
    }

    /**
     * Handles opening the Money Bag.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);

        stack.shrink(1);

        if (isRich) {
            SoundHelper.playMoneyBagRichOpen(world, player);
            giveCoins(world, player, CUConfig.economy.richMoneyBagMin.get(), CUConfig.economy.richMoneyBagMax.get());
        }

        else {
            SoundHelper.playMoneyBagCheapOpen(world, player);
            giveCoins(world, player, CUConfig.economy.cheapMoneyBagMin.get(), CUConfig.economy.cheapMoneyBagMax.get());
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    private void giveCoins(World world, PlayerEntity player, int minAmount, int maxAmount) {

        if (!world.isRemote) {

            int amount = minAmount + random.nextInt(maxAmount - minAmount);

            int netheriteValue = CUConfig.coinValues.netherite.get();
            int goldValue = CUConfig.coinValues.gold.get();
            int brassValue = CUConfig.coinValues.brass.get();
            int ironValue = CUConfig.coinValues.iron.get();
            int copperValue = CUConfig.coinValues.copper.get();
            int zincValue = CUConfig.coinValues.zinc.get();

            int netherite = (int)Math.floor((float)amount / netheriteValue);
            amount -= (netherite * netheriteValue);
            int gold = (int)Math.floor((float)amount / goldValue);
            amount -= (gold * goldValue);
            int brass = (int)Math.floor((float)amount / brassValue);
            amount -= (brass * brassValue);
            int iron = (int)Math.floor((float)amount / ironValue);
            amount -= (iron * ironValue);
            int copper = (int)Math.floor((float)amount / copperValue);
            amount -= (copper * copperValue);
            int zinc = (int)Math.floor((float)amount / zincValue);

            String formatString = "createdeco:%s&%d";
            ItemHelper.spawnStackAtEntity(world, player, ItemHelper.getStackFromString(String.format(formatString, "netherite_coin", netherite)));
            ItemHelper.spawnStackAtEntity(world, player, ItemHelper.getStackFromString(String.format(formatString, "gold_coin", gold)));
            ItemHelper.spawnStackAtEntity(world, player, ItemHelper.getStackFromString(String.format(formatString, "brass_coin", brass)));
            ItemHelper.spawnStackAtEntity(world, player, ItemHelper.getStackFromString(String.format(formatString, "iron_coin", iron)));
            ItemHelper.spawnStackAtEntity(world, player, ItemHelper.getStackFromString(String.format(formatString, "copper_coin", copper)));
            ItemHelper.spawnStackAtEntity(world, player, ItemHelper.getStackFromString(String.format(formatString, "zinc_coin", zinc)));
        }
    }
}
