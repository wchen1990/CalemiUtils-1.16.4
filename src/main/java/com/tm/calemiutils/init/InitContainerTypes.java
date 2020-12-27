package com.tm.calemiutils.init;

import com.tm.calemiutils.main.CUReference;
import com.tm.calemiutils.inventory.*;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class InitContainerTypes {

    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CUReference.MOD_ID);

    public static final RegistryObject<ContainerType<ContainerWallet>> WALLET = CONTAINER_TYPES.register("wallet", () -> IForgeContainerType.create(ContainerWallet::createClientWallet));
    public static final RegistryObject<ContainerType<ContainerTorchPlacer>> TORCH_PLACER = CONTAINER_TYPES.register("torch_placer", () -> IForgeContainerType.create(ContainerTorchPlacer::new));
    public static final RegistryObject<ContainerType<ContainerBookStand>> BOOK_STAND = CONTAINER_TYPES.register("book_stand", () -> IForgeContainerType.create(ContainerBookStand::new));
    public static final RegistryObject<ContainerType<ContainerItemStand>> ITEM_STAND = CONTAINER_TYPES.register("item_stand", () -> IForgeContainerType.create(ContainerItemStand::new));
    public static final RegistryObject<ContainerType<ContainerBank>> BANK = CONTAINER_TYPES.register("bank", () -> IForgeContainerType.create(ContainerBank::new));
    public static final RegistryObject<ContainerType<ContainerTradingPost>> TRADING_POST = CONTAINER_TYPES.register("trading_post", () -> IForgeContainerType.create(ContainerTradingPost::new));
}
