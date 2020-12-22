package com.tm.calemiutils;

import com.tm.calemiutils.command.CUCommandBase;
import com.tm.calemiutils.command.DyeColorArgument;
import com.tm.calemiutils.config.CUConfig;
import com.tm.calemiutils.config.MarketItemsFile;
import com.tm.calemiutils.gui.*;
import com.tm.calemiutils.init.*;
import com.tm.calemiutils.render.RenderBookStand;
import com.tm.calemiutils.render.RenderItemStand;
import com.tm.calemiutils.render.RenderTradingPost;
import com.tm.calemiutils.world.WorldGen;
import com.tm.calemiutils.event.*;
import com.tm.calemiutils.packet.*;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import top.theillusivec4.curios.api.SlotTypeMessage;

@Mod(CUReference.MOD_ID)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class CalemiUtils {

    public static final ResourceLocation EMPTY_WALLET_SLOT = new ResourceLocation(CUReference.MOD_ID, "gui/empty_wallet_slot");
    public static boolean curiosLoaded = false;

    public static final ItemGroup TAB = new CUTab();
    public static CalemiUtils instance;
    public static SimpleChannel network;
    public static IEventBus MOD_EVENT_BUS;

    public CalemiUtils () {

        curiosLoaded = ModList.get().isLoaded("curios");

        MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_EVENT_BUS.addListener(this::setup);
        MOD_EVENT_BUS.addListener(this::doClientStuff);

        InitSounds.SOUNDS.register(MOD_EVENT_BUS);
        InitTileEntityTypes.TILE_ENTITY_TYPES.register(MOD_EVENT_BUS);
        InitContainerTypes.CONTAINER_TYPES.register(MOD_EVENT_BUS);
        InitEnchantments.ENCHANTMENTS.register(MOD_EVENT_BUS);

        InitItems.init();
        MarketItemsFile.init();

        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CUConfig.spec, CUReference.CONFIG_DIR + "/CalemiUtilsCommon.toml");
    }

    private void setup (final FMLCommonSetupEvent event) {

        WorldGen.initOres();

        int id = 0;
        network = NetworkRegistry.newSimpleChannel(new ResourceLocation(CUReference.MOD_ID, CUReference.MOD_ID), () -> "1.0", s -> true, s -> true);
        network.registerMessage(++id, PacketEnableTileEntity.class, PacketEnableTileEntity::toBytes, PacketEnableTileEntity::new, PacketEnableTileEntity::handle);
        network.registerMessage(++id, PacketPencilSetColor.class, PacketPencilSetColor::toBytes, PacketPencilSetColor::new, PacketPencilSetColor::handle);
        network.registerMessage(++id, PacketLinkBook.class, PacketLinkBook::toBytes, PacketLinkBook::new, PacketLinkBook::handle);
        network.registerMessage(++id, PacketItemStand.class, PacketItemStand::toBytes, PacketItemStand::new, PacketItemStand::handle);
        network.registerMessage(++id, PacketWallet.class, PacketWallet::toBytes, PacketWallet::new, PacketWallet::handle);
        network.registerMessage(++id, PacketOpenWallet.class, PacketOpenWallet::toBytes, PacketOpenWallet::new, PacketOpenWallet::handle);
        network.registerMessage(++id, PacketBank.class, PacketBank::toBytes, PacketBank::new, PacketBank::handle);
        network.registerMessage(++id, PacketTradingPost.class, PacketTradingPost::toBytes, PacketTradingPost::new, PacketTradingPost::handle);
        network.registerMessage(++id, PacketMarketOptions.class, PacketMarketOptions::toBytes, PacketMarketOptions::new, PacketMarketOptions::handle);
        network.registerMessage(++id, PacketMarketTrade.class, PacketMarketTrade::toBytes, PacketMarketTrade::new, PacketMarketTrade::handle);

        MinecraftForge.EVENT_BUS.register(new WrenchEvent());
        MinecraftForge.EVENT_BUS.register(new SecurityEvent());
        MinecraftForge.EVENT_BUS.register(new MobBeaconEvent());
        MinecraftForge.EVENT_BUS.register(new AddTradesEvent());

        ArgumentTypes.register("cu:color", DyeColorArgument.class, new ArgumentSerializer<>(DyeColorArgument::color));
    }

    private void doClientStuff (final FMLClientSetupEvent event) {

        MinecraftForge.EVENT_BUS.register(new WrenchLoreEvent());
        MinecraftForge.EVENT_BUS.register(new SledgehammerChargeOverlayEvent());
        MinecraftForge.EVENT_BUS.register(new TradingPostOverlayEvent());
        MinecraftForge.EVENT_BUS.register(new WalletOverlayEvent());
        MinecraftForge.EVENT_BUS.register(new WalletKeyEvent());
        MinecraftForge.EVENT_BUS.register(new CoinPickupSoundEvent());

        RenderTypeLookup.setRenderLayer(InitItems.BLUEPRINT.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(InitItems.IRON_SCAFFOLD.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(InitItems.BOOK_STAND.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(InitItems.ITEM_STAND.get(), RenderType.getCutout());
        RenderTypeLookup.setRenderLayer(InitItems.TRADING_POST.get(), RenderType.getCutout());

        ScreenManager.registerFactory(InitContainerTypes.WALLET.get(), ScreenWallet::new);
        ScreenManager.registerFactory(InitContainerTypes.TORCH_PLACER.get(), ScreenTorchPlacer::new);
        ScreenManager.registerFactory(InitContainerTypes.BOOK_STAND.get(), ScreenOneSlot::new);
        ScreenManager.registerFactory(InitContainerTypes.ITEM_STAND.get(), ScreenOneSlot::new);
        ScreenManager.registerFactory(InitContainerTypes.BANK.get(), ScreenBank::new);
        ScreenManager.registerFactory(InitContainerTypes.TRADING_POST.get(), ScreenTradingPost::new);

        ClientRegistry.bindTileEntityRenderer(InitTileEntityTypes.BOOK_STAND.get(), RenderBookStand::new);
        ClientRegistry.bindTileEntityRenderer(InitTileEntityTypes.ITEM_STAND.get(), RenderItemStand::new);
        ClientRegistry.bindTileEntityRenderer(InitTileEntityTypes.TRADING_POST.get(), RenderTradingPost::new);

        InitKeyBindings.init();
    }

    @SubscribeEvent
    public void onServerStarting (FMLServerStartingEvent event) {
        CUCommandBase.register(event.getServer().getFunctionManager().getCommandDispatcher());
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onTextureStitch(TextureStitchEvent.Pre event) {

        if (event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE)) {
            event.addSprite(EMPTY_WALLET_SLOT);
        }
    }

    @SubscribeEvent
    public static void onModRegister(InterModEnqueueEvent event) {

        if (curiosLoaded) {
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("wallet").size(1).icon(EMPTY_WALLET_SLOT).build());
            InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> new SlotTypeMessage.Builder("belt").size(1).build());
        }
    }
}