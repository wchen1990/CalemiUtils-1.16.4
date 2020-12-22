package calemiutils.tileentity;

import calemiutils.config.MarketItemsFile;
import calemiutils.init.InitTileEntityTypes;
import calemiutils.security.ISecurity;
import calemiutils.security.SecurityProfile;
import calemiutils.tileentity.base.ICurrencyNetworkUnit;
import calemiutils.tileentity.base.TileEntityBase;
import calemiutils.util.Location;
import calemiutils.util.helper.CurrencyHelper;
import calemiutils.util.helper.InventoryHelper;
import calemiutils.util.helper.NetworkHelper;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TileEntityMarket extends TileEntityBase implements ISecurity, ICurrencyNetworkUnit {

    private final SecurityProfile profile = new SecurityProfile();
    private Location bankLocation;
    public boolean dirtyFlag;

    public final List<MarketItemsFile.MarketItem> marketItemsToBuy = new ArrayList<>();
    public final List<MarketItemsFile.MarketItem> marketItemsToSell = new ArrayList<>();
    public boolean buyMode = true;
    public boolean automationMode = false;
    public int selectedIndex;
    public int purchaseAmount = 1;

    public TileEntityMarket() {
        super(InitTileEntityTypes.MARKET.get());
        dirtyFlag = true;
    }

    @Override
    public void tick() {

        if (world != null && !world.isRemote && dirtyFlag) {
            registerMarketItems();
        }

        MarketItemsFile.MarketItem selectedMarketItem = getSelectedMarketItem();
        TileEntityBank bank = getBank();
        IInventory inv = getConnectedInventory();

        if (world.getGameTime() % 10 == 0) {

            if (automationMode && selectedMarketItem != null && bank != null && inv != null) {

                int totalAmount = selectedMarketItem.amount * purchaseAmount;
                int totalValue = selectedMarketItem.value * purchaseAmount;

                ItemStack selectedStack = getSelectedItemStack();
                selectedStack.setCount(totalAmount);

                if (buyMode) {

                    if (bank.getStoredCurrency() >= totalValue) {

                        if (InventoryHelper.canInsertStack(selectedStack, inv)) {

                            InventoryHelper.insertOverflowingStack(inv, selectedStack);
                            CurrencyHelper.withdrawFromBank(bank, totalValue);
                        }
                    }
                }

                else {

                    if (CurrencyHelper.canDepositToBank(bank, totalValue)) {

                        if (InventoryHelper.countItems(inv, true, selectedStack) >= totalAmount) {

                            InventoryHelper.consumeStack(inv, totalAmount, true, selectedStack);
                            CurrencyHelper.depositToBank(bank, totalValue);
                        }
                    }
                }
            }
        }
    }

    private void registerMarketItems() {

        marketItemsToBuy.clear();
        marketItemsToSell.clear();
        if (MarketItemsFile.marketItemsBuyList != null) marketItemsToBuy.addAll(MarketItemsFile.marketItemsBuyList.values());
        if (MarketItemsFile.marketItemsSellList != null) marketItemsToSell.addAll(MarketItemsFile.marketItemsSellList.values());
        marketItemsToBuy.sort(Comparator.comparingInt(o -> o.index));
        marketItemsToSell.sort(Comparator.comparingInt(o -> o.index));
        dirtyFlag = false;

        markForUpdate();
    }

    public ItemStack getSelectedItemStack() {
        return MarketItemsFile.getStackFromList(getCurrentMarketItemList(), selectedIndex);
    }

    public MarketItemsFile.MarketItem getSelectedMarketItem() {
        clampSelectedIndex();

        if (getCurrentMarketItemList().size() > 0) {
            return getCurrentMarketItemList().get(selectedIndex);
        }

        return null;
    }

    private void clampSelectedIndex() {

        if (selectedIndex < 0 || selectedIndex >= getCurrentMarketItemList().size()) {
            selectedIndex = 0;
        }
    }

    private List<MarketItemsFile.MarketItem> getCurrentMarketItemList() {
        return buyMode ? marketItemsToBuy : marketItemsToSell;
    }

    private IInventory getConnectedInventory() {

        Location location = getLocation().translate(Direction.UP, 1);

        if (location.getTileEntity() != null && !(location.getTileEntity() instanceof TileEntityBank) && location.getTileEntity() instanceof IInventory) {
            return (IInventory) location.getTileEntity();
        }

        return null;
    }

    @Override
    public Location getBankLocation() {
        return bankLocation;
    }

    @Override
    public void setBankLocation(Location location) {
        this.bankLocation = location;
    }

    public TileEntityBank getBank() {
        TileEntityBank bank = NetworkHelper.getConnectedBank(getLocation(), bankLocation);
        if (bank == null) bankLocation = null;
        return bank;
    }

    @Override
    public SecurityProfile getSecurityProfile() {
        return profile;
    }

    @Override
    public Direction[] getConnectedDirections () {
        return Direction.values();
    }

    @Override
    public void read(BlockState state, CompoundNBT nbtRoot) {

        buyMode = nbtRoot.getBoolean("buyMode");
        automationMode = nbtRoot.getBoolean("autoMode");

        selectedIndex = nbtRoot.getInt("selectedOffer");
        purchaseAmount = nbtRoot.getInt("purchaseAmount");

        marketItemsToBuy.clear();
        marketItemsToSell.clear();

        if (nbtRoot.contains("MarketItemsBuy")) {

            CompoundNBT marketParent = nbtRoot.getCompound("MarketItemsBuy");

            for (int i = 0; i < marketParent.size(); i++) {
                CompoundNBT itemTag = marketParent.getCompound("BuyItem" + i);
                marketItemsToBuy.add(MarketItemsFile.MarketItem.readFromNBT(itemTag));
            }
        }

        if (nbtRoot.contains("MarketItemsSell")) {

            CompoundNBT marketParent = nbtRoot.getCompound("MarketItemsSell");

            for (int i = 0; i < marketParent.size(); i++) {
                CompoundNBT itemTag = marketParent.getCompound("SellItem" + i);
                marketItemsToSell.add(MarketItemsFile.MarketItem.readFromNBT(itemTag));
            }
        }

        super.read(state, nbtRoot);
    }

    @Override
    public CompoundNBT write(CompoundNBT nbtRoot) {

        CompoundNBT buyParent = new CompoundNBT();
        CompoundNBT sellParent = new CompoundNBT();

        for (int i = 0; i < marketItemsToBuy.size(); i++) {

            MarketItemsFile.MarketItem marketItem = marketItemsToBuy.get(i);

            CompoundNBT marketTag = marketItem.writeToNBT();

            //Write Item to parent
            buyParent.put("BuyItem" + i, marketTag);
        }

        for (int i = 0; i < marketItemsToSell.size(); i++) {

            MarketItemsFile.MarketItem marketItem = marketItemsToSell.get(i);

            CompoundNBT marketTag = marketItem.writeToNBT();

            //Write Item to parent
            sellParent.put("SellItem" + i, marketTag);
        }

        //Write parent to root
        nbtRoot.put("MarketItemsBuy", buyParent);
        nbtRoot.put("MarketItemsSell", sellParent);

        nbtRoot.putBoolean("buyMode", buyMode);
        nbtRoot.putBoolean("autoMode", automationMode);

        nbtRoot.putInt("selectedOffer", selectedIndex);
        nbtRoot.putInt("purchaseAmount", purchaseAmount);

        return super.write(nbtRoot);
    }
}