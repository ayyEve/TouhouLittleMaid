package com.github.tartaricacid.touhoulittlemaid.client.init;

import com.github.tartaricacid.touhoulittlemaid.client.renderer.item.PerspectiveBakedModel;
import com.github.tartaricacid.touhoulittlemaid.init.InitItems;
import com.google.common.collect.Lists;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent.BakingCompleted;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class InitSpecialItemRender {
    private static final List<Pair<ModelResourceLocation, ModelResourceLocation>> PERSPECTIVE_MODEL_LIST = Lists.newArrayList();

    @SubscribeEvent
    public static void registerModels(RegisterAdditional event) {
        addInHandModel(InitItems.HAKUREI_GOHEI);
        addInHandModel(InitItems.EXTINGUISHER);
        addInHandModel(InitItems.CAMERA);
        addInHandModel(InitItems.MAID_BEACON);

        PERSPECTIVE_MODEL_LIST.forEach((pair) -> event.register(pair.getRight()));
    }

    public static void addInHandModel(RegistryObject<Item> item) {
        ResourceLocation res = item.getId();
        if (res != null) {
            ModelResourceLocation rawName = new ModelResourceLocation(res.toString() + "#inventory");
            ModelResourceLocation inHandName = new ModelResourceLocation(res.toString() + "_in_hand#inventory");
            PERSPECTIVE_MODEL_LIST.add(Pair.of(rawName, inHandName));
        }
    }
    
    @SubscribeEvent
    public static void onBakedModel(BakingCompleted event) {
        Map<ResourceLocation, BakedModel> registry = event.getModels();
        for (Pair<ModelResourceLocation, ModelResourceLocation> pair : PERSPECTIVE_MODEL_LIST) {
            PerspectiveBakedModel model = new PerspectiveBakedModel(registry.get(pair.getLeft()), registry.get(pair.getRight()));
            registry.put(pair.getLeft(), model);
        }
    }
}
