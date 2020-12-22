package calemiutils.gui.base;

import calemiutils.init.InitItems;
import calemiutils.util.helper.ItemHelper;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PencilButton extends ItemStackButton {

    private final int colorId;

    /**
     * A colored Pencil button. Used to set the color of a Pencil.
     * @param colorId Used to determine what color of Pencil to render.
     * @param pressable Called when the button is pressed.
     */
    public PencilButton (int colorId, int x, int y, ItemRenderer itemRender, Button.IPressable pressable) {
        super(x, y, itemRender, pressable);
        this.colorId = colorId;
    }

    @Override
    public ItemStack getRenderedStack() {
        ItemStack stack = new ItemStack(InitItems.PENCIL.get());
        ItemHelper.getNBT(stack).putInt("color", colorId);
        return stack;
    }

    @Override
    public String[] getTooltip() {
        return new String[] {DyeColor.byId(colorId).getString().toUpperCase()};
    }
}
