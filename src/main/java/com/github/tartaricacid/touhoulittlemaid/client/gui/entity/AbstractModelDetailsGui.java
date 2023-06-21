package com.github.tartaricacid.touhoulittlemaid.client.gui.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.model.DebugFloorModel;
import com.github.tartaricacid.touhoulittlemaid.client.resource.pojo.IModelInfo;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.github.tartaricacid.touhoulittlemaid.util.Rectangle;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;

public abstract class AbstractModelDetailsGui<T extends LivingEntity, E extends IModelInfo> extends Screen {
    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/skin_detail.png");
    private static final ResourceLocation FLOOR_TEXTURE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/entity/debug_floor.png");

    private static final int LEFT_MOUSE_BUTTON = 0;
    private static final int RIGHT_MOUSE_BUTTON = 1;

    private static final float SCALE_MAX = 360f;
    private static final float SCALE_MIN = 18f;
    private static final float PITCH_MAX = 90f;
    private static final float PITCH_MIN = -90f;

    private static Rectangle BACKGROUND_SIZE;
    private static Rectangle BOTTOM_STATUS_BAR_SIZE;
    private static Rectangle SIDE_MENU_SIZE;
    private static Rectangle TOP_STATUS_BAR_SIZE;

    protected final DebugFloorModel floorModel;

    protected T sourceEntity;
    protected volatile T guiEntity;
    protected E modelInfo;

    private float posX = 0;
    private float posY = 25;
    private float scale = 80;
    private float yaw = 145;
    private float pitch = -25;
    private boolean showFloor = true;

    public AbstractModelDetailsGui(T sourceEntity, @Nullable T guiEntity, E modelInfo) {
        super(Component.translatable("gui.touhou_little_maid.custom_model_details_gui.title"));
        this.sourceEntity = sourceEntity;
        this.guiEntity = guiEntity;
        this.modelInfo = modelInfo;
        this.floorModel = new DebugFloorModel(Minecraft.getInstance().getEntityModels().bakeLayer(DebugFloorModel.LAYER));
    }

    /**
     * Click return button action
     */
    abstract protected void applyReturnButtonLogic();

    /**
     * Init side button
     */
    abstract protected void initSideButton();

    /**
     * Render extra entity in main window
     *
     * @param manager  EntityRenderDispatcher
     * @param matrix   PoseStack
     * @param bufferIn MultiBufferSource
     */
    abstract protected void renderExtraEntity(EntityRenderDispatcher manager, PoseStack matrix, MultiBufferSource.BufferSource bufferIn);

