package com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.client.gui.entity.MaidMainContainerGui;
import com.github.tartaricacid.touhoulittlemaid.entity.ai.brain.MaidSchedule;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.init.InitEntities;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidConfigMessage;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.schedule.Activity;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class ScheduleButton extends Button {
    private static final ResourceLocation BUTTON = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_button.png");
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00");
    private final EntityMaid maid;
    private MaidSchedule mode;

    public ScheduleButton(int x, int y, MaidMainContainerGui gui) {
        super(x, y, 61, 13, Component.empty(), (b) -> {
        });
        this.maid = gui.getMaid();
        this.mode = maid.getSchedule();
    }

    @Override
    public void onPress() {
        int index = mode.ordinal() + 1;
        int length = MaidSchedule.values().length;
        this.mode = MaidSchedule.values()[index % length];
        NetworkHandler.CHANNEL.sendToServer(new MaidConfigMessage(maid.getId(), maid.isHomeModeEnable(), maid.isPickup(), maid.isRideable(), this.mode));
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BUTTON);
        RenderSystem.enableDepthTest();
        blit(poseStack, this.x, this.y, 82, 43 + 14 * mode.ordinal(), this.width, this.height, 256, 256);
    }

    public List<Component> getTooltips() {
        int time = (int) (maid.level.getDayTime() % 24000L);
        int hour = (time / 1000 + 6) % 24;
        int minute = (time % 1000) * 60 / 1000;
        Activity activity;

        List<Component> out = Lists.newArrayList();
        out.add(Component.literal(String.format("§n%s§7 %s:%s", getScheduleTransText(), DECIMAL_FORMAT.format(hour), DECIMAL_FORMAT.format(minute))));
        switch (mode) {
            case ALL:
                out.add(Component.literal(String.format("§a█ 00:00-24:00 %s", getActivityTransText(Activity.WORK))));
                break;
            case NIGHT:
                activity = InitEntities.MAID_NIGHT_SHIFT_SCHEDULES.get().getActivityAt(time);
                if (activity == Activity.WORK) {
                    out.add(Component.literal(String.format("§a█ 18:00-06:00 %s", getActivityTransText(Activity.WORK))));
                } else {
                    out.add(Component.literal(String.format("§8█ 18:00-06:00 %s", getActivityTransText(Activity.WORK))));
                }

                if (activity == Activity.REST) {
                    out.add(Component.literal(String.format("§a█ 06:00-14:00 %s", getActivityTransText(Activity.REST))));
                } else {
                    out.add(Component.literal(String.format("§8█ 06:00-14:00 %s", getActivityTransText(Activity.REST))));
                }

                if (activity == Activity.IDLE) {
                    out.add(Component.literal(String.format("§a█ 14:00-18:00 %s", getActivityTransText(Activity.IDLE))));
                } else {
                    out.add(Component.literal(String.format("§8█ 14:00-18:00 %s", getActivityTransText(Activity.IDLE))));
                }
                break;
            case DAY:
            default:
                activity = InitEntities.MAID_DAY_SHIFT_SCHEDULES.get().getActivityAt(time);
                if (activity == Activity.WORK) {
                    out.add(Component.literal(String.format("§a█ 06:00-18:00 %s", getActivityTransText(Activity.WORK))));
                } else {
                    out.add(Component.literal(String.format("§8█ 06:00-18:00 %s", getActivityTransText(Activity.WORK))));
                }

                if (activity == Activity.IDLE) {
                    out.add(Component.literal(String.format("§a█ 18:00-22:00 %s", getActivityTransText(Activity.IDLE))));
                } else {
                    out.add(Component.literal(String.format("§8█ 18:00-22:00 %s", getActivityTransText(Activity.IDLE))));
                }

                if (activity == Activity.REST) {
                    out.add(Component.literal(String.format("§a█ 22:00-06:00 %s", getActivityTransText(Activity.REST))));
                } else {
                    out.add(Component.literal(String.format("§8█ 22:00-06:00 %s", getActivityTransText(Activity.REST))));
                }
        }
        out.add(Component.translatable("tooltips.touhou_little_maid.schedule.desc"));
        return out;
    }

    public String getScheduleTransText() {
        return I18n.get("gui.touhou_little_maid.schedule." + mode.name().toLowerCase(Locale.US));
    }

    public String getActivityTransText(Activity activity) {
        return I18n.get("gui.touhou_little_maid.activity." + activity.getName());
    }
}
