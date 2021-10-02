package com.tm.calemiutils.inventory;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.init.InitContainerTypes;
import com.tm.calemiutils.init.InitItems;
import com.tm.calemiutils.inventory.base.ContainerBase;
import com.tm.calemiutils.inventory.base.SlotFilter;
import com.tm.calemiutils.tileentity.TileEntityBank;
import com.tm.calemiutils.util.helper.ItemHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;

import java.util.stream.Stream;

public class ContainerBank extends ContainerBase {

    public ContainerBank (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityBank) getTileEntity(playerInventory, data));
    }

    public ContainerBank (final int windowId, final PlayerInventory playerInventory, final TileEntityBank tileEntity) {
        super(InitContainerTypes.BANK.get(), windowId, playerInventory, tileEntity, 8, 62);
        Item[] combinedList = Stream.concat(CUConfig.coins.stream(), CUConfig.coinStacks.stream()).map((itemStr) -> ItemHelper.getItemFromString(itemStr)).toArray(Item[]::new);
        tileEntity.containerSlots.set(0, addSlot(new SlotFilter(tileEntity.getInventory(), 0, 62, 18, combinedList)));
        tileEntity.containerSlots.set(1, addSlot(new SlotFilter(tileEntity.getInventory(), 1, 98, 18, InitItems.WALLET.get())));
    }
}
