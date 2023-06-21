package com.github.tartaricacid.touhoulittlemaid.data;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenEvent {
    @SubscribeEvent
    public static void dataGen(GatherDataEvent event) {
        //TODO: should this be true?
        event.getGenerator().addProvider(false, new MaidBlockStateProvider(event.getGenerator(), TouhouLittleMaid.MOD_ID, event.getExistingFileHelper()));
        event.getGenerator().addProvider(false, new AltarRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(false, new MaidRecipeProvider(event.getGenerator()));
        event.getGenerator().addProvider(false, new LootModifierProvider(event.getGenerator()));
    }
}
