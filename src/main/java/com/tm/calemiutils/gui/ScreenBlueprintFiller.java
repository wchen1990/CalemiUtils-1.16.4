package com.tm.calemiutils.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tm.calemiutils.gui.base.ButtonRect;
import com.tm.calemiutils.gui.base.ContainerScreenBase;
import com.tm.calemiutils.gui.base.FakeSlot;
import com.tm.calemiutils.inventory.ContainerBlueprintFiller;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.packet.PacketBlueprintFiller;
import com.tm.calemiutils.tileentity.TileEntityBlueprintFiller;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.StringHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScreenBlueprintFiller extends ContainerScreenBase<ContainerBlueprintFiller> {

    private ButtonRect scanBtn;
    private ButtonRect activateBtn;

    private final FakeSlot[] filters = new FakeSlot[16];

    public ScreenBlueprintFiller (Container container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
    }

    public TileEntityBlueprintFiller getFiller() {
        return (TileEntityBlueprintFiller) getTileEntity();
    }

    /**
     * @return The Tile Entities current enable state.
     */
    private String getEnabledText () {
        return getFiller().enable ? "Enabled" : "Disabled";
    }

    /**
     * @return The Tile Entities current scan state.
     */
    private String getScanText () {
        return getFiller().isScanning() ? "Scanning" : "Scan";
    }

    @Override
    protected void init () {
        super.init();

        scanBtn = addButton(new ButtonRect(getScreenX() + 28, getScreenY() + 18, 48, getScanText(), (btn) -> startScan()));
        activateBtn = addButton(new ButtonRect(getScreenX() + 100, getScreenY() + 18, 48, getEnabledText(), (btn) -> enable()));

        for (int i = 0; i < filters.length; i++) {

            int index = i;

            int yOffset = 0;
            if (i > 7) yOffset = 18;

            filters[i] = addButton(new FakeSlot(getScreenX() + 17 + ((i % 8) * 18), getScreenY() + 41 + yOffset, itemRenderer, (btn) -> setFakeSlot(index)));
            filters[i].setItemStack(getFiller().getFilter(i));
        }
    }

    /**
     * Called when the activateBtn is pressed.
     * Handles toggling activation.
     */
    private void enable () {

        if (!getFiller().isScanning()) {

            CalemiUtils.network.sendToServer(new PacketBlueprintFiller("syncenable", getFiller().getPos(), true));
            getFiller().setEnable(true);
        }
    }

    private void startScan () {

        if (!getFiller().enable) {

            CalemiUtils.network.sendToServer(new PacketBlueprintFiller("startscan", getFiller().getPos()));
            getFiller().startScan();
        }
    }

    /**
     * Called when a fakeSlot button is pressed.
     * Sets fakeSlot's icon to the hovered Stack and syncs it.
     */
    private void setFakeSlot (int index) {

        ItemStack stack = new ItemStack(playerInventory.getItemStack().getItem(), 1);
        if (playerInventory.getItemStack().hasTag()) stack.setTag(playerInventory.getItemStack().getTag());

        CalemiUtils.network.sendToServer(new PacketBlueprintFiller("syncfilter", getFiller().getPos(), stack, index));
        getFiller().setFilter(index, stack);
        filters[index].setItemStack(stack);
    }

    @Override
    public void tick() {

        activateBtn.setMessage(new StringTextComponent(getEnabledText()));
        scanBtn.setMessage(new StringTextComponent(getScanText()));

        super.tick();
    }

    @Override
    protected void drawGuiForeground(MatrixStack matrixStack, int mouseX, int mouseY) {

        addInfoIcon(0);

        List<String> list = new ArrayList<>();

        list.add("Scanned Blueprint: " + getFiller().getScannedBlueprint().size() + " / " + getFiller().getScaledRange());

        for (int i = 0; i < getFiller().getAmounts().length; i++) {

            if (getFiller().getAmounts()[i] != 0) {
                list.add(DyeColor.values()[i].getString().toUpperCase() + ": " + ItemHelper.countByStacks(getFiller().getAmounts()[i]));
            }
        }

        addInfoHoveringText(matrixStack, mouseX, mouseY, StringHelper.getArrayFromList(list));

        leftTabOffset += 17;
    }

    @Override
    protected void drawGuiBackground(MatrixStack matrixStack, int mouseY, int mouseX) {}

    @Override
    public int getGuiSizeY () {
        return 172;
    }

    @Override
    protected String getGuiTextureName () {
        return "blueprint_filler";
    }
}
