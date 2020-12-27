package com.tm.calemiutils.item;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.helper.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.FoodStats;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBlender extends ItemBase {

    public ItemBlender () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Blends up food into juice which you can drink!", true);
        LoreHelper.addControlsLore(tooltip, "Drink", LoreHelper.Type.USE);
        LoreHelper.addControlsLore(tooltip, "Toggle Blend Mode", LoreHelper.Type.SNEAK_USE, true);
        LoreHelper.addBlankLine(tooltip);
        tooltip.add(new StringTextComponent("Blend Food: " + TextFormatting.AQUA + (ItemHelper.getNBT(stack).getBoolean("blend") ? "ON" : "OFF")));
        tooltip.add(new StringTextComponent("Juice: " + TextFormatting.AQUA + StringHelper.printCommas((int) getJuice(stack)) + " / " + StringHelper.printCommas(CUConfig.misc.blenderMaxJuice.get())));
    }

    /**
     * Used to get the amount of juice stored in the Blender.
     */
    private float getJuice (ItemStack blenderStack) {
        CompoundNBT nbt = ItemHelper.getNBT(blenderStack);
        return nbt.getFloat("juice");
    }

    /**
     * Used to remove an amount of juice from the Blender.
     */
    private void changeJuice (ItemStack blenderStack, float amount) {
        CompoundNBT nbt = ItemHelper.getNBT(blenderStack);
        nbt.putFloat("juice", nbt.getFloat("juice") + amount);
    }

    /**
     * Handles toggling blend mode & drinking.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack stack = playerIn.getHeldItem(handIn);
        float juice = getJuice(stack);

        //If the Player is crouching, toggle the blend mode.
        if (playerIn.isCrouching()) {
            ItemHelper.getNBT(stack).putBoolean("blend", !ItemHelper.getNBT(stack).getBoolean("blend"));
            SoundHelper.playClick(worldIn, playerIn);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        //If the Player is not crouching, handle drinking.
        else {

            //Checks if the player needs food and if there is at least 1 juice in the Blender.
            if (playerIn.getFoodStats().needFood() && juice >= 1) {
                playerIn.setActiveHand(handIn);
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    /**
     * Handles adding food to Player after drinking.
     */
    @Override
    public ItemStack onItemUseFinish (ItemStack stack, World worldIn, LivingEntity entityLiving) {

        CompoundNBT nbt = ItemHelper.getNBT(stack);

        //Loop infinitely until broken.
        while (true) {

            FoodStats stats = ((PlayerEntity) entityLiving).getFoodStats();

            //If Player doesn't need food anymore, stop the loop.
            if (!stats.needFood()) break;

            float juice = getJuice(stack);

            int missingFood = 20 - stats.getFoodLevel();
            int addedFood = 0;
            int addedSat = 0;

            //If Blender has enough juice, add to data to variables.
            if (juice >= 1) {
                changeJuice(stack, -1);
                stats.addStats(1, 2);
            }

            else break;
        }

        return stack;
    }

    /**
     * Handles blending food.
     */
    @Override
    public void inventoryTick (ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {

        //Checks if entity is Player.
        if (entityIn instanceof PlayerEntity) {

            //Checks if in blend mode.
            if (ItemHelper.getNBT(stack).getBoolean("blend")) {

                PlayerEntity player = (PlayerEntity) entityIn;

                //Iterate through the Player's inventory slots.
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {

                    ItemStack currentStack = player.inventory.getStackInSlot(i);

                    //Checks if the current Item is food.
                    if (currentStack.isFood() && currentStack.getItem().getFood() != null) {

                        CompoundNBT nbt = ItemHelper.getNBT(currentStack);
                        float food = (float) (currentStack.getItem().getFood().getHealing()) / 2;

                        //Checks if the added juice can fit.
                        if (food > 0 && getJuice(stack) + food <= CUConfig.misc.blenderMaxJuice.get()) {

                            InventoryHelper.consumeStack(player.inventory, 1, false, currentStack);
                            changeJuice(stack, food);
                        }
                    }
                }
            }
        }
    }

    @Override
    public int getUseDuration (ItemStack stack) {
        return 64;
    }

    @Override
    public UseAction getUseAction (ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public boolean hasEffect (ItemStack stack) {
        return ItemHelper.getNBT(stack).getBoolean("blend");
    }
}
