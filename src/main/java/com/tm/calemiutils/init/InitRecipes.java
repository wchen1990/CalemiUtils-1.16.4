package com.tm.calemiutils.init;

import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.recipe.BlueprintRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitRecipes {

    public static final DeferredRegister<IRecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CUReference.MOD_ID);

    public static final RegistryObject<IRecipeSerializer<BlueprintRecipe>> BLUEPRINT = RECIPES.register("blueprint", () -> new SpecialRecipeSerializer<>(BlueprintRecipe::new));
}
