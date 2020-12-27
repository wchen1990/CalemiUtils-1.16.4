package com.tm.calemiutils.item;

import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.UnitChatMessage;
import com.tm.calemiutils.util.helper.LoreHelper;
import com.tm.calemiutils.util.helper.SoundHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBrush extends ItemBase {

    public Location location1, location2;

    public ItemBrush () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Creates shapes of blueprint for all your building needs! Use /cutils for commands.", true);
        LoreHelper.addControlsLore(tooltipList, "Marks the first point", LoreHelper.Type.USE, true);
        LoreHelper.addControlsLore(tooltipList, "Marks the second point", LoreHelper.Type.SNEAK_USE);
        LoreHelper.addBlankLine(tooltipList);
        tooltipList.add(new StringTextComponent(TextFormatting.GRAY + "Position 1: " + TextFormatting.AQUA + (location1 != null ? location1.toString() : "Not set")));
        tooltipList.add(new StringTextComponent(TextFormatting.GRAY + "Position 2: " + TextFormatting.AQUA + (location2 != null ? location2.toString() : "Not set")));
    }

    /**
     * Handles setting the positions.
     */
    @Override
    public ActionResultType onItemUse (ItemUseContext context) {

        World world = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();

        //Checks if the Player exists.
        if (player != null) {

            //If the Player is not crouching, set the first position.
            if (!player.isCrouching()) {

                location1 = new Location(world, pos);
                if (!world.isRemote) getMessage(player).printMessage(TextFormatting.GREEN, "First position set to coords: " + location1.x + ", " + location1.y + ", " + location1.z);
            }

            //If the Player is crouching, set the second position.
            else {

                location2 = new Location(world, pos);
                if (!world.isRemote) getMessage(player).printMessage(TextFormatting.GREEN, "Second position set to coords: " + location2.x + ", " + location2.y + ", " + location2.z);
            }

            SoundHelper.playClick(world, player);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.FAIL;
    }

    public static UnitChatMessage getMessage (PlayerEntity player) {
        return new UnitChatMessage("Brush", player);
    }
}
