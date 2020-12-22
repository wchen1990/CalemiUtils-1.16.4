package calemiutils.config;

import calemiutils.CUReference;
import calemiutils.util.helper.ItemHelper;
import calemiutils.util.helper.LogHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarketItemsFile {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    public static Map<String, MarketItem> marketItemsBuyList;
    public static Map<String, MarketItem> marketItemsSellList;

    public static void init() {
        marketItemsBuyList = createFile("MarketItemsBuyList", getBuyDefaults());
        marketItemsSellList = createFile("MarketItemsSellList", getSellDefaults());
    }

    private static Map<String, MarketItem> createFile(String fileName, Map<String, MarketItem> defaultItemsList) {

        String path = CUReference.CONFIG_DIR;
        File directory = new File(path);
        File jsonConfig = new File(path, fileName + ".json");

        if (!directory.exists()) {
            directory.mkdir();
        }

        try {

            // Create the config if it doesn't already exist.
            if (!jsonConfig.exists() && jsonConfig.createNewFile()) {

                // Get a default map of blocks. You could just use a blank map, however.
                // Convert the map to JSON format. There is a built in (de)serializer for it already.
                String json = gson.toJson(defaultItemsList, new TypeToken<Map<String, MarketItem>>(){}.getType());
                FileWriter writer = new FileWriter(jsonConfig);
                // Write to the file you passed
                writer.write(json);
                // Always close when done.
                writer.close();
            }

            // If the file exists (or we just made one exist), convert it from JSON format to a populated Map object
            return gson.fromJson(new FileReader(jsonConfig), new TypeToken<Map<String, MarketItem>>(){}.getType());
        }

        catch (IOException e) {
            // Print an error if something fails.
            LogHelper.log("Error creating default configuration.");
        }

        return null;
    }

    private static Map<String, MarketItem> getBuyDefaults() {
        Map<String, MarketItem> ret = new HashMap<>();

        int id = 0;
        addDefault(ret, id, "TestBuyItem", "minecraft:cobblestone", "", 64, 64);

        return ret;
    }

    private static Map<String, MarketItem> getSellDefaults() {
        Map<String, MarketItem> ret = new HashMap<>();

        int id = 0;
        addDefault(ret, id, "TestSellItem", "minecraft:cobblestone", "", 64, 1);

        return ret;
    }

    private static void addDefault(Map<String, MarketItem> ret, int index, String stackName, String stackObj, String stackNBT, int amount, int value) {
        ret.put(stackName, new MarketItem(index, stackObj, stackNBT, amount, value));
    }

    private static MarketItem getMarketItemById (List<MarketItemsFile.MarketItem> list, int index) {

        if (index < list.size()) {

            for (MarketItem marketItem : list) {

                if (marketItem.index == index) {
                    return marketItem;
                }
            }
        }

        return null;
    }

    public static ItemStack getStackFromList(List<MarketItem> list, int index) {

        ItemStack stack = ItemStack.EMPTY;
        MarketItem marketItem = getMarketItemById(list, index);

        if (marketItem != null) {

            Item item = Registry.ITEM.getOrDefault(new ResourceLocation(marketItem.stackStr));

            if (item != Items.AIR) {

                stack = new ItemStack(item, marketItem.amount);

                if (!marketItem.stackNBT.isEmpty()) {
                    ItemHelper.attachNBTFromString(stack, marketItem.stackNBT);
                }
            }
        }

        return stack;
    }

    public static class MarketItem {

        public final int index;
        public final String stackStr;
        public final String stackNBT;
        public final int amount;
        public final int value;

        MarketItem (int index, String stackObj, String stackNBT, int amount, int value) {
            this.index = index;
            this.stackStr = stackObj;
            this.stackNBT = stackNBT;
            this.amount = amount;
            this.value = value;
        }

        public static MarketItem readFromNBT(CompoundNBT nbt) {
            String stackNBT = nbt.getString("stackNBT");
            return new MarketItem(nbt.getInt("index"), nbt.getString("stackStr"), stackNBT, nbt.getInt("amount"), nbt.getInt("value"));
        }

        public CompoundNBT writeToNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putInt("index", index);
            nbt.putString("stackStr", stackStr);
            nbt.putString("stackNBT", stackNBT);
            nbt.putInt("amount", amount);
            nbt.putInt("value", value);
            return nbt;
        }

        @Override
        public String toString() {
            return "MarketItem[index=" + index + ", stackStr=" + stackStr + ", stackNBT=" + stackNBT + ", amount=" + amount + ", value=" + value + "]";
        }
    }
}