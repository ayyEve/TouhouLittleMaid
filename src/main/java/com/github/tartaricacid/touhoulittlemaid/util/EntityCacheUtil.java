package com.github.tartaricacid.touhoulittlemaid.util;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = TouhouLittleMaid.MOD_ID, value = Dist.CLIENT)
public final class EntityCacheUtil {
    /**
     * 实体缓存，在客户端会大量运用实体渲染，这个缓存可以减少重复创建实体带来的性能问题
     */
    public static final Cache<EntityType<?>, Entity> ENTITY_CACHE = CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.MINUTES).build();
    private static ResourceKey<Level> dimAt;

    public static void clearMaidDataResidue(EntityMaid maid, boolean clearEquipmentData) {
        maid.hurtDuration = 0;
        maid.hurtTime = 0;
        maid.deathTime = 0;
        maid.setInSittingPose(false);
        maid.setBackpackLevel(BackpackLevel.EMPTY);
        maid.setCustomName(Component.empty());
        if (clearEquipmentData) {
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                maid.setItemSlot(slot, ItemStack.EMPTY);
            }
        }
    }

    @SubscribeEvent
    public static void onChangeDim(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide && event.getEntity() == Minecraft.getInstance().player) {
            ResourceKey<Level> dim = event.getEntity().level.dimension();
            if (!dim.equals(dimAt)) {
                dimAt = dim;
                EntityCacheUtil.ENTITY_CACHE.invalidateAll();
            }
        }
    }
}
