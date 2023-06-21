package com.github.tartaricacid.touhoulittlemaid.loot;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.command.arguments.HandleTypeArgument;
import com.github.tartaricacid.touhoulittlemaid.init.registry.MobSpawnInfoRegistry;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
// import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class AdditionLootModifier extends LootModifier {
    public static final DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_SERIALIZER = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, TouhouLittleMaid.MOD_ID);

    public static final RegistryObject<Codec<AdditionLootModifier>> CODEC = LOOT_SERIALIZER.register("addition", () -> 
        RecordCodecBuilder.create(inst -> codecStart(inst).and(
            inst.group(
                Codec.STRING.fieldOf("parameter_set_name").forGetter(m -> m.parameterSet.toString()),
                Codec.STRING.fieldOf("addition_loot_table").forGetter(m -> m.additionLootTable.toString())
            )).apply(inst, AdditionLootModifier::fromCodec)
        ));




    private final ResourceLocation parameterSet;
    private final ResourceLocation additionLootTable;

    public AdditionLootModifier(LootItemCondition[] conditionsIn, ResourceLocation parameterSet, ResourceLocation additionLootTable) {
        super(conditionsIn);
        this.parameterSet = parameterSet;
        this.additionLootTable = additionLootTable;
    }

    public static AdditionLootModifier fromCodec(LootItemCondition[] conditionsIn, String parameterSet, String additionLootTable) {
        return new AdditionLootModifier(conditionsIn, new ResourceLocation(parameterSet), new ResourceLocation(additionLootTable));
    }

    @Nonnull
    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ResourceLocation currentLootTable = context.getQueriedLootTableId();
        if (!currentLootTable.equals(additionLootTable) && parameterSetEquals(context)) {
            LootTable additionTable = context.getLootTable(additionLootTable);
            generatedLoot.addAll(additionTable.getRandomItems(context));
        }
        return generatedLoot;
    }

    private boolean parameterSetEquals(LootContext context) {
        ResourceLocation currentLootTable = context.getQueriedLootTableId();
        LootTable lootTable = context.getLootTable(currentLootTable);
        return Objects.equals(lootTable.getParamSet(), LootContextParamSets.get(parameterSet));
    }

    // public static class Serializer extends GlobalLootModifierSerializer<AdditionLootModifier> {
    //     @Override
    //     public AdditionLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
    //         String parameterSet = GsonHelper.getAsString(object, "parameter_set_name");
    //         String additionLootTable = GsonHelper.getAsString(object, "addition_loot_table");
    //         return new AdditionLootModifier(conditions, new ResourceLocation(parameterSet), new ResourceLocation(additionLootTable));
    //     }

    //     @Override
    //     public JsonObject write(AdditionLootModifier instance) {
    //         JsonObject object = makeConditions(instance.conditions);
    //         object.addProperty("parameter_set_name", instance.parameterSet.toString());
    //         object.addProperty("addition_loot_table", instance.additionLootTable.toString());
    //         return object;
    //     }
    // }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
