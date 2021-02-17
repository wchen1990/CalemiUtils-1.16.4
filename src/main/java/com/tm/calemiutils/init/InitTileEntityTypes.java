package com.tm.calemiutils.init;

import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.tileentity.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CUReference.MOD_ID);

    public static final RegistryObject<TileEntityType<TileEntityTorchPlacer>> TORCH_PLACER = TILE_ENTITY_TYPES.register("torch_placer", () -> TileEntityType.Builder.create(TileEntityTorchPlacer::new, InitItems.TORCH_PLACER.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityBlueprintFiller>> BLUEPRINT_FILLER = TILE_ENTITY_TYPES.register("blueprint_filler", () -> TileEntityType.Builder.create(TileEntityBlueprintFiller::new, InitItems.BLUEPRINT_FILLER.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityMobBeacon>> MOB_BEACON = TILE_ENTITY_TYPES.register("mob_beacon", () -> TileEntityType.Builder.create(TileEntityMobBeacon::new, InitItems.MOB_BEACON.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityBookStand>> BOOK_STAND = TILE_ENTITY_TYPES.register("book_stand", () -> TileEntityType.Builder.create(TileEntityBookStand::new, InitItems.BOOK_STAND.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityItemStand>> ITEM_STAND = TILE_ENTITY_TYPES.register("item_stand", () -> TileEntityType.Builder.create(TileEntityItemStand::new, InitItems.ITEM_STAND.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityBank>> BANK = TILE_ENTITY_TYPES.register("bank", () -> TileEntityType.Builder.create(TileEntityBank::new, InitItems.BANK.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityNetworkCable>> NETWORK_CABLE = TILE_ENTITY_TYPES.register("network_cable", () -> TileEntityType.Builder.create(TileEntityNetworkCable::new, InitItems.NETWORK_CABLE.get(), InitItems.NETWORK_CABLE_OPAQUE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityNetworkGate>> NETWORK_GATE = TILE_ENTITY_TYPES.register("network_gate", () -> TileEntityType.Builder.create(TileEntityNetworkGate::new, InitItems.NETWORK_GATE.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityTradingPost>> TRADING_POST = TILE_ENTITY_TYPES.register("trading_post", () -> TileEntityType.Builder.create(TileEntityTradingPost::new, InitItems.TRADING_POST.get()).build(null));
    public static final RegistryObject<TileEntityType<TileEntityMarket>> MARKET = TILE_ENTITY_TYPES.register("market", () -> TileEntityType.Builder.create(TileEntityMarket::new, InitItems.MARKET.get()).build(null));
}
