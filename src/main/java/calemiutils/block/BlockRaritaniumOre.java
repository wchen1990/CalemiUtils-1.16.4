package calemiutils.block;

import calemiutils.config.CUConfig;
import calemiutils.block.base.BlockBase;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BlockRaritaniumOre extends BlockBase {

    static final Random rand = new Random();

    public BlockRaritaniumOre () {
        super(Block.Properties.create(Material.ROCK).sound(SoundType.STONE).hardnessAndResistance(3).harvestLevel(2).harvestTool(ToolType.PICKAXE));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Found between Y levels " + CUConfig.worldGen.raritaniumOreGenMinY.get() + " and " + CUConfig.worldGen.raritaniumOreGenMaxY.get() + ".", true);
    }

    @Override
    public int getExpDrop (BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        return silktouch == 0 ? rand.nextInt(3 + fortune) + 1 : 0;
    }
}
