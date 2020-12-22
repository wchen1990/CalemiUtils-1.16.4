package calemiutils.tileentity;

import calemiutils.gui.ScreenOneSlot;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.inventory.ContainerBookStand;
import calemiutils.tileentity.base.ITileEntityGuiHandler;
import calemiutils.tileentity.base.TileEntityInventoryBase;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityBookStand extends TileEntityInventoryBase implements ITileEntityGuiHandler {

    private ItemStack lastStack;

    public TileEntityBookStand () {
        super(InitTileEntityTypes.BOOK_STAND.get());

        lastStack = ItemStack.EMPTY;
    }

    @Override
    public void tick () {

        if (world != null && !world.isRemote) {

            if (!lastStack.equals(getInventory().getStackInSlot(0))) {
                markForUpdate();
                lastStack = getInventory().getStackInSlot(0);
            }
        }
    }

    @Override
    public int getSizeInventory () {
        return 1;
    }

    @Override
    public ITextComponent getDefaultName () {
        return new StringTextComponent("Book Stand");
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerBookStand(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenOneSlot(getTileContainer(windowId, playerInv), playerInv, getDefaultName());
    }
}
