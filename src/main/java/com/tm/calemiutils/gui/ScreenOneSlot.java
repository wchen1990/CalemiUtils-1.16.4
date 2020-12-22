package com.tm.calemiutils.gui;

import com.tm.calemiutils.gui.base.ContainerScreenBase;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenOneSlot extends ContainerScreenBase<ContainerBase> {

    public ScreenOneSlot (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    @Override
    public void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {}

    @Override
    public void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX) {}

    @Override
    public int getGuiSizeY () {
        return 123;
    }

    @Override
    public String getGuiTextureName () {
        return "one_slot";
    }
}
