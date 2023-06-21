package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.command.RootCommand;
import com.github.tartaricacid.touhoulittlemaid.command.arguments.HandleTypeArgument;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.github.tartaricacid.touhoulittlemaid.item.ItemMaidBackpack;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


@Mod.EventBusSubscriber
public final class CommandRegistry {
    @SubscribeEvent
    public static void onServerStaring(RegisterCommandsEvent event) {
        RootCommand.register(event.getDispatcher());
    }

    public static final DeferredRegister<ArgumentTypeInfo<?,?>> ARGUMENTS = DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, TouhouLittleMaid.MOD_ID);
    public static RegistryObject<ArgumentTypeInfo<?,?>> MAID_HANDLE_TYPES = ARGUMENTS.register("handle_types", () -> ArgumentTypeInfos.registerByClass(HandleTypeArgument.class, new HandleTypeArgument.Info()));

    // public static void registerArgumentTypes() {
    //     ArgumentTypes.register("touhou_little_maid:handle_types", HandleTypeArgument.class, new EmptyArgumentSerializer<>(HandleTypeArgument::type));
    // }
}

