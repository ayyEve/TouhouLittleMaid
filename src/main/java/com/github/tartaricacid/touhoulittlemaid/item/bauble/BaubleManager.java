package com.github.tartaricacid.touhoulittlemaid.item.bauble;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.bauble.IMaidBauble;
import com.github.tartaricacid.touhoulittlemaid.command.RootCommand;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.Map;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
@Mod.EventBusSubscriber
public final class BaubleManager {
    private static Map<Item, IMaidBauble> BAUBLES;

    private BaubleManager() {
        BAUBLES = Maps.newHashMap();
    }

    public static void init() {
        BaubleManager manager = new BaubleManager();
        manager.bind(InitItems.DROWN_PROTECT_BAUBLE, new DrownProtectBauble());
        manager.bind(InitItems.EXPLOSION_PROTECT_BAUBLE, new ExplosionProtectBauble());
        manager.bind(InitItems.ULTRAMARINE_ORB_ELIXIR, new ExtraLifeBauble());
        manager.bind(InitItems.FALL_PROTECT_BAUBLE, new FallProtectBauble());
        manager.bind(InitItems.FIRE_PROTECT_BAUBLE, new FireProtectBauble());
        manager.bind(InitItems.ITEM_MAGNET_BAUBLE, new ItemMagnetBauble());
        manager.bind(InitItems.MAGIC_PROTECT_BAUBLE, new MagicProtectBauble());
        manager.bind(InitItems.NIMBLE_FABRIC, new NimbleFabricBauble());
        manager.bind(InitItems.PROJECTILE_PROTECT_BAUBLE, new ProjectileProtectBauble());
        manager.bind(InitItems.MUTE_BAUBLE, new MuteBauble());
        manager.bind(Items.TOTEM_OF_UNDYING, new UndyingTotemBauble());
        manager.bind(InitItems.WIRELESS_IO, new WirelessIOBauble());
        for (ILittleMaid littleMaid : TouhouLittleMaid.EXTENSIONS) {
            littleMaid.bindMaidBauble(manager);
        }
        BAUBLES = ImmutableMap.copyOf(BAUBLES);
    }

    @Nullable
    public static IMaidBauble getBauble(Item item) {
        return BAUBLES.get(item);
    }

    @Nullable
    public static IMaidBauble getBauble(ItemStack stack) {
        return getBauble(stack.getItem());
    }

    public void bind(RegistryObject<Item> item, IMaidBauble bauble) {
        BAUBLES.put(item.get(), bauble);
    }

    public void bind(Item item, IMaidBauble bauble) {
        BAUBLES.put(item, bauble);
    }


    
    @SubscribeEvent
    public static void initBaubles(RegisterCommandsEvent event) {
        TouhouLittleMaid.LOGGER.info("init baubles");
        init();
    }
}
