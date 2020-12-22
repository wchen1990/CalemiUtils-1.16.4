package com.tm.calemiutils.inventory;

import com.tm.calemiutils.init.InitContainerTypes;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.tileentity.TileEntityTradingPost;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerTradingPost extends ContainerBase {

    public ContainerTradingPost (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityTradingPost) getTileEntity(playerInventory, data));
    }

    public ContainerTradingPost (final int windowId, final PlayerInventory playerInventory, final TileEntityTradingPost tileEntity) {
        super(InitContainerTypes.TRADING_POST.get(), windowId, playerInventory, tileEntity, 8, 150);
        addTileEntityStorageInv(tileEntity.getInventory(), 0, 8, 83, 3);
    }
}
