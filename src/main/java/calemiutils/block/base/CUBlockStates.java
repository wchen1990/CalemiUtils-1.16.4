package calemiutils.block.base;

import net.minecraft.item.DyeColor;
import net.minecraft.state.EnumProperty;

/**
 * General Properties for Blocks and Items
 */
public class CUBlockStates {

    public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);
}
