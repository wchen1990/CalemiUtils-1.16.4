package com.tm.calemiutils.render;

import com.tm.calemiutils.block.BlockBookStand;
import com.tm.calemiutils.tileentity.TileEntityBookStand;
import com.tm.calemiutils.util.Location;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderBookStand extends TileEntityRenderer<TileEntityBookStand> {

    public RenderBookStand (TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render (TileEntityBookStand tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        if (!tileEntity.getInventory().getStackInSlot(0).isEmpty()) {

            ItemStack bookStack = tileEntity.getInventory().getStackInSlot(0);
            Location location = tileEntity.getLocation();

            Direction dir = location.getForgeBlockState().getBlockState().get(BlockBookStand.FACING);
            int rotation = 0;

            switch (dir) {
                case EAST:
                    rotation = -90;
                    break;
                case SOUTH:
                    rotation = -180;
                    break;
                case WEST:
                    rotation = -270;
            }

            matrixStack.push();
            matrixStack.translate(0.5D, 0.505D, 0.5D);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(rotation));
            matrixStack.scale(2F, 2F, 2F);

            renderItem(bookStack, partialTicks, matrixStack, buffer, combinedLight);

            matrixStack.pop();
        }
    }

    private void renderItem (ItemStack stack, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight) {
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
    }
}
