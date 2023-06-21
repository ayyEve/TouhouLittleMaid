package com.github.tartaricacid.touhoulittlemaid.init;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipe;
import com.github.tartaricacid.touhoulittlemaid.crafting.AltarRecipeSerializer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class InitRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TouhouLittleMaid.MOD_ID);
    public static RegistryObject<RecipeSerializer<?>> ALTAR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("altar_crafting", AltarRecipeSerializer::new);
    
    public static final DeferredRegister<RecipeType<?>> RECIPES = DeferredRegister.create(ForgeRegistries.Keys.RECIPE_TYPES, TouhouLittleMaid.MOD_ID);
    public static RegistryObject<RecipeType<AltarRecipe>> ALTAR_CRAFTING = RECIPES.register("altar_crafting", () -> new RecipeType<AltarRecipe>() {
        @Override
        public String toString() {
            return TouhouLittleMaid.MOD_ID + ":altar_crafting";
        }
    });
}
