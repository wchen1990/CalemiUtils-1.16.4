package calemiutils.gui;

import calemiutils.CalemiUtils;
import calemiutils.gui.base.ButtonRect;
import calemiutils.gui.base.ContainerScreenBase;
import calemiutils.inventory.ContainerTorchPlacer;
import calemiutils.packet.PacketEnableTileEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenTorchPlacer extends ContainerScreenBase<ContainerTorchPlacer> {

    private ButtonRect activateBtn;

    public ScreenTorchPlacer (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    /**
     * @return The Tile Entities current enable state.
     */
    private String getEnabledText () {
        return getTileEntity().enable ? "Enabled" : "Disabled";
    }    
    
    @Override
    protected void init () {
        super.init();

        int btnWidth = 62;
        activateBtn = addButton(new ButtonRect(getScreenX() + (getGuiSizeX() / 2) - (btnWidth / 2), getScreenY() + 24, btnWidth, getEnabledText(), (btn) -> toggleActivate()));
    }    

    /**
     * Called when the activateBtn is pressed.
     * Handles toggling activation.
     */
    private void toggleActivate () {

        boolean value = !getTileEntity().enable;

        CalemiUtils.network.sendToServer(new PacketEnableTileEntity(value, getTileEntity().getPos()));
        getTileEntity().enable = value;

        activateBtn.setMessage(new StringTextComponent(getEnabledText()));
    }

    @Override
    protected void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX) {}

    @Override
    public int getGuiSizeY () {
        return 201;
    }

    @Override
    protected String getGuiTextureName () {
        return "torch_placer";
    }
}
