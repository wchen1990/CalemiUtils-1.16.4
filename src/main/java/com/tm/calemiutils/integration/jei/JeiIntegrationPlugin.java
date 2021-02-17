package com.tm.calemiutils.integration.jei;

import com.tm.calemiutils.gui.ScreenBlueprintFiller;
import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.gui.ScreenTorchPlacer;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import net.minecraft.util.ResourceLocation;

@JeiPlugin
public class JeiIntegrationPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid () {
        return new ResourceLocation(CUReference.MOD_ID, "main");
    }

    @Override
    public void registerGuiHandlers (IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(ScreenTorchPlacer.class, new UpgradeSlotsJEIHandler());
        registration.addGuiContainerHandler(ScreenBlueprintFiller.class, new UpgradeSlotsJEIHandler());
    }
}
