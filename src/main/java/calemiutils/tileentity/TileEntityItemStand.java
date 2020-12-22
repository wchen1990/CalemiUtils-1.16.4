package calemiutils.tileentity;

import calemiutils.gui.ScreenOneSlot;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.inventory.ContainerItemStand;
import calemiutils.tileentity.base.ITileEntityGuiHandler;
import calemiutils.tileentity.base.TileEntityInventoryBase;
import calemiutils.util.helper.NBTHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityItemStand extends TileEntityInventoryBase implements ITileEntityGuiHandler {

    public Vector3f translation, rotation, spin, scale, pivot;

    private ItemStack lastStack;

    public TileEntityItemStand () {
        super(InitTileEntityTypes.ITEM_STAND.get());

        translation = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        spin = new Vector3f(0, 0, 0);
        scale = new Vector3f(1, 1, 1);
        pivot = new Vector3f(0, 0, 0);

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
    @OnlyIn(Dist.CLIENT)
    public double getMaxRenderDistanceSquared () {
        return 8000.0D;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox () {
        return super.getRenderBoundingBox().expand(new Vector3d(3, 3, 3));
    }

    @Override
    public int getSizeInventory () {
        return 1;
    }

    @Override
    public ITextComponent getDefaultName () {
        return new StringTextComponent("Item Stand");
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerItemStand(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenOneSlot(getTileContainer(windowId, playerInv), playerInv, getDefaultName());
    }

    @Override
    public void read (BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        translation = NBTHelper.readVector(nbt, "Translate");
        rotation = NBTHelper.readVector(nbt, "Rotation");
        spin = NBTHelper.readVector(nbt, "Spin");
        scale = NBTHelper.readVector(nbt, "Scale");
        pivot = NBTHelper.readVector(nbt, "Pivot");
    }

    @Override
    public CompoundNBT write (CompoundNBT nbt) {

        NBTHelper.writeVector(translation, "Translate", nbt);
        NBTHelper.writeVector(rotation, "Rotation", nbt);
        NBTHelper.writeVector(spin, "Spin", nbt);
        NBTHelper.writeVector(scale, "Scale", nbt);
        NBTHelper.writeVector(pivot, "Pivot", nbt);

        return super.write(nbt);
    }
}
