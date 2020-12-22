package calemiutils.inventory.base;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

/**
 * The Tile Entity for Items.
 */
public class ItemStackInventory implements IInventory {

    public final NonNullList<ItemStack> slots;

    public ItemStackInventory (ItemStack stack, int size) {

        slots = NonNullList.withSize(size, ItemStack.EMPTY);

        CompoundNBT mainTag = stack.getTag();

        if (mainTag != null && mainTag.get("inv") != null) {
            CompoundNBT itemListTag = mainTag.getCompound("inv");
            ItemStackHelper.loadAllItems(itemListTag, slots);
        }
    }

    public void dump (ItemStack stack) {

        CompoundNBT mainTag = stack.getTag();

        if (mainTag == null) {
            mainTag = new CompoundNBT();
        }

        CompoundNBT itemListTag = new CompoundNBT();
        ItemStackHelper.saveAllItems(itemListTag, slots);
        mainTag.put("inv", itemListTag);
        stack.setTag(mainTag);
    }

    @Override
    public ItemStack getStackInSlot (int slot) {
        return slots.get(slot);
    }

    @Override
    public ItemStack decrStackSize (int index, int count) {

        ItemStack itemstack;

        if (this.slots.get(index).getCount() <= count) {
            itemstack = this.slots.get(index);
            this.slots.set(index, ItemStack.EMPTY);
        }

        else {
            itemstack = this.slots.get(index).split(count);
            if (slots.get(index) == ItemStack.EMPTY) {
                this.slots.set(index, ItemStack.EMPTY);
            }
        }

        return itemstack;
    }

    @Override
    public void setInventorySlotContents (int slot, ItemStack stack) {

        this.slots.set(slot, stack);

        if (stack.getCount() > this.getInventoryStackLimit()) {
            decrStackSize(slot, stack.getCount() - this.getInventoryStackLimit());
        }
    }

    @Override
    public ItemStack removeStackFromSlot (int index) {
        ItemStack copy = getStackInSlot(index).copy();
        setInventorySlotContents(index, ItemStack.EMPTY);
        return copy;
    }

    @Override
    public boolean isEmpty () {

        for (ItemStack stack : slots) {
            if (!stack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void clear () {
        for (int i = 0; i < getSizeInventory(); i++) {
            slots.set(i, ItemStack.EMPTY);
        }
    }

    @Override
    public int getSizeInventory () {
        return slots.size();
    }

    @Override
    public int getInventoryStackLimit () {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot (int index, ItemStack stack) {
        return true;
    }

    @Override
    public boolean isUsableByPlayer (PlayerEntity player) {
        return true;
    }

    @Override
    public void markDirty () {}

    @Override
    public void openInventory (PlayerEntity player) {}

    @Override
    public void closeInventory (PlayerEntity player) {}
}
