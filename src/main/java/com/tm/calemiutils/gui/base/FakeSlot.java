package com.tm.calemiutils.gui.base;

import com.tm.calemiutils.util.helper.ScreenHelper;
import com.tm.calemiutils.util.helper.StringHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FakeSlot extends Button {

    private final ScreenRect rect;
    private final ItemRenderer itemRender;
    private ItemStack stack = new ItemStack(Items.AIR);

    /**
     * A fake slot. Can set its icon based on what Item is clicked into it.
     * @param pressable Called when the button is pressed.
     */
    public FakeSlot (int x, int y, ItemRenderer itemRender, Button.IPressable pressable) {
        super(x, y, 16, 16, new StringTextComponent(""), pressable);
        rect = new ScreenRect(this.x, this.y, width, height);
        this.itemRender = itemRender;
    }

    @Override
    public void renderButton (MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        if (this.visible && !stack.isEmpty()) {

            List<String> list = new ArrayList<>();
            List<ITextComponent> lore = stack.getTooltip(Minecraft.getInstance().player, ITooltipFlag.TooltipFlags.NORMAL);

            for (ITextComponent component : lore) {
                list.add(component.getString());
            }

            StringHelper.removeNullsFromList(list);
            StringHelper.removeCharFromList(list, "Shift", "Ctrl");

            ScreenHelper.drawItemStack(itemRender, getItemStack(), rect.x, rect.y);

            GL11.glPushMatrix();
            GL11.glColor4f(1, 1, 1, 0.75F);
            ScreenHelper.drawHoveringTextBox(matrixStack, mouseX, mouseY, 300, rect, StringHelper.getArrayFromList(list));
            GL11.glColor4f(1, 1, 1, 1);
            GL11.glPopMatrix();
        }
    }

    private ItemStack getItemStack () {
        return stack;
    }

    public void setItemStack (ItemStack stack) {
        this.stack = stack;
    }
}
