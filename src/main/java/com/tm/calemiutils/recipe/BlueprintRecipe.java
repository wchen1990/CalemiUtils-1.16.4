package com.tm.calemiutils.recipe;

import com.google.common.collect.Lists;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.init.InitRecipes;
import com.tm.calemiutils.item.ItemPencil;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

public class BlueprintRecipe extends SpecialRecipe {

    public BlueprintRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {

        int emptyCount = 0;
        int pencilCount = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {

            ItemStack stackInSlot = inv.getStackInSlot(i);

            if (stackInSlot.getItem() instanceof ItemPencil) {
                pencilCount++;
            } else if (stackInSlot.isEmpty()) {
                emptyCount++;
            }
        }

        return pencilCount == 1 && (emptyCount == 3 || emptyCount == 8);
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {

        List<DyeItem> list = Lists.newArrayList();
        int colorID = 0;

        for (int i = 0; i < inv.getSizeInventory(); i++) {

            ItemStack stackInSlot = inv.getStackInSlot(i);

            if (stackInSlot.getItem() instanceof ItemPencil) {
                colorID = ItemPencil.getColorId(stackInSlot);
            }
        }

        ItemStack result = new ItemStack(InitItems.BLUEPRINT_ITEM.get(), 64);
        result.setDamage(colorID);

        return result;
    }

    @Override
    public boolean canFit(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return InitRecipes.BLUEPRINT.get();
    }
}