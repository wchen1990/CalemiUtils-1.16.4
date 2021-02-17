package com.tm.calemiutils.inventory;

import com.tm.calemiutils.init.InitContainerTypes;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.inventory.base.SlotFilter;
import com.tm.calemiutils.tileentity.TileEntityBlueprintFiller;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerBlueprintFiller extends ContainerBase {

    public ContainerBlueprintFiller (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityBlueprintFiller) getTileEntity(playerInventory, data));
    }

    public ContainerBlueprintFiller (final int windowId, final PlayerInventory playerInventory, final TileEntityBlueprintFiller tileEntity) {
        super(InitContainerTypes.BLUEPRINT_FILLER.get(), windowId, playerInventory, tileEntity, 8, 90);

        tileEntity.upgradeSlots.add(addSlot(new SlotFilter(tileEntity.getUpgradeInventory(), 0, 178, 7, InitItems.SPEED_UPGRADE.get())));
        tileEntity.upgradeSlots.add(addSlot(new SlotFilter(tileEntity.getUpgradeInventory(), 1, 178, 31, InitItems.RANGE_UPGRADE.get())));
    }
}
