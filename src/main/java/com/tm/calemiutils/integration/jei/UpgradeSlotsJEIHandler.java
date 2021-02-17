package com.tm.calemiutils.integration.jei;

import com.tm.calemiutils.gui.base.ContainerScreenBase;
import com.tm.calemiutils.tileentity.base.TileEntityUpgradable;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to add special behaviors to JEI.
 */
public class UpgradeSlotsJEIHandler implements IGuiContainerHandler<ContainerScreenBase<?>> {

    /**
     * Prevents JEI from rendering Items over the GUI.
     */
    @Override
    public List<Rectangle2d> getGuiExtraAreas (ContainerScreenBase containerScreen) {
        List<Rectangle2d> list = new ArrayList<>();

        TileEntity tileEntity = containerScreen.getTileEntity();

        int sizeY = 0;

        if (tileEntity instanceof TileEntityUpgradable) {
            sizeY += 50;
        }

        list.add(new Rectangle2d(containerScreen.getScreenX() + containerScreen.getGuiSizeX(), containerScreen.getScreenY(), 25, sizeY));
        return list;
    }
}
