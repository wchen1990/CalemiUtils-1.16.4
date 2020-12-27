package com.tm.calemiutils.item;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.event.WrenchEvent;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.tileentity.base.TileEntityBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.helper.LoreHelper;
import com.tm.calemiutils.util.helper.SecurityHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemSecurityWrench extends ItemBase {

    public ItemSecurityWrench () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Used to access secured blocks!", true);
        LoreHelper.addControlsLore(tooltipList, "Interact with secured blocks", LoreHelper.Type.USE, true);
        LoreHelper.addControlsLore(tooltipList, "Pick up secured blocks", LoreHelper.Type.SNEAK_USE);
    }

    /**
     * Handles calling the Wrench event.
     */
    @Override
    public ActionResultType onItemUseFirst (ItemStack stack, ItemUseContext context) {

        PlayerEntity player = context.getPlayer();

        Location location = new Location(context.getWorld(), context.getPos());

        //Checks if the Player exists.
        if (player != null && player.isCrouching()) {

            //Checks if the Tile Entity exists and if its a the mod's Tile Entity
            if (location.getTileEntity() != null && location.getTileEntity() instanceof TileEntityBase) {

                //Checks if the Tile Entity has security
                if (location.getTileEntity() instanceof ISecurity) {

                    ISecurity security = (ISecurity) location.getTileEntity();

                    //Checks if the Player is the owner of the secured block. Bypassed by creative mode or config option.
                    if (security.getSecurityProfile().isOwner(player.getName().getString()) || player.isCreative() || !CUConfig.misc.useSecurity.get()) {

                        WrenchEvent.onBlockWrenched(context.getWorld(), location);
                        return ActionResultType.SUCCESS;
                    }

                    else SecurityHelper.printErrorMessage(location, player);
                }

                //If the Tile Entity has no security, call the event.
                else WrenchEvent.onBlockWrenched(context.getWorld(), location);
            }
        }

        return super.onItemUseFirst(stack, context);
    }
}
