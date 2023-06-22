package com.github.tartaricacid.touhoulittlemaid.client.renderer.item;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;

import java.util.Collections;
import java.util.List;

public class PerspectiveBakedModel implements BakedModel {
    private final BakedModel bakedModel2d;
    private final BakedModel bakedModel3d;

    public PerspectiveBakedModel(BakedModel bakedModel2d, BakedModel bakedModel3d) {
        this.bakedModel2d = bakedModel2d;
        this.bakedModel3d = bakedModel3d;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
        return Collections.emptyList();
    }

    @Override
    public boolean usesBlockLight() {
        return bakedModel2d.usesBlockLight();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.bakedModel2d.getParticleIcon();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return bakedModel2d.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return bakedModel2d.isGui3d();
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Nonnull
    @Override
    public ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public BakedModel applyTransform(ItemTransforms.TransformType type, com.mojang.blaze3d.vertex.PoseStack mat, boolean applyLeftHandTransform) {
        if (type == ItemTransforms.TransformType.GUI || type == ItemTransforms.TransformType.FIXED) {
            return bakedModel2d.applyTransform(type, mat, applyLeftHandTransform);
        } else {
            return bakedModel3d.applyTransform(type, mat, applyLeftHandTransform);
        }
    }

}
