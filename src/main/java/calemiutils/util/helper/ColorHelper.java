package calemiutils.util.helper;

import net.minecraft.item.DyeColor;

public class ColorHelper {

    /**
     * Searches all colors for a matching name. Returns BLUE as default.
     * @param name The name of the color.
     */
    public static DyeColor getColorFromString(String name) {

        if (name != null) {

            for (DyeColor color : DyeColor.values()) {

                if (name.equalsIgnoreCase(color.getString())) {
                    return color;
                }
            }
        }

        return DyeColor.BLUE;
    }
}