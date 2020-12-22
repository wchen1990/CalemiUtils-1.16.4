package com.tm.calemiutils.inventory;

import com.tm.calemiutils.init.InitContainerTypes;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.inventory.base.SlotFilter;
import com.tm.calemiutils.tileentity.TileEntityBank;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerBank extends ContainerBase {

    public ContainerBank (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityBank) getTileEntity(playerInventory, data));
    }

    public ContainerBank (final int windowId, final PlayerInventory playerInventory, final TileEntityBank tileEntity) {
        super(InitContainerTypes.BANK.get(), windowId, playerInventory, tileEntity, 8, 62);
        tileEntity.containerSlots.set(0, addSlot(new SlotFilter(tileEntity.getInventory(), 0, 62, 18, InitItems.COIN_PENNY.get(), InitItems.COIN_NICKEL.get(), InitItems.COIN_QUARTER.get(), InitItems.COIN_DOLLAR.get())));
        tileEntity.containerSlots.set(1, addSlot(new SlotFilter(tileEntity.getInventory(), 1, 98, 18, InitItems.WALLET.get())));
    }
}
