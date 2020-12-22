package com.tm.calemiutils.render;

import com.tm.calemiutils.block.BlockItemStand;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.tileentity.TileEntityItemStand;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderItemStand extends TileEntityRenderer<TileEntityItemStand> {

    private long lastTime;
    private float rot;

    public RenderItemStand (TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render (TileEntityItemStand stand, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        if (!stand.getInventory().getStackInSlot(0).isEmpty()) {

            ItemStack bookStack = stand.getInventory().getStackInSlot(0);

            long targetTime = 10;
            if (System.currentTimeMillis() - lastTime >= targetTime) {
                lastTime = System.currentTimeMillis();
                rot += 1F;
                rot %= 360;
            }

            matrixStack.push();

            matrixStack.translate(0.5D + stand.translation.getX() * 0.2D, 1.2D + stand.translation.getY() * 0.2D, 0.5D + stand.translation.getZ() * 0.2D);

            matrixStack.rotate(Vector3f.XP.rotationDegrees(stand.spin.getX() != 0 ? rot * stand.spin.getX() : stand.rotation.getX()));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(stand.spin.getY() != 0 ? rot * stand.spin.getY() : stand.rotation.getY()));
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(stand.spin.getZ() != 0 ? rot * stand.spin.getZ() : stand.rotation.getZ()));

            //Pivot
            matrixStack.translate(stand.pivot.getX() * 0.2D, stand.pivot.getY() * 0.2D, stand.pivot.getZ() * 0.2D);


            matrixStack.scale(1.5F * stand.scale.getX() + 0.5F, 1.5F * stand.scale.getY() + 0.5F, 1.5F * stand.scale.getZ() + 0.5F);

            renderItem(bookStack, partialTicks, matrixStack, buffer, combinedLight);

            matrixStack.pop();
        }

        //Render a floating wrench if the clear display is chosen.
        //This prevents Item Stands from getting lost.
        if (Minecraft.getInstance().player.getHeldItemMainhand().getItem() == InitItems.SECURITY_WRENCH.get()) {

            if (stand.getBlockState().get(BlockItemStand.DISPLAY_ID) == 3) {

                matrixStack.push();
                matrixStack.translate(0.5D, 0.4D, 0.5D);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(rot));
                matrixStack.scale(1F, 1F, 1F);

                renderItem(new ItemStack(InitItems.SECURITY_WRENCH.get()), partialTicks, matrixStack, buffer, combinedLight);

                matrixStack.pop();
            }
        }
    }

    private void renderItem (ItemStack stack, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight) {
        Minecraft.getInstance().getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.GROUND, combinedLight, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
    }
}
