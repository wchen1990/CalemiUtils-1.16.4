package com.tm.calemiutils.item;

import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.main.CalemiUtils;
import com.tm.calemiutils.integration.curios.CuriosIntegration;
import com.tm.calemiutils.inventory.ContainerWallet;
import com.tm.calemiutils.inventory.base.ItemStackInventory;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.util.UnitChatMessage;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.LoreHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.List;

public class ItemWallet extends ItemBase {

    public ItemWallet () {
        super(new Item.Properties().group(CalemiUtils.TAB).maxStackSize(1));
    }

    @Override
    public void addInformation (ItemStack stack, @Nullable World world, List<ITextComponent> tooltipList, ITooltipFlag advanced) {
        LoreHelper.addInformationLore(tooltipList, "Used to store currency in one place.", true);
        LoreHelper.addControlsLore(tooltipList, "Open Inventory", LoreHelper.Type.USE, true);
        LoreHelper.addBlankLine(tooltipList);
        LoreHelper.addCurrencyLore(tooltipList, getBalance(stack), CUConfig.wallet.walletCurrencyCapacity.get());
    }

    public static UnitChatMessage getMessage (PlayerEntity player) {
        return new UnitChatMessage("Wallet", player);
    }

    /**
     * Gets the balance of the given Wallet Stack.
     */
    public static int getBalance (ItemStack stack) {
        return ItemHelper.getNBT(stack).getInt("balance");
    }

    /**
     * Deposits currency into the given Wallet Stack.
     */
    public static void depositCurrency (ItemStack stack, int depsositAmount) {
        ItemHelper.getNBT(stack).putInt("balance", getBalance(stack) + depsositAmount);
    }

    /**
     * Deposits currency into the given Wallet Stack.
     */
    public static void withdrawCurrency (ItemStack stack, int withdrawAmount) {
        ItemHelper.getNBT(stack).putInt("balance", getBalance(stack) - withdrawAmount);
    }

    /**
     * Handles opening the GUI.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick (World world, PlayerEntity player, Hand hand) {

        ItemStack stack = player.getHeldItem(hand);

        //Checks if on server & if the Player is a Server Player.
        if (!world.isRemote && player instanceof ServerPlayerEntity) {

            //Checks if Wallet is not disabled by config.
            if (CUConfig.wallet.walletCurrencyCapacity.get() > 0) {
                openGui((ServerPlayerEntity) player, stack, player.inventory.currentItem);
                return new ActionResult<>(ActionResultType.SUCCESS, stack);
            }
        }

        return new ActionResult<>(ActionResultType.FAIL, stack);
    }

    /**
     * opens the GUI.
     */
    private void openGui (ServerPlayerEntity player, ItemStack stack, int selectedSlot) {

        NetworkHooks.openGui(player, new SimpleNamedContainerProvider(
            (id, playerInventory, openPlayer) -> new ContainerWallet(id, playerInventory, new ItemStackInventory(stack, 1), player.inventory.currentItem), stack.getDisplayName()),
            (buffer) -> buffer.writeVarInt(selectedSlot));
    }

    /**
     * Adds behaviours to the Wallet as a curios Item.
     */
    @Override
    public ICapabilityProvider initCapabilities(final ItemStack stack, CompoundNBT unused) {

        if (CalemiUtils.curiosLoaded) {
            return CuriosIntegration.walletCapability();
        }

        return super.initCapabilities(stack, unused);
    }
}
