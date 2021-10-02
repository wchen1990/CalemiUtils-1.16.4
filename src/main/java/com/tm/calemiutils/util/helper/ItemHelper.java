package com.tm.calemiutils.util.helper;

import com.tm.calemiutils.util.Location;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

public class ItemHelper {

    private static final Random rand = new Random();

    public static CompoundNBT getNBT (ItemStack is) {

        if (is.getTag() == null) {
            is.setTag(new CompoundNBT());
        }

        return is.getTag();
    }

    public static String getStringFromStack (ItemStack stack) {

        ResourceLocation resourcelocation = Registry.ITEM.getKey(stack.getItem());

        if (stack.isEmpty()) {
            return "null";
        }

        return resourcelocation.toString() + "&" + stack.getCount();
    }

    public static String getNBTFromStack (ItemStack stack) {

        if (stack.getTag() != null && stack.hasTag()) {
            return stack.getTag().toString();
        }

        return "";
    }

    public static ItemStack getStackFromString (String string) {

        if (!string.equalsIgnoreCase("null")) {

            String[] data = string.split("&");

            if (data.length == 2) {

                String registryName = data[0];
                int stackSize = Integer.parseInt(data[1]);

                return new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(registryName)), stackSize);
            }
        }

        return ItemStack.EMPTY;
    }

    public static Item getItemFromString (String string) {

        if (!string.equalsIgnoreCase("null")) {
            return ForgeRegistries.ITEMS.getValue(new ResourceLocation(string));
        }

        return Items.AIR;
    }

    public static void attachNBTFromString (ItemStack stack, String nbtString) {

        try {
            stack.setTag(JsonToNBT.getTagFromJson(nbtString));
        }

        catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String countByStacks (int count) {
        int remainder = (count % 64);
        return StringHelper.printCommas(count) + " blocks" + ((count > 64) ? " (" + ((int) Math.floor((float) count / 64)) + " stack(s)" + ((remainder > 0) ? (" + " + remainder + " blocks)") : ")") : "");
    }

    public static void spawnStacksAtLocation(World world, Location location, List<ItemStack> stack) {
        spawnStacks(world, location.x + 0.5F, location.y + 0.5F, location.z + 0.5F, stack);
    }

    private static void spawnStacks(World world, float x, float y, float z, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            spawnStack(world, x, y, z, stack);
        }
    }

    public static void spawnOverflowingStackAtEntity(World world, Entity entity, ItemStack stack) {
        spawnOverflowingStack(world, (float) entity.getPosition().getX() + 0.5F, (float) entity.getPosition().getY() + 0.5F, (float) entity.getPosition().getZ() + 0.5F, stack);
    }

    private static void spawnOverflowingStack(World world, float x, float y, float z, ItemStack stack) {

        if (stack.getCount() > stack.getMaxStackSize()) {

            int amountLeft = stack.getCount();

            while (amountLeft > 0) {
                ItemStack spawnStack = stack.copy();
                spawnStack.setCount(Math.min(amountLeft, stack.getMaxStackSize()));
                ItemHelper.spawnStack(world, x, y, z, spawnStack);
                amountLeft -= spawnStack.getCount();
            }
        }

        else ItemHelper.spawnStack(world, x, y, z, stack);
    }

    public static ItemEntity spawnStackAtLocation(World world, Location location, ItemStack stack) {
        return spawnStack(world, location.x + 0.5F, location.y + 0.5F, location.z + 0.5F, stack);
    }

    public static ItemEntity spawnStackAtEntity(World world, Entity entity, ItemStack stack) {
        return spawnStack(world, (float) entity.getPosition().getX() + 0.5F, (float) entity.getPosition().getY() + 0.5F, (float) entity.getPosition().getZ() + 0.5F, stack);
    }

    private static ItemEntity spawnStack(World world, float x, float y, float z, ItemStack stack) {
        ItemEntity item = new ItemEntity(world, x, y, z, stack);
        item.setNoPickupDelay();
        item.setMotion(-0.05F + rand.nextFloat() * 0.1F, -0.05F + rand.nextFloat() * 0.1F, -0.05F + rand.nextFloat() * 0.1F);
        world.addEntity(item);
        return item;
    }
}
