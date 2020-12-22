package com.tm.calemiutils.block;

import com.tm.calemiutils.block.base.BlockContainerBase;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.util.helper.LoreHelper;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.List;

public class BlockMobBeacon extends BlockContainerBase {

    public BlockMobBeacon () {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(-1.0F, 3600000.0F));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "Prevents natural enemy spawning within a chunk. (Won't prevent Spawners)", true);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity (IBlockReader worldIn) {
        return InitTileEntityTypes.MOB_BEACON.get().create();
    }
}
