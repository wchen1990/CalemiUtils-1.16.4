package com.tm.calemiutils.init;

import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.block.base.BlockItemBase;
import com.tm.calemiutils.item.*;
import com.tm.calemiutils.item.base.ItemBase;
import com.tm.calemiutils.block.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitItems {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CUReference.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CUReference.MOD_ID);

    public static void init () {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    //----- BLOCKS ------\\

    public static final RegistryObject<Block> RARITANIUM_ORE = BLOCKS.register("raritanium_ore", BlockRaritaniumOre::new);
    public static final RegistryObject<Item> RARITANIUM_ORE_ITEM = ITEMS.register("raritanium_ore", () -> new BlockItemBase(RARITANIUM_ORE.get()));

    public static final RegistryObject<Block> COIN_STACK_PENNY = BLOCKS.register("coin_stack_penny", BlockCoinStack::new);
    public static final RegistryObject<Block> COIN_STACK_NICKEL = BLOCKS.register("coin_stack_nickel", BlockCoinStack::new);
    public static final RegistryObject<Block> COIN_STACK_QUARTER = BLOCKS.register("coin_stack_quarter", BlockCoinStack::new);
    public static final RegistryObject<Block> COIN_STACK_DOLLAR = BLOCKS.register("coin_stack_dollar", BlockCoinStack::new);

    public static final RegistryObject<Block> BLUEPRINT = BLOCKS.register("blueprint", BlockBlueprint::new);
    public static final RegistryObject<Item> BLUEPRINT_ITEM = ITEMS.register("blueprint", BlockBlueprintItem::new);

    public static final RegistryObject<Block> IRON_SCAFFOLD = BLOCKS.register("iron_scaffold", BlockIronScaffold::new);
    public static final RegistryObject<Item> IRON_SCAFFOLD_ITEM = ITEMS.register("iron_scaffold", () -> new BlockItemBase(IRON_SCAFFOLD.get()));

    public static final RegistryObject<Block> ITEM_STAND = BLOCKS.register("item_stand", BlockItemStand::new);
    public static final RegistryObject<Item> ITEM_STAND_ITEM = ITEMS.register("item_stand", () -> new BlockItemBase(ITEM_STAND.get()));

    public static final RegistryObject<Block> BOOK_STAND = BLOCKS.register("book_stand", BlockBookStand::new);
    public static final RegistryObject<Item> BOOK_STAND_ITEM = ITEMS.register("book_stand", () -> new BlockItemBase(BOOK_STAND.get()));

    public static final RegistryObject<Block> MOB_BEACON = BLOCKS.register("mob_beacon", BlockMobBeacon::new);
    public static final RegistryObject<Item> MOB_BEACON_ITEM = ITEMS.register("mob_beacon", () -> new BlockItemBase(MOB_BEACON.get()));

    public static final RegistryObject<Block> TORCH_PLACER = BLOCKS.register("torch_placer", BlockTorchPlacer::new);
    public static final RegistryObject<Item> TORCH_PLACER_ITEM = ITEMS.register("torch_placer", () -> new BlockItemBase(TORCH_PLACER.get()));

    public static final RegistryObject<Block> BLUEPRINT_FILLER = BLOCKS.register("blueprint_filler", BlockBlueprintFiller::new);
    public static final RegistryObject<Item> BLUEPRINT_FILLER_ITEM = ITEMS.register("blueprint_filler", () -> new BlockItemBase(BLUEPRINT_FILLER.get()));

    public static final RegistryObject<Block> BANK = BLOCKS.register("bank", BlockBank::new);
    public static final RegistryObject<Item> BANK_ITEM = ITEMS.register("bank", () -> new BlockItemBase(BANK.get()));

    public static final RegistryObject<Block> NETWORK_CABLE_OPAQUE = BLOCKS.register("network_cable_opaque", BlockNetworkCableOpaque::new);
    public static final RegistryObject<Item> NETWORK_CABLE_OPAQUE_ITEM = ITEMS.register("network_cable_opaque", () -> new BlockItemBase(NETWORK_CABLE_OPAQUE.get()));

    public static final RegistryObject<Block> NETWORK_CABLE = BLOCKS.register("network_cable", BlockNetworkCable::new);
    public static final RegistryObject<Item> NETWORK_CABLE_ITEM = ITEMS.register("network_cable", () -> new BlockItemBase(NETWORK_CABLE.get()));

    public static final RegistryObject<Block> NETWORK_GATE = BLOCKS.register("network_gate", BlockNetworkGate::new);
    public static final RegistryObject<Item> NETWORK_GATE_ITEM = ITEMS.register("network_gate", () -> new BlockItemBase(NETWORK_GATE.get()));

    public static final RegistryObject<Block> TRADING_POST = BLOCKS.register("trading_post", BlockTradingPost::new);
    public static final RegistryObject<Item> TRADING_POST_ITEM = ITEMS.register("trading_post", () -> new BlockItemBase(TRADING_POST.get()));

    public static final RegistryObject<Block> MARKET = BLOCKS.register("market", BlockMarket::new);
    public static final RegistryObject<Item> MARKET_ITEM = ITEMS.register("market", () -> new BlockItemBase(MARKET.get()));

    //----- ITEMS ------\\

    public static final RegistryObject<Item> RARITANIUM = ITEMS.register("raritanium", ItemBase::new);
    public static final RegistryObject<Item> COIN_PENNY = ITEMS.register("coin_penny", () -> new ItemCoin(1, COIN_STACK_PENNY.get()));
    public static final RegistryObject<Item> COIN_NICKEL = ITEMS.register("coin_nickel", () -> new ItemCoin(5, COIN_STACK_NICKEL.get()));
    public static final RegistryObject<Item> COIN_QUARTER = ITEMS.register("coin_quarter", () -> new ItemCoin(25, COIN_STACK_QUARTER.get()));
    public static final RegistryObject<Item> COIN_DOLLAR = ITEMS.register("coin_dollar", () -> new ItemCoin(100, COIN_STACK_DOLLAR.get()));

    public static final RegistryObject<Item> MONEY_BAG_CHEAP = ITEMS.register("money_bag_cheap", () -> new ItemMoneyBag(false));
    public static final RegistryObject<Item> MONEY_BAG_RICH = ITEMS.register("money_bag_rich", () -> new ItemMoneyBag(true));

    public static final RegistryObject<Item> GOLD_CHIP = ITEMS.register("gold_chip", ItemBase::new);
    public static final RegistryObject<Item> MOTOR = ITEMS.register("motor", ItemBase::new);
    public static final RegistryObject<Item> KNOB_WOOD = ITEMS.register("knob_wood", ItemBase::new);
    public static final RegistryObject<Item> KNOB_STONE = ITEMS.register("knob_stone", ItemBase::new);
    public static final RegistryObject<Item> KNOB_IRON = ITEMS.register("knob_iron", ItemBase::new);
    public static final RegistryObject<Item> KNOB_GOLD = ITEMS.register("knob_gold", ItemBase::new);
    public static final RegistryObject<Item> KNOB_DIAMOND = ITEMS.register("knob_diamond", ItemBase::new);
    public static final RegistryObject<Item> KNOB_NETHERITE = ITEMS.register("knob_netherite", ItemBase::new);
    public static final RegistryObject<Item> KNOB_STARLIGHT = ITEMS.register("knob_starlight", () -> new ItemBase().setEffect().setRarity(Rarity.RARE));

    public static final RegistryObject<Item> SLEDGEHAMMER_WOOD = ITEMS.register("sledgehammer_wood", () -> new ItemSledgehammer(SledgehammerTiers.WOOD));
    public static final RegistryObject<Item> SLEDGEHAMMER_STONE = ITEMS.register("sledgehammer_stone", () -> new ItemSledgehammer(SledgehammerTiers.STONE));
    public static final RegistryObject<Item> SLEDGEHAMMER_IRON = ITEMS.register("sledgehammer_iron", () -> new ItemSledgehammer(SledgehammerTiers.IRON));
    public static final RegistryObject<Item> SLEDGEHAMMER_GOLD = ITEMS.register("sledgehammer_gold", () -> new ItemSledgehammer(SledgehammerTiers.GOLD));
    public static final RegistryObject<Item> SLEDGEHAMMER_DIAMOND = ITEMS.register("sledgehammer_diamond", () -> new ItemSledgehammer(SledgehammerTiers.DIAMOND));
    public static final RegistryObject<Item> SLEDGEHAMMER_NETHERITE = ITEMS.register("sledgehammer_netherite", () -> new ItemSledgehammer(SledgehammerTiers.NETHERITE));
    public static final RegistryObject<Item> SLEDGEHAMMER_STARLIGHT = ITEMS.register("sledgehammer_starlight", () -> new ItemSledgehammer(SledgehammerTiers.STARLIGHT));

    public static final RegistryObject<Item> SECURITY_WRENCH = ITEMS.register("security_wrench", ItemSecurityWrench::new);
    public static final RegistryObject<Item> PENCIL = ITEMS.register("pencil", ItemPencil::new);
    public static final RegistryObject<Item> BRUSH = ITEMS.register("brush", ItemBrush::new);
    public static final RegistryObject<Item> ERASER = ITEMS.register("eraser", ItemEraser::new);
    public static final RegistryObject<Item> WALLET = ITEMS.register("wallet", ItemWallet::new);
    public static final RegistryObject<Item> BLENDER = ITEMS.register("blender", ItemBlender::new);
    public static final RegistryObject<Item> TORCH_BELT = ITEMS.register("torch_belt", ItemTorchBelt::new);
    public static final RegistryObject<Item> LINK_BOOK_LOCATION = ITEMS.register("link_book_location", () -> new ItemLinkBookLocation().setRarity(Rarity.RARE));

    public static final RegistryObject<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade", ItemUpgrade::new);
    public static final RegistryObject<Item> RANGE_UPGRADE = ITEMS.register("range_upgrade", ItemUpgrade::new);
}
