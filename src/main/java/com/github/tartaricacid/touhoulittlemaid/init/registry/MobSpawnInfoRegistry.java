package com.github.tartaricacid.touhoulittlemaid.init.registry;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.config.subconfig.MiscConfig;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
// import net.minecraftforge.event.;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// import static net.minecraft.world.level.biome.Biome.BiomeCategory.*;


// @Mod.EventBusSubscriber
// public final class MobSpawnInfoRegistry {
//     @SubscribeEvent
//     public static void addMobSpawnInfo(BiomeLoadingEvent event) {
//         if (biomeIsOkay(event.getCategory())) {
//             event.getSpawns().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(InitEntities.FAIRY.get(), MiscConfig.MAID_FAIRY_SPAWN_PROBABILITY.get(), 2, 6));
//         }
//     }

//     private static boolean biomeIsOkay(Biome.BiomeCategory category) {
//         // TODO: 2021/10/17 添加配置文件，管控可生成的生物群系
//         return category != NETHER && category != THEEND && category != NONE && category != MUSHROOM;
//     }
// }

//TODO: add the biomes to the json whatever: https://forge.gemwire.uk/wiki/Biome_Modifiers
public record MobSpawnInfoRegistry(HolderSet<Biome> biomes) implements BiomeModifier {
  public static DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TouhouLittleMaid.MOD_ID);

  static RegistryObject<Codec<MobSpawnInfoRegistry>> CODEC = BIOME_MODIFIER_SERIALIZERS.register("example", () ->
    RecordCodecBuilder.create(builder -> builder.group(
        // declare fields
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(MobSpawnInfoRegistry::biomes)
      // declare constructor
      ).apply(builder, MobSpawnInfoRegistry::new)));


  public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
    // This allows modifications to the given biome via the provided Builder.
        if (phase.equals(Phase.ADD) && biomes.contains(biome)) {
            builder.getMobSpawnSettings().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(InitEntities.FAIRY.get(), MiscConfig.MAID_FAIRY_SPAWN_PROBABILITY.get(), 2, 6));
        }
  }

  public Codec<? extends BiomeModifier> codec() {
    return CODEC.get();
  }
}