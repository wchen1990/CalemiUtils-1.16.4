package calemiutils.item;

import calemiutils.CalemiUtils;
import calemiutils.block.BlockBlueprint;
import calemiutils.gui.ScreenPencil;
import calemiutils.init.InitItems;
import calemiutils.item.base.ItemBase;
import calemiutils.util.Location;
import calemiutils.util.helper.ItemHelper;
import calemiutils.util.helper.LoreHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPencil extends ItemBase {

    public ItemPencil () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Places Blueprint. Blueprint can be used for mass building!", true);
        LoreHelper.addControlsLore(tooltipList, "Place Blueprint", LoreHelper.Type.USE, true);
        LoreHelper.addControlsLore(tooltipList, "Change Blueprint Color", LoreHelper.Type.SNEAK_USE);
        LoreHelper.addBlankLine(tooltipList);
        tooltipList.add(new StringTextComponent(TextFormatting.GRAY + "Color: " + TextFormatting.AQUA + (DyeColor.byId(getColorId(stack)).getString()).toUpperCase()));
    }

    /**
     * Gets the color id of a given Pencil Stack.
     */
    public int getColorId (ItemStack stack) {

        int meta = 11;

        if (ItemHelper.getNBT(stack).contains("color")) {
            meta = ItemHelper.getNBT(stack).getInt("color");
        }

        return meta;
    }

    /**
     * Sets the Pencil's color by id.
     */
    public void setColorById (ItemStack stack, int meta) {
        ItemHelper.getNBT(stack).putInt("color", meta);
    }

    /**
     * Handles placing Blueprint & opening the GUI.
     */
    @Override
    public ActionResultType onItemUse (ItemUseContext context) {

        World world = context.getWorld();
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        Direction dir = context.getFace();
        Hand hand = context.getHand();

        BlockBlueprint BLUEPRINT = (BlockBlueprint) InitItems.BLUEPRINT.get();
        Location location = new Location(world, pos);

        //Checks if the Player exists.
        if (player != null) {

            //If the Player is crouching, open the GUI.
            if (player.isCrouching()) {

                if (world.isRemote) {
                    openGui(player, hand);
                }

                return ActionResultType.SUCCESS;
            }

            //If the Player is not crouching, check some things.

            //Checks if the clicked Location can be replaced.
            if (!location.getBlockState().getMaterial().isReplaceable()) {

                location = new Location(location, dir);

                if (!location.isBlockValidForPlacing()) return ActionResultType.FAIL;
            }

            //Checks if the Player can edit the Location.
            if (!player.canPlayerEdit(pos, dir, player.getHeldItem(hand))) return ActionResultType.FAIL;

            else {

                if (location.isBlockValidForPlacing()) {
                    location.setBlock(BLUEPRINT.getDefaultState().with(BlockBlueprint.COLOR, DyeColor.byId(getColorId(context.getItem()))));
                }

                return ActionResultType.SUCCESS;
            }
        }

        return ActionResultType.FAIL;
    }

    /**
     * Handles opening the GUI.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);

        if (world.isRemote && player.isCrouching()) {
            openGui(player, hand);
            return new ActionResult<>(ActionResultType.SUCCESS, stack);
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    @OnlyIn(Dist.CLIENT)
    private void openGui (PlayerEntity player, Hand hand) {
        Minecraft.getInstance().displayGuiScreen(new ScreenPencil(player, hand));
    }
}
