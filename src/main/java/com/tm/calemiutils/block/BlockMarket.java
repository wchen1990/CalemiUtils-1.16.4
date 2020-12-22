package com.tm.calemiutils.block;

import com.tm.calemiutils.block.base.BlockContainerBase;
import com.tm.calemiutils.gui.ScreenMarket;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.tileentity.TileEntityMarket;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.LoreHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class BlockMarket extends BlockContainerBase {

    public BlockMarket() {
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(-1.0F, 3600000.0F));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LoreHelper.addInformationLore(tooltip, "A server-wide Market that allows Players to buy and sell Items.", true);
        LoreHelper.addInformationLore(tooltip, "Can automate the process with a Chest placed on top.");
        LoreHelper.addControlsLore(tooltip, "Open Gui", LoreHelper.Type.USE, true);
    }

    /**
     * This will handle opening the guis for options and inventory.
     */
    @Override
    public ActionResultType onBlockActivated (BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {

        Location location = new Location(world, pos);
        TileEntity tileEntity = location.getTileEntity();

        if (world.isRemote) {
            openGui(player, (TileEntityMarket) tileEntity);
        }

        return ActionResultType.SUCCESS;
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui (PlayerEntity player, TileEntityMarket market) {
        Minecraft.getInstance().displayGuiScreen(new ScreenMarket(player, market));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity (IBlockReader worldIn) {
        return InitTileEntityTypes.MARKET.get().create();
    }
}