package calemiutils.init;

import calemiutils.CUReference;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class InitKeyBindings {

    public static final KeyBinding openWalletButton = new KeyBinding("Open Wallet", 71, CUReference.MOD_NAME);

    public static void init () {
        ClientRegistry.registerKeyBinding(openWalletButton);
    }
}