    @Override
    protected void init() {
        this.clearWidgets();

        BACKGROUND_SIZE = new Rectangle(0, 0, width, height);
        BOTTOM_STATUS_BAR_SIZE = new Rectangle(0, height - 16, width, height);
        SIDE_MENU_SIZE = new Rectangle(0, 0, 132, height);
        TOP_STATUS_BAR_SIZE = new Rectangle(0, 0, width, 15);

        ImageButton closeButton = new ImageButton(width - 15, 0, 15, 15,
                0, 24, 15, BUTTON_TEXTURE, b -> Minecraft.getInstance().setScreen(null));
        ImageButton floorButton = new ImageButton(width - 30, 0, 15, 15,
                30, 24, 15, BUTTON_TEXTURE, b -> showFloor = !showFloor);
        ImageButton returnButton = new ImageButton(width - 45, 0, 15, 15,
                15, 24, 15, BUTTON_TEXTURE, b -> applyReturnButtonLogic());
        addRenderableWidget(closeButton);
        addRenderableWidget(floorButton);
        addRenderableWidget(returnButton);

        this.initSideButton();
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        if (minecraft == null) {
            return;
        }
        this.renderViewBg(stack);
        this.renderEntity((width + 132) / 2, height / 2);
        this.renderViewCrosshair();
        this.renderBottomStatueBar(stack);
        this.fillGradient(stack, SIDE_MENU_SIZE, 0xfe21252b);
        this.fillGradient(stack, TOP_STATUS_BAR_SIZE, 0xfe282c34);
        drawString(stack, font, getTitle(), 6, 4, 0xffaaaaaa);
        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void renderViewBg(PoseStack stack) {
        this.fillGradient(stack, BACKGROUND_SIZE, 0xfe17191d, -999);
        stack.pushPose();
        stack.translate(0, 0, -900);
        drawString(stack, font, Component.translatable("gui.touhou_little_maid.skin_details.left_mouse"), (int) SIDE_MENU_SIZE.w + 4, (int) TOP_STATUS_BAR_SIZE.h + 4, 0xffaaaaaa);
        drawString(stack, font, Component.translatable("gui.touhou_little_maid.skin_details.right_mouse"), (int) SIDE_MENU_SIZE.w + 4, (int) TOP_STATUS_BAR_SIZE.h + 14, 0xffaaaaaa);
        drawString(stack, font, Component.translatable("gui.touhou_little_maid.skin_details.mouse_wheel"), (int) SIDE_MENU_SIZE.w + 4, (int) TOP_STATUS_BAR_SIZE.h + 24, 0xffaaaaaa);
        stack.popPose();
    }

    private void renderBottomStatueBar(PoseStack stack) {
        this.fillGradient(stack, BOTTOM_STATUS_BAR_SIZE, 0xfe282c34);
        String name = String.format("%s %s", "\u2714", I18n.get(ParseI18n.getI18nKey(modelInfo.getName())));
        String info = String.format("%d FPS %.2f%%", Minecraft.fps, scale * 100 / 80);
        drawString(stack, font, name, 136, this.height - 12, 0xcacad4);
        drawString(stack, font, info, this.width - font.width(info) - 4, this.height - 12, 0xcacad4);
    }

    private void renderViewCrosshair() {
        if (minecraft != null) {
            Camera camera = minecraft.gameRenderer.getMainCamera();
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.translate(width - 16, height - 32, -20);
            posestack.mulPose(Vector3f.XN.rotationDegrees(camera.getXRot()));
            posestack.mulPose(Vector3f.YP.rotationDegrees(camera.getYRot()));
            posestack.scale(-1.0F, -1.0F, -1.0F);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.renderCrosshair(10);
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        boolean isInWidthRange = 132 < mouseX && mouseX < width - 1;
        boolean isInHeightRange = 15 < mouseY && mouseY < height - 16;
        boolean isInRange = isInWidthRange && isInHeightRange;
        if (minecraft == null || !isInRange) {
            return false;
        }
        if (button == LEFT_MOUSE_BUTTON) {
            yaw += dragX;
            changePitchValue((float) dragY);
        }
        if (button == RIGHT_MOUSE_BUTTON) {
            posX += dragX;
            posY += dragY;
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean isInWidthRange = 132 < mouseX && mouseX < width - 1;
        boolean isInHeightRange = 15 < mouseY && mouseY < height - 16;
        boolean isInRange = isInWidthRange && isInHeightRange;
        if (minecraft == null || !isInRange) {
            return false;
        }
        if (delta != 0) {
            changeScaleValue((float) delta * 0.07f);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private void changePitchValue(float amount) {
        if (pitch - amount > PITCH_MAX) {
            pitch = 90;
        } else if (pitch - amount < PITCH_MIN) {
            pitch = -90;
        } else {
            pitch = pitch - amount;
        }
    }

    private void changeScaleValue(float amount) {
        float tmp = scale + amount * scale;
        scale = Mth.clamp(tmp, SCALE_MIN, SCALE_MAX);
    }

    // TODO: 2022/5/30 修缮未完成部分
    private void renderEntity(int middleWidth, int middleHeight) {
//        PoseStack poseStack = new PoseStack();
//        poseStack.pushPose();
//        poseStack.translate(posX + middleWidth, posY + middleHeight, -500);
//        poseStack.scale(1.0F, 1.0F, -1.0F);
//        poseStack.mulPose(Vector3f.XP.rotationDegrees(-pitch));
//        poseStack.mulPose(Vector3f.YP.rotationDegrees(-yaw));
//        poseStack.mulPose(Vector3f.ZP.rotationDegrees(-180.0F));
//        poseStack.scale(scale, scale, scale);
//        Lighting.setupForEntityInInventory();
//        EntityRenderDispatcher manager = Minecraft.getInstance().getEntityRenderDispatcher();
//        manager.overrideCameraOrientation(Vector3f.XP.rotationDegrees(pitch));
//        manager.setRenderShadow(false);
//        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
//        RenderSystem.runAsFancy(() -> {
//            manager.render(guiEntity, 0, -0.5, 0, 0, 1, poseStack, buffer, 0xf000f0);
//            if (showFloor) {
//                this.floorModel.renderToBuffer(poseStack, buffer.getBuffer(this.floorModel.renderType(FLOOR_TEXTURE)), 0xf000f0, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
//            }
//            this.renderExtraEntity(manager, poseStack, buffer);
//        });
//        buffer.endBatch();
//        manager.setRenderShadow(true);
//        poseStack.popPose();
//        Lighting.setupFor3DItems();
    }

    private void fillGradient(PoseStack poseStack, Rectangle vec4d, int color) {
        fillGradient(poseStack, (int) vec4d.x, (int) vec4d.y, (int) vec4d.w, (int) vec4d.h, color, color);
    }

    private void fillGradient(PoseStack poseStack, Rectangle vec4d, int color, int zLevel) {
        int blitOffset = getBlitOffset();
        setBlitOffset(zLevel);
        fillGradient(poseStack, vec4d, color);
        setBlitOffset(blitOffset);
    }
}
