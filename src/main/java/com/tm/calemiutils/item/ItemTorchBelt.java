package com.tm.calemiutils.item;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.integration.curios.CuriosIntegration;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.UnitChatMessage;
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

    public ItemTorchBelt() {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    public static boolean isActive(ItemStack stack) {
        return ItemHelper.getNBT(stack).getBoolean("on");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        LoreHelper.addInformationLore(tooltip, "Place this anywhere in your inventory. Automatically uses and places torches in dark areas.", true);
        LoreHelper.addControlsLore(tooltip, "Toggle ON/OFF", LoreHelper.Type.USE, true);
        LoreHelper.addBlankLine(tooltip);
        tooltip.add(new StringTextComponent("Status: " + TextFormatting.AQUA + (isActive(stack) ? "ON" : "OFF")));
    }

    public static UnitChatMessage getMessage (PlayerEntity player) {
        return new UnitChatMessage("Torch Belt", player);
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
    public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);

        setActive(stack, world, player, !isActive(stack));

        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    public static void setActive(ItemStack stack, World world, PlayerEntity player, boolean state) {

        ItemHelper.getNBT(stack).putBoolean("on", state);
        SoundHelper.playClick(world, player);
    }

    /**
     * Handles calling tick when in inventory.
     */
    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        tick(stack, world, entity);
    }

    /**
     * Handles placing Torches.
     */
    public static void tick(ItemStack stack, World world, Entity entity) {

        //Checks if the entity is a Player
        if (entity instanceof PlayerEntity) {

            //Checks if the state is on.
            if (isActive(stack)) {

                PlayerEntity player = (PlayerEntity) entity;
                Location location = new Location(world, (int) Math.floor(player.getPosition().getX()), (int) Math.floor(player.getPosition().getY()), (int) Math.floor(player.getPosition().getZ()));

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
    public boolean hasEffect(ItemStack stack) {
        return isActive(stack);
    }
}
