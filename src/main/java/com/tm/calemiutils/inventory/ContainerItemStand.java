package com.tm.calemiutils.inventory;

import com.tm.calemiutils.init.InitContainerTypes;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.tileentity.TileEntityItemStand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerItemStand extends ContainerBase {

    public ContainerItemStand (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityItemStand) getTileEntity(playerInventory, data));
    }

    public ContainerItemStand (final int windowId, final PlayerInventory playerInventory, final TileEntityItemStand tileEntity) {
        super(InitContainerTypes.ITEM_STAND.get(), windowId, playerInventory, tileEntity, 8, 41);
        addSlot(new SlotItemHandler(tileEntity.getInventory(), 0, 80, 18));
    }
}
