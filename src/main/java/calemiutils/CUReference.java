package calemiutils;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;

public class CUReference {

    public static final String MOD_ID = "calemiutils";
    public static final String MOD_NAME = "Calemi's Utils";
    public static final String CONFIG_DIR = FMLPaths.CONFIGDIR.get().toString() + "/CalemiUtils";

    public static final ResourceLocation GUI_TEXTURES = new ResourceLocation(MOD_ID + ":textures/gui/gui_textures.png");
    public static final ResourceLocation TOOLTIP_TEXTURE = new ResourceLocation(MOD_ID + ":textures/gui/tooltip.png");
}

