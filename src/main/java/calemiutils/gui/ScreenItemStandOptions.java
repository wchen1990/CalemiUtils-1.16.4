package calemiutils.gui;

import calemiutils.CalemiUtils;
import calemiutils.block.BlockItemStand;
import calemiutils.gui.base.ButtonRect;
import calemiutils.gui.base.GuiScreenBase;
import calemiutils.gui.base.TextFieldRect;
import calemiutils.packet.PacketItemStand;
import calemiutils.tileentity.TileEntityItemStand;
import calemiutils.util.UnitChatMessage;
import calemiutils.util.helper.ScreenHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScreenItemStandOptions extends GuiScreenBase {

    private final TileEntityItemStand stand;
    private final List<TextFieldRect> fields = new ArrayList<>();
    private TextFieldRect transXField, transYField, transZField;
    private TextFieldRect rotXField, rotYField, rotZField;
    private TextFieldRect spinXField, spinYField, spinZField;
    private TextFieldRect scaleXField, scaleYField, scaleZField;
    private TextFieldRect pivotXField, pivotYField, pivotZField;

    public ScreenItemStandOptions (PlayerEntity player, TileEntityItemStand stand) {
        super(player, Hand.MAIN_HAND);
        this.stand = stand;
    }

    @Override
    protected void init () {
        super.init();

        if (minecraft != null) {

            addButton(new ButtonRect(getScreenX() - 50 - 70, getScreenY() - 65, 100, "Cycle Displays", (btn) -> cycleDisplay()));

            addButton(new ButtonRect(getScreenX() - 50 + 70, getScreenY() - 65, 40, "Copy", (btn) -> {
                convertFieldsToFloats();
                clampAllOptions();
                copyOptions();
                setAndSyncOptions();
            }));

            addButton(new ButtonRect(getScreenX() - 50 + 70 + 50, getScreenY() - 65, 40, "Paste", (btn) -> {
                convertFieldsToFloats();
                clampAllOptions();
                pasteOptions();
                setAndSyncOptions();
            }));

            int xSpread = 45;
            int xOffset = -70;
            int yOffset = -20;

            transXField = initField(stand.translation.getX(), -xSpread + xOffset, yOffset);
            transYField = initField(stand.translation.getY(), xOffset, yOffset);
            transZField = initField(stand.translation.getZ(), xSpread + xOffset, yOffset);

            xOffset = 70;
            scaleXField = initField(stand.scale.getX(), -xSpread + xOffset, yOffset);
            scaleYField = initField(stand.scale.getY(), xOffset, yOffset);
            scaleZField = initField(stand.scale.getZ(), xSpread + xOffset, yOffset);

            yOffset = 20;
            spinXField = initField(stand.spin.getX(), -xSpread + xOffset, yOffset);
            spinYField = initField(stand.spin.getY(), xOffset, yOffset);
            spinZField = initField(stand.spin.getZ(), xSpread + xOffset, yOffset);

            xOffset = -70;
            rotXField = initField(stand.rotation.getX(), -xSpread + xOffset, yOffset);
            rotYField = initField(stand.rotation.getY(), xOffset, yOffset);
            rotZField = initField(stand.rotation.getZ(), xSpread + xOffset, yOffset);

            xOffset = 0;
            yOffset = 60;
            pivotXField = initField(stand.pivot.getX(), -xSpread + xOffset, yOffset);
            pivotYField = initField(stand.pivot.getY(), xOffset, yOffset);
            pivotZField = initField(stand.pivot.getZ(), xSpread + xOffset, yOffset);
        }
    }

    /**
     * Used to create a field with reduced code.
     */
    private TextFieldRect initField (float value, int x, int y) {

        if (minecraft != null) {

            TextFieldRect field = new TextFieldRect(minecraft.fontRenderer, getScreenX() + x - 20, getScreenY() + y - 7, 40, 5, "" + value);
            children.add(field);
            fields.add(field);
            return field;
        }

        return null;
    }

    /**
     * Called when the Cycle Display button is pressed.
     * Handles the cycling of the Block's state.
     */
    private void cycleDisplay () {
        BlockState state = stand.getBlockState();
        int displayID = state.get(BlockItemStand.DISPLAY_ID) + 1;
        displayID %= 4;
        stand.getLocation().setBlock(stand.getBlockState().with(BlockItemStand.DISPLAY_ID, displayID));
        CalemiUtils.network.sendToServer(new PacketItemStand("syncdisplay", stand.getPos(), displayID));
    }

    /**
     * Converts every field's text to a float. If the text contains a non-number, set it to 0.0.
     */
    private void convertFieldsToFloats () {

        for (TextFieldRect field : fields) {

            try {
                Float.parseFloat(field.getText());
            }

            catch (NumberFormatException | NullPointerException nfe) {
                field.setText("0.0");
            }
        }
    }

    /**
     * Clamps every field's text to its respective minimums and maximums.
     * WARNING: Make sure they are all converted to floats!
     */
    private void clampAllOptions () {

        clampOption(transXField, -10, 10);
        clampOption(transYField, -10, 10);
        clampOption(transZField, -10, 10);

        clampOption(rotXField, -360, 360);
        clampOption(rotYField, -360, 360);
        clampOption(rotZField, -360, 360);

        clampOption(spinXField, -5, 5);
        clampOption(spinYField, -5, 5);
        clampOption(spinZField, -5, 5);

        clampOption(scaleXField, 0.1F, 2);
        clampOption(scaleYField, 0.1F, 2);
        clampOption(scaleZField, 0.1F, 2);

        clampOption(pivotXField, -10, 10);
        clampOption(pivotYField, -10, 10);
        clampOption(pivotZField, -10, 10);
    }

    /**
     * Clamps a given text field to a minimum and maximum.
     * WARNING: Make sure its converted to a float!
     */
    private void clampOption (TextFieldRect field, float min, float max) {

        float value = Float.parseFloat(field.getText());
        field.setText("" + MathHelper.clamp(value, min, max));
    }

    /**
     * Copies all set options from every field to the client's clipboard.
     */
    private void copyOptions () {

        StringBuilder str = new StringBuilder();

        for (TextFieldRect field : fields) {

            str.append(field.getMessage());
            str.append("%");
        }

        str.append(stand.getBlockState().get(BlockItemStand.DISPLAY_ID));
        Minecraft.getInstance().keyboardListener.setClipboardString(str.toString());
    }

    /**
     * Pastes options to every field from the client's clipboard.
     */
    private void pasteOptions () {

        UnitChatMessage unitMessage = new UnitChatMessage("Item Stand", Minecraft.getInstance().player);

        String clipboard = Minecraft.getInstance().keyboardListener.getClipboardString();
        String[] data = clipboard.split("%");

        if (data.length == 16) {

            for (int i = 0; i < data.length - 1; i++) {

                String value = data[i];

                try {
                    Float.parseFloat(value);
                }

                catch (NumberFormatException | NullPointerException nfe) {
                    value = "0";
                }

                fields.get(i).setText(value);
            }

            stand.getLocation().setBlock(stand.getBlockState().with(BlockItemStand.DISPLAY_ID, Integer.parseInt(data[data.length - 1])));
            CalemiUtils.network.sendToServer(new PacketItemStand("syncdisplay", stand.getPos(), Integer.parseInt(data[data.length - 1])));

            unitMessage.printMessage(TextFormatting.GREEN, "Successfully pasted values from clipboard!");
        }

        else {
            unitMessage.printMessage(TextFormatting.RED, "Invalid values in clipboard!");
        }
    }

    /**
     * Sets and syncs all set options to the Item Stand.
     */
    private void setAndSyncOptions () {

        float transX = Float.parseFloat(transXField.getText());
        float transY = Float.parseFloat(transYField.getText());
        float transZ = Float.parseFloat(transZField.getText());

        float rotX = Float.parseFloat(rotXField.getText());
        float rotY = Float.parseFloat(rotYField.getText());
        float rotZ = Float.parseFloat(rotZField.getText());

        float spinX = Float.parseFloat(spinXField.getText());
        float spinY = Float.parseFloat(spinYField.getText());
        float spinZ = Float.parseFloat(spinZField.getText());

        float scaleX = Float.parseFloat(scaleXField.getText());
        float scaleY = Float.parseFloat(scaleYField.getText());
        float scaleZ = Float.parseFloat(scaleZField.getText());

        float pivotX = Float.parseFloat(pivotXField.getText());
        float pivotY = Float.parseFloat(pivotYField.getText());
        float pivotZ = Float.parseFloat(pivotZField.getText());

        stand.translation = new Vector3f(transX, transY, transZ);
        stand.rotation = new Vector3f(rotX, rotY, rotZ);
        stand.spin = new Vector3f(spinX, spinY, spinZ);
        stand.scale = new Vector3f(scaleX, scaleY, scaleZ);
        stand.pivot = new Vector3f(pivotX, pivotY, pivotZ);

        CalemiUtils.network.sendToServer(new PacketItemStand("syncoptions", stand.getPos(), stand.translation, stand.rotation, stand.spin, stand.scale, stand.pivot));
    }

    @Override
    public void drawGuiBackground (MatrixStack matrixStack, int mouseX, int mouseY) {

        for (TextFieldRect field : fields) {
            field.render(matrixStack, mouseX, mouseY, 0);
        }

        ScreenHelper.drawCenteredString(matrixStack, "Translation", getScreenX() - 70, getScreenY() - 36, 0, 0xFFFFFF);
        ScreenHelper.drawCenteredString(matrixStack, "Scale", getScreenX() + 70, getScreenY() - 36, 0, 0xFFFFFF);
        ScreenHelper.drawCenteredString(matrixStack, "Rotation", getScreenX() - 70, getScreenY() + 4, 0, 0xFFFFFF);
        ScreenHelper.drawCenteredString(matrixStack, "Spin", getScreenX() + 70, getScreenY() + 4, 0, 0xFFFFFF);
        ScreenHelper.drawCenteredString(matrixStack, "Pivot", getScreenX(), getScreenY() + 44, 0, 0xFFFFFF);
    }

    @Override
    public void drawGuiForeground (MatrixStack matrixStack, int mouseX, int mouseY) {}

    @Override
    public boolean keyPressed (int id, int i2, int i3) {

        //If the enter key is pressed, convert, clamp, and set all options.
        if (id == 257) {
            convertFieldsToFloats();
            clampAllOptions();
            setAndSyncOptions();
        }

        return super.keyPressed(id, i2, i3);
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
    public String getGuiTextureName () {
        return null;
    }

    @Override
    public boolean canCloseWithInvKey () {
        return true;
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }
}
