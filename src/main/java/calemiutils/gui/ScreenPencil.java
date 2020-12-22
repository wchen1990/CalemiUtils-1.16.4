package calemiutils.gui;

import calemiutils.CalemiUtils;
import calemiutils.gui.base.PencilButton;
import calemiutils.gui.base.GuiScreenBase;
import calemiutils.packet.PacketPencilSetColor;
import calemiutils.util.helper.ScreenHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenPencil extends GuiScreenBase {

    private final PencilButton[] buttons = new PencilButton[16];

    public ScreenPencil (PlayerEntity player, Hand hand) {
        super(player, hand);
    }

    @Override
    protected void init () {

        super.init();

        for (int i = 0; i < buttons.length; i++) {
            int id = i;

            addButton(new PencilButton(id, getScreenX() + (i * 20) - 158, getScreenY() - 8, itemRenderer, (btn) -> {
                CalemiUtils.network.sendToServer(new PacketPencilSetColor(id, hand));
                player.closeScreen();
            }));
        }
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }

    @Override
    public String getGuiTextureName () {
        return null;
    }

    @Override
    public int getGuiSizeX () {
        return 0;
    }

    @Override
    public int getGuiSizeY () {
        return 0;
    }

    @Override
    public void drawGuiBackground (MatrixStack matrixStack, int mouseX, int mouseY) {

        for (int i = 0; i < DyeColor.values().length; i++) {
            int color = DyeColor.byId(i).getColorValue();
            ScreenHelper.drawColoredRect(getScreenX() + (i * 20) - 160, 0, 0, 20, this.height, color, 0.4F);
        }

        ScreenHelper.drawCenteredString(matrixStack, "Choose a Color", getScreenX(), getScreenY() - 25, 0, 0xFFFFFF);
    }

    @Override
    public void drawGuiForeground (MatrixStack matrixStack, int mouseX, int mouseY) {}

    @Override
    public boolean canCloseWithInvKey () {
        return true;
    }
}
