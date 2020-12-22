package calemiutils.init;

import calemiutils.CUReference;
import net.minecraft.block.SoundType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.ref.Reference;

public class InitSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CUReference.MOD_ID);

    public static final RegistryObject<SoundEvent> COIN = SOUNDS.register("item.coin_sound", () -> new SoundEvent(new ResourceLocation(CUReference.MOD_ID, "item.coin_sound")));
    public static final RegistryObject<SoundEvent> MONEY_BAG_CHEAP = SOUNDS.register("item.money_bag_cheap_sound", () -> new SoundEvent(new ResourceLocation(CUReference.MOD_ID, "item.money_bag_cheap_sound")));
    public static final RegistryObject<SoundEvent> MONEY_BAG_RICH = SOUNDS.register("item.money_bag_rich_sound", () -> new SoundEvent(new ResourceLocation(CUReference.MOD_ID, "item.money_bag_rich_sound")));
}
