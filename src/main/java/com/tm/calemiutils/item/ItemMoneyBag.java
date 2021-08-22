package com.tm.calemiutils.item;

import com.github.talrey.createdeco.Registration;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.init.InitItems;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

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
            giveCoins(world, player, 125, 350);
        }

        else {
            SoundHelper.playMoneyBagCheapOpen(world, player);
            giveCoins(world, player, 25, 150);
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    private void giveCoins(World world, PlayerEntity player, int minAmount, int maxAmount) {

        if (!world.isRemote) {

            int amount = minAmount + random.nextInt(maxAmount - minAmount);

            int hundreds = (int)Math.floor((float)amount / 100);
            amount -= (hundreds * 100);
            int fifties = (int)Math.floor((float)amount / 50);
            amount -= (fifties * 50);
            int twenties = (int)Math.floor((float)amount / 20);
            amount -= (twenties * 20);
            int tens = (int)Math.floor((float)amount / 10);
            amount -= (tens * 10);
            int fives = (int)Math.floor((float)amount / 5);
            amount -= (fives * 5);
            int ones = amount;

            ItemHelper.spawnStackAtEntity(world, player, new ItemStack(Registration.COIN_ITEM.get("Netherite").get(), hundreds));
            ItemHelper.spawnStackAtEntity(world, player, new ItemStack(Registration.COIN_ITEM.get("Gold").get(), fifties));
            ItemHelper.spawnStackAtEntity(world, player, new ItemStack(Registration.COIN_ITEM.get("Brass").get(), twenties));
            ItemHelper.spawnStackAtEntity(world, player, new ItemStack(Registration.COIN_ITEM.get("Iron").get(), tens));
            ItemHelper.spawnStackAtEntity(world, player, new ItemStack(Registration.COIN_ITEM.get("Copper").get(), fives));
            ItemHelper.spawnStackAtEntity(world, player, new ItemStack(Registration.COIN_ITEM.get("Zinc").get(), ones));
        }
    }
}
