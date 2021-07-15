package com.tm.calemiutils.init;

import com.tm.calemiutils.main.CUReference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class InitKeyBindings {

    public static final KeyBinding openWalletButton = new KeyBinding("Open Wallet", 71, CUReference.MOD_NAME);
    public static final KeyBinding toggleTorchBeltButton = new KeyBinding("Toggle Torch Belt", 72, CUReference.MOD_NAME);

    public static void init () {
        ClientRegistry.registerKeyBinding(openWalletButton);
        ClientRegistry.registerKeyBinding(toggleTorchBeltButton);
    }
}
