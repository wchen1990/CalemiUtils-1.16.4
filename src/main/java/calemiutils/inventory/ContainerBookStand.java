package calemiutils.inventory;

import calemiutils.init.InitContainerTypes;
import calemiutils.init.InitItems;
import calemiutils.inventory.base.ContainerBase;
import calemiutils.inventory.base.SlotFilter;
import calemiutils.tileentity.TileEntityBookStand;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;

public class ContainerBookStand extends ContainerBase {

    public ContainerBookStand (final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
        this(windowId, playerInventory, (TileEntityBookStand) getTileEntity(playerInventory, data));
    }

    public ContainerBookStand (final int windowId, final PlayerInventory playerInventory, final TileEntityBookStand tileEntity) {
        super(InitContainerTypes.BOOK_STAND.get(), windowId, playerInventory, tileEntity, 8, 41);
        tileEntity.containerSlots.set(0, addSlot(new SlotFilter(tileEntity.getInventory(), 0, 80, 18, InitItems.LINK_BOOK_LOCATION.get())));
    }
}
