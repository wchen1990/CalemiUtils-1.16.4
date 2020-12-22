package com.tm.calemiutils.tileentity;

import com.tm.calemiutils.gui.ScreenTradingPost;
import com.tm.calemiutils.init.InitTileEntityTypes;
import com.tm.calemiutils.inventory.ContainerTradingPost;
import com.tm.calemiutils.security.ISecurity;
import com.tm.calemiutils.security.SecurityProfile;
import com.tm.calemiutils.tileentity.base.ICurrencyNetworkUnit;
import com.tm.calemiutils.tileentity.base.ITileEntityGuiHandler;
import com.tm.calemiutils.tileentity.base.TileEntityInventoryBase;
import com.tm.calemiutils.util.Location;
import com.tm.calemiutils.util.UnitChatMessage;
import com.tm.calemiutils.util.helper.ItemHelper;
import com.tm.calemiutils.util.helper.NetworkHelper;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Objects;

public class TileEntityTradingPost extends TileEntityInventoryBase implements ITileEntityGuiHandler, ICurrencyNetworkUnit, ISecurity {

    private final SecurityProfile profile = new SecurityProfile();
    private Location bankLocation;

    private ItemStack stackForSale = ItemStack.EMPTY;
    public int amountForSale;
    public int salePrice;
    public boolean buyMode = false;
    public boolean adminMode = false;
    public boolean hasValidTradeOffer;

    public int broadcastDelay;

    public TileEntityTradingPost () {
        super(InitTileEntityTypes.TRADING_POST.get());
        amountForSale = 1;
        salePrice = 0;
        hasValidTradeOffer = false;
    }

    @Override
    public void tick () {
        super.tick();

        hasValidTradeOffer = getStackForSale() != null && !getStackForSale().isEmpty() && amountForSale >= 1;

        if (world != null && !world.isRemote) {

            if (broadcastDelay > 0) {

                if (world.getGameTime() % 20 == 0) {
                    broadcastDelay--;
                }
            }
        }
    }

    public ItemStack getStackForSale () {
        return stackForSale;
    }

    public void setStackForSale (ItemStack stack) {
        stackForSale = stack;
    }

    public int getStock () {

        if (getStackForSale() != null) {

            int count = 0;

            for (int i = 0; i < getSizeInventory(); i++) {

                if (getInventory().getStackInSlot(i).isItemEqual(getStackForSale())) {

                    if (getStackForSale().hasTag()) {

                        if (getInventory().getStackInSlot(i).hasTag() && Objects.requireNonNull(getInventory().getStackInSlot(i).getTag()).equals(getStackForSale().getTag())) {
                            count += getInventory().getStackInSlot(i).getCount();
                        }
                    }

                    else count += getInventory().getStackInSlot(i).getCount();
                }
            }

            return count;
        }

        return 0;
    }

    public UnitChatMessage getUnitName (PlayerEntity player) {

        if (adminMode) {
            return new UnitChatMessage("Admin Post", player);
        }

        return new UnitChatMessage(getSecurityProfile().getOwnerName() + "'s Trading Post", player);
    }

    @Override
    public Location getBankLocation () {
        return bankLocation;
    }

    @Override
    public void setBankLocation (Location location) {
        bankLocation = location;
    }

    public TileEntityBank getBank () {
        TileEntityBank bank = NetworkHelper.getConnectedBank(getLocation(), bankLocation);
        if (bank == null) bankLocation = null;
        return bank;
    }

    @Override
    public SecurityProfile getSecurityProfile () {
        return profile;
    }

    @Override
    public Direction[] getConnectedDirections () {
        return new Direction[] {Direction.DOWN};
    }

    @Override
    public ITextComponent getDefaultName () {
        return new StringTextComponent("Trading Post");
    }

    @Override
    public int getSizeInventory () {
        return 27;
    }

    @Override
    public Container getTileContainer (int windowId, PlayerInventory playerInv) {
        return new ContainerTradingPost(windowId, playerInv, this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ContainerScreen getTileGuiContainer (int windowId, PlayerInventory playerInv) {
        return new ScreenTradingPost(getTileContainer(windowId, playerInv), playerInv, new StringTextComponent("Trading Post"));
    }

    @Override
    public void read (BlockState state, CompoundNBT nbt) {

        super.read(state, nbt);

        amountForSale = nbt.getInt("amount");
        salePrice = nbt.getInt("price");

        stackForSale = ItemHelper.getStackFromString(nbt.getString("stack"));

        if (!nbt.getString("nbt").isEmpty()) {
            ItemHelper.attachNBTFromString(stackForSale, nbt.getString("nbt"));
        }

        adminMode = nbt.getBoolean("adminMode");
        buyMode = nbt.getBoolean("buyMode");

        broadcastDelay = nbt.getInt("broadcastDelay");
    }

    @Override
    public CompoundNBT write (CompoundNBT nbt) {

        super.write(nbt);

        nbt.putInt("amount", amountForSale);
        nbt.putInt("price", salePrice);

        nbt.putString("stack", ItemHelper.getStringFromStack(stackForSale));

        String nbtString = "";

        if (stackForSale.hasTag()) {
            nbtString = stackForSale.getTag().toString();
        }

        nbt.putString("nbt", nbtString);

        nbt.putBoolean("adminMode", adminMode);
        nbt.putBoolean("buyMode", buyMode);

        nbt.putInt("broadcastDelay", broadcastDelay);

        return nbt;
    }
}
