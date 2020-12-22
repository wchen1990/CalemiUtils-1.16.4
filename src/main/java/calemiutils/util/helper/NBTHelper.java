package calemiutils.util.helper;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3f;

public class NBTHelper {

    public static Vector3f readVector (CompoundNBT nbt, String tagName) {
        return new Vector3f(nbt.getFloat(tagName + "X"), nbt.getFloat(tagName + "Y"), nbt.getFloat(tagName + "Z"));
    }

    public static void writeVector (Vector3f vector, String tagName, CompoundNBT nbt) {
        nbt.putFloat(tagName + "X", vector.getX());
        nbt.putFloat(tagName + "Y", vector.getY());
        nbt.putFloat(tagName + "Z", vector.getZ());
    }

    public static ItemStack readItem (CompoundNBT nbt, int index) {

        CompoundNBT tag = (CompoundNBT) nbt.get("SingleItem" + index);

        if (tag == null) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = new ItemStack(Items.AIR);
        stack.setTag(tag);
        return stack;
    }

    public static void writeItem (CompoundNBT nbt, ItemStack stack, int index) {

        CompoundNBT newNBT = new CompoundNBT();

        if (stack != null && !stack.isEmpty()) {
            stack.write(newNBT);
        }

        nbt.put("SingleItem" + index, newNBT);
    }
}
