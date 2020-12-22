package calemiutils.tileentity.base;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class TileEntityInventoryBase extends TileEntityBase implements ITileEntityGuiHandler, INamedContainerProvider {

    private ITextComponent customName;
    private final CUItemHandler inventory;
    public final List<Slot> containerSlots = new ArrayList<>();

    public TileEntityInventoryBase (TileEntityType<?> tileEntityType) {
        super(tileEntityType);

        this.inventory = new CUItemHandler(getSizeInventory(), containerSlots);

        for (int i = 0; i < inventory.getSlots(); i++) {
            containerSlots.add(null);
        }
    }

    public abstract ITextComponent getDefaultName();
    public abstract int getSizeInventory();

    public CUItemHandler getInventory() {
        return this.inventory;
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    public ITextComponent getCurrentName () {
        return customName != null ? customName : getDefaultName();
    }

    @Override
    public ITextComponent getDisplayName() {
        return getCurrentName();
    }

    @Override
    public <T> LazyOptional<T> getCapability (Capability<T> cap, Direction side) {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.orEmpty(cap, LazyOptional.of(() -> this.inventory));
    }

    @Nullable
    @Override
    public Container createMenu (int id, PlayerInventory playerInv, PlayerEntity player) {
        return getTileContainer(id, playerInv);
    }

    public boolean isItemValidForSlot (int index, ItemStack stack) {
        return containerSlots.get(index) != null && containerSlots.get(index).isItemValid(stack);
    }

    @Override
    public void read (BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);

        if (nbt.contains("CustomName", Constants.NBT.TAG_STRING)) {
            this.customName = ITextComponent.Serializer.getComponentFromJson(nbt.getString("CustomName"));
        }

        NonNullList<ItemStack> inv = NonNullList.withSize(this.inventory.getSlots(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, inv);
        this.inventory.setNonNullList(inv);
    }

    @Override
    public CompoundNBT write (CompoundNBT nbt) {

        if (this.customName != null) {
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(customName));
        }

        ItemStackHelper.saveAllItems(nbt, this.inventory.toNonNullList());
        return super.write(nbt);
    }
}
