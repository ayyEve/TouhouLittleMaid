package com.github.tartaricacid.touhoulittlemaid.command.arguments;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HandleTypeArgument implements ArgumentType<String> {
    public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType(
            (obj) -> Component.translatable("argument.touhou_little_maid.handle_type.invalid", obj));
    private static final List<String> HANDLE_TYPES = Lists.newArrayList();

    private HandleTypeArgument() {
        HANDLE_TYPES.add("set");
        HANDLE_TYPES.add("add");
        HANDLE_TYPES.add("min");
    }

    public static HandleTypeArgument type() {
        return new HandleTypeArgument();
    }

    public static String getType(CommandContext<CommandSourceStack> context, String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String arg = reader.readUnquotedString();
        int index = HANDLE_TYPES.indexOf(arg);
        if (index >= 0) {
            return HANDLE_TYPES.get(index);
        }
        throw ERROR_INVALID_VALUE.create(arg);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return SharedSuggestionProvider.suggest(HANDLE_TYPES, builder);
    }



    
   public static class Info implements ArgumentTypeInfo<HandleTypeArgument, HandleTypeArgument.Info.Template> {
        @Override
        public void serializeToNetwork(Template pTemplate, FriendlyByteBuf pBuffer) { }

        public HandleTypeArgument.Info.Template deserializeFromNetwork(FriendlyByteBuf pBuffer) {
            return new HandleTypeArgument.Info.Template();
        }

        public void serializeToJson(HandleTypeArgument.Info.Template pTemplate, JsonObject pJson) { }

        public HandleTypeArgument.Info.Template unpack(HandleTypeArgument pArgument) {
            return new HandleTypeArgument.Info.Template();
        }

        public final class Template implements ArgumentTypeInfo.Template<HandleTypeArgument> {
            Template() { }

            public HandleTypeArgument instantiate(CommandBuildContext pContext) {
                return new HandleTypeArgument();
            }

            public ArgumentTypeInfo<HandleTypeArgument, ?> type() {
                return new Info();
            }
        }
    }

}
