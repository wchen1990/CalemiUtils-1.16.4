package com.tm.calemiutils.init;

import com.tm.calemiutils.CUReference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class InitKeyBindings {

    public static final KeyBinding openWalletButton = new KeyBinding("Open Wallet", 71, CUReference.MOD_NAME);

    public static void init () {
        ClientRegistry.registerKeyBinding(openWalletButton);
    }
}
