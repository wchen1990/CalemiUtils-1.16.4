package com.tm.calemiutils.util.helper;

import net.minecraft.world.World;

public class LogHelper {

    public static void logCommon (World world, Object message) {
        System.out.println((world.isRemote ? "CLIENT: " : "SERVER: ") + message);
    }

    public static void log (Object message) {
        System.out.println(message);
    }
}
