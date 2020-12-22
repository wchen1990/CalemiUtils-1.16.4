package calemiutils.world;

import calemiutils.CUReference;
import calemiutils.config.CUConfig;
import calemiutils.init.InitItems;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;

@Mod.EventBusSubscriber()
public class WorldGen {

    private static final ArrayList<ConfiguredFeature<?, ?>> overworldOres = new ArrayList<>();

    public static void initOres() {

        if (CUConfig.worldGen.raritaniumOreGen.get()) {
            overworldOres.add(register("raritanium_ore", Feature.ORE.withConfiguration(new OreFeatureConfig(
                    OreFeatureConfig.FillerBlockType.BASE_STONE_OVERWORLD,
                    InitItems.RARITANIUM_ORE.get().getDefaultState(), CUConfig.worldGen.raritaniumVeinSize.get()))
                    .withPlacement(Placement.RANGE.configure(new TopSolidRangeConfig(CUConfig.worldGen.raritaniumOreGenMinY.get(), 0, CUConfig.worldGen.raritaniumOreGenMaxY.get()))).square()
                    .func_242731_b(CUConfig.worldGen.raritaniumVeinsPerChunk.get())));
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void generateOres(BiomeLoadingEvent event) {

        BiomeGenerationSettingsBuilder generation = event.getGeneration();

        if (!event.getCategory().equals(Biome.Category.NETHER) && !event.getCategory().equals(Biome.Category.THEEND)) {

            for (ConfiguredFeature<?, ?> ore : overworldOres) {
                if (ore != null) generation.withFeature(GenerationStage.Decoration.UNDERGROUND_ORES, ore);
            }
        }
    }

    private static <FC extends IFeatureConfig> ConfiguredFeature<FC, ?> register(String name, ConfiguredFeature<FC, ?> configuredFeature) {
        return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, CUReference.MOD_ID + ":" + name, configuredFeature);
    }
}
