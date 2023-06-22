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
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo.BiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

// TODO: 2021/10/17 添加配置文件，管控可生成的生物群系
// TODO: add the biomes to the json whatever: https://forge.gemwire.uk/wiki/Biome_Modifiers
public class MobSpawnInfoRegistry {
    public static DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, TouhouLittleMaid.MOD_ID);
    static RegistryObject<Codec<MobSpawnInfo>> CODEC = BIOME_MODIFIER_SERIALIZERS.register("biomes", () -> RecordCodecBuilder.create(builder -> builder.group(
        // declare fields
        Biome.LIST_CODEC.fieldOf("biomes").forGetter(MobSpawnInfo::biomes),
        Biome.LIST_CODEC.fieldOf("biomes2").forGetter(MobSpawnInfo::biomes2),
        Biome.LIST_CODEC.fieldOf("biomes3").forGetter(MobSpawnInfo::biomes3)
    ).apply(builder, MobSpawnInfo::new)));


    // TODO: is there a better way to do this? having 3 biome entries is dumb but it doesnt accept a list of namespace-tags
    public record MobSpawnInfo(HolderSet<Biome> biomes, HolderSet<Biome> biomes2, HolderSet<Biome> biomes3) implements BiomeModifier {
        public void modify(Holder<Biome> biome, Phase phase, BiomeInfo.Builder builder) {
            // This allows modifications to the given biome via the provided Builder.
            if (phase.equals(Phase.ADD) && !biomes.contains(biome) && !biomes2.contains(biome) && !biomes3.contains(biome)) {
                builder.getMobSpawnSettings().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(InitEntities.FAIRY.get(), MiscConfig.MAID_FAIRY_SPAWN_PROBABILITY.get(), 2, 6));
            }
        }

        public Codec<? extends BiomeModifier> codec() {
          	return CODEC.get();
        }
    }
}
