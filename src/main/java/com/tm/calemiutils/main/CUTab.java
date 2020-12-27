package com.tm.calemiutils.main;

import com.tm.calemiutils.init.InitItems;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUTab extends ItemGroup {

    public CUTab() {
        super(CUReference.MOD_ID + ".tabMain");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack createIcon() {
        return new ItemStack(InitItems.PENCIL.get());
    }
}
