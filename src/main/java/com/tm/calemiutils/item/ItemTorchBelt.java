package com.tm.calemiutils.item;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.integration.curios.CuriosIntegration;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.*;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTorchBelt extends ItemBase {

    public ItemTorchBelt () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Place this anywhere in your inventory. Automatically uses and places torches in dark areas.", true);
        LoreHelper.addControlsLore(tooltip, "Toggle ON/OFF", LoreHelper.Type.USE, true);
        LoreHelper.addBlankLine(tooltip);
        tooltip.add(new StringTextComponent("Status: " + TextFormatting.AQUA + (ItemHelper.getNBT(stack).getBoolean("on") ? "ON" : "OFF")));
    }

    /**
     * Adds behaviours to the Torch Belt as a curios Item.
     */
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundNBT unused) {

        if (CalemiUtils.curiosLoaded) {
            return CuriosIntegration.torchBeltCapability();
        }

        return super.initCapabilities(stack, unused);
    }

    /**
     * Handles toggle the state.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World worldIn, PlayerEntity playerIn, Hand handIn) {

        ItemStack stack = playerIn.getHeldItem(handIn);

        ItemHelper.getNBT(stack).putBoolean("on", !ItemHelper.getNBT(stack).getBoolean("on"));
        SoundHelper.playClick(worldIn, playerIn);

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    /**
     * Handles calling tick when in inventory.
     */
    @Override
    public void inventoryTick (ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        tick(stack, worldIn, entityIn);
    }

    /**
     * Handles placing Torches.
     */
    public static void tick (ItemStack stack, World worldIn, Entity entityIn) {

        //Checks if the entity is a Player
        if (entityIn instanceof PlayerEntity) {

            //Checks if the state is on.
            if (ItemHelper.getNBT(stack).getBoolean("on")) {

                PlayerEntity player = (PlayerEntity) entityIn;
                Location location = new Location(worldIn, (int) Math.floor(player.getPosition().getX()), (int) Math.floor(player.getPosition().getY()), (int) Math.floor(player.getPosition().getZ()));

                //Checks if the Player has a Torch. Bypassed by creative mode.
                if (player.inventory.hasItemStack(new ItemStack(Blocks.TORCH)) || player.abilities.isCreativeMode) {

                    //Checks if a torch can be placed on the Player's Location.
                    if (BlockHelper.canPlaceTorchAt(location)) {

                        location.setBlock(Blocks.TORCH);
                        if (!player.abilities.isCreativeMode) InventoryHelper.consumeStack(player.inventory, 1, false, new ItemStack(Blocks.TORCH));
                    }
                }
            }
        }
    }

    @Override
    public boolean hasEffect (ItemStack stack) {
        return ItemHelper.getNBT(stack).getBoolean("on");
    }
}
