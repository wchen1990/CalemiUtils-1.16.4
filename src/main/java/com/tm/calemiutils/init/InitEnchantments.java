package com.tm.calemiutils.init;

import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.enchantment.EnchantmentCrushing;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, CUReference.MOD_ID);

    //public static final RegistryObject<Enchantment> CRUSHING = ENCHANTMENTS.register("crushing", EnchantmentCrushing::new);
}