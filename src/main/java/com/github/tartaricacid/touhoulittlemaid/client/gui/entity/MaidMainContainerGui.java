package com.github.tartaricacid.touhoulittlemaid.client.gui.entity;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.api.task.IMaidTask;
import com.github.tartaricacid.touhoulittlemaid.client.download.InfoGetManager;
import com.github.tartaricacid.touhoulittlemaid.client.download.pojo.DownloadInfo;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.ScheduleButton;
import com.github.tartaricacid.touhoulittlemaid.client.gui.widget.button.TaskButton;
import com.github.tartaricacid.touhoulittlemaid.client.resource.CustomPackLoader;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;
import com.github.tartaricacid.touhoulittlemaid.inventory.container.MaidMainContainer;
import com.github.tartaricacid.touhoulittlemaid.item.BackpackLevel;
import com.github.tartaricacid.touhoulittlemaid.network.NetworkHandler;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidConfigMessage;
import com.github.tartaricacid.touhoulittlemaid.network.message.MaidTaskMessage;
import com.github.tartaricacid.touhoulittlemaid.util.ParseI18n;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import static com.github.tartaricacid.touhoulittlemaid.util.GuiTools.NO_ACTION;

public class MaidMainContainerGui extends AbstractContainerScreen<MaidMainContainer> {
    private static final ResourceLocation BG = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_main.png");
    private static final ResourceLocation SIDE = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_side.png");
    private static final ResourceLocation BACKPACK = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_backpack.png");
    private static final ResourceLocation BUTTON = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_button.png");
    private static final ResourceLocation TASK = new ResourceLocation(TouhouLittleMaid.MOD_ID, "textures/gui/maid_gui_task.png");
    private static final int TASK_COUNT_PER_PAGE = 12;
    private static int TASK_PAGE = 0;
    private final EntityMaid maid;
    private StateSwitchingButton home;
    private StateSwitchingButton pick;
    private StateSwitchingButton ride;
    private ImageButton info;
    private ImageButton skin;
    private ImageButton pageDown;
    private ImageButton pageUp;
    private ImageButton pageClose;
    private ImageButton taskSwitch;
    private ImageButton modelDownload;
    private ImageButton soundDownload;
    private ScheduleButton scheduleButton;
    private boolean taskListOpen;

    public MaidMainContainerGui(MaidMainContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.maid = menu.getMaid();
    }

    @Override
    protected void init() {
        super.init();
        this.clearWidgets();
        this.addHomeButton();
        this.addPickButton();
        this.addRideButton();
        this.addDownloadButton();
        this.addStateButton();
        this.addTaskSwitchButton();
        this.addTaskControlButton();
        this.addTaskListButton();
        this.addScheduleButton();
    }

    @Override
    @SuppressWarnings("all")
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.drawCurrentTaskText(poseStack);
        this.renderTooltip(poseStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int x, int y) {
        poseStack.translate(0, 0, -100);
        renderBackground(poseStack);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BG);
        blit(poseStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        this.drawMaidCharacter(x, y);
        this.drawBaseInfoGui(poseStack);
        this.drawBackpackGui(poseStack);
        this.drawTaskListBg(poseStack);
    }

    @Override
    protected void renderTooltip(PoseStack poseStack, int x, int y) {
        super.renderTooltip(poseStack, x, y);
        renderTransTooltip(home, poseStack, x, y, "gui.touhou_little_maid.button.home");
        renderTransTooltip(pick, poseStack, x, y, "gui.touhou_little_maid.button.pickup");
        renderTransTooltip(ride, poseStack, x, y, "gui.touhou_little_maid.button.maid_riding_set");
        renderTransTooltip(modelDownload, poseStack, x, y, "gui.touhou_little_maid.button.model_download");
        renderTransTooltip(soundDownload, poseStack, x, y, "gui.touhou_little_maid.button.sound_download");
        renderTransTooltip(skin, poseStack, x, y, "gui.touhou_little_maid.button.skin");
        renderTransTooltip(pageUp, poseStack, x, y, "gui.touhou_little_maid.task.next_page");
        renderTransTooltip(pageDown, poseStack, x, y, "gui.touhou_little_maid.task.previous_page");
        renderTransTooltip(pageClose, poseStack, x, y, "gui.touhou_little_maid.task.close");
        renderTransTooltip(taskSwitch, poseStack, x, y, "gui.touhou_little_maid.task.switch");
        renderMaidInfo(poseStack, x, y);
        renderScheduleInfo(poseStack, x, y);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int x, int y) {
        this.drawTaskPageCount(poseStack);
    }

    private void addStateButton() {
        skin = new ImageButton(leftPos + 62, topPos + 14, 9, 9, 72, 43, 10, BUTTON, (b) -> getMinecraft().setScreen(new MaidModelGui(maid)));
        info = new ImageButton(leftPos + 8, topPos + 14, 9, 9, 72, 65, 10, BUTTON, NO_ACTION);
        this.addRenderableWidget(skin);
        this.addRenderableWidget(info);
    }

    private void addTaskControlButton() {
        pageDown = new ImageButton(leftPos - 72, topPos + 9, 16, 13, 93, 0, 14, TASK, (b) -> {
            List<IMaidTask> tasks = TaskManager.getTaskIndex();
            if (TASK_PAGE * TASK_COUNT_PER_PAGE + TASK_COUNT_PER_PAGE < tasks.size()) {
                TASK_PAGE++;
                init();
            }
        });
        pageUp = new ImageButton(leftPos - 89, topPos + 9, 16, 13, 110, 0, 14, TASK, (b) -> {
            if (TASK_PAGE > 0) {
                TASK_PAGE--;
                init();
            }
        });
        pageClose = new ImageButton(leftPos - 19, topPos + 9, 13, 13, 127, 0, 14, TASK, (b) -> {
            taskListOpen = false;
            init();
        });
        this.addRenderableWidget(pageUp);
        this.addRenderableWidget(pageDown);
        this.addRenderableWidget(pageClose);
        pageUp.visible = taskListOpen;
        pageDown.visible = taskListOpen;
        pageClose.visible = taskListOpen;
    }

    private void addTaskListButton() {
        List<IMaidTask> tasks = TaskManager.getTaskIndex();
        if (TASK_PAGE * TASK_COUNT_PER_PAGE >= tasks.size()) {
            TASK_PAGE = 0;
        }
        for (int count = 0; count < TASK_COUNT_PER_PAGE; count++) {
            int index = TASK_PAGE * TASK_COUNT_PER_PAGE + count;
            if (index < tasks.size()) {
                drawPerTaskButton(tasks, count, index);
            }
        }
    }

    private void drawPerTaskButton(List<IMaidTask> tasks, int count, int index) {
        final IMaidTask maidTask = tasks.get(index);
        TaskButton button = new TaskButton(maidTask, leftPos - 89, topPos + 23 + 19 * count,
                83, 19, 93, 28, 20, TASK, 256, 256,
                (b) -> NetworkHandler.CHANNEL.sendToServer(new MaidTaskMessage(maid.getId(), maidTask.getUid())),
                (b, m, x, y) -> renderComponentTooltip(m, getTaskTooltips(maidTask), x, y), Component.empty());
        this.addRenderableWidget(button);
        button.visible = taskListOpen;
    }

    private List<Component> getTaskTooltips(IMaidTask maidTask) {
        List<Component> desc = ParseI18n.keysToTrans(maidTask.getDescription(maid), ChatFormatting.GRAY);
        if (!desc.isEmpty()) {
            desc.add(0, Component.translatable("task.touhou_little_maid.desc.title").withStyle(ChatFormatting.GOLD));
        }
        List<Pair<String, Predicate<EntityMaid>>> conditions = maidTask.getConditionDescription(maid);
        if (!conditions.isEmpty()) {
            desc.add(Component.literal("\u0020"));
            desc.add(Component.translatable("task.touhou_little_maid.desc.condition").withStyle(ChatFormatting.GOLD));
        }
        MutableComponent prefix = Component.literal("-\u0020");
        for (Pair<String, Predicate<EntityMaid>> line : conditions) {
            String key = String.format("task.%s.%s.condition.%s", maidTask.getUid().getNamespace(), maidTask.getUid().getPath(), line.getFirst());
            MutableComponent condition = Component.translatable(key);
            if (line.getSecond().test(maid)) {
                condition.withStyle(ChatFormatting.GREEN);
            } else {
                condition.withStyle(ChatFormatting.RED);
            }
            desc.add(prefix.append(condition));
        }
        return desc;
    }

    private void addScheduleButton() {
        scheduleButton = new ScheduleButton(leftPos + 9, topPos + 177, this);
        this.addRenderableWidget(scheduleButton);
    }

    private void addTaskSwitchButton() {
        taskSwitch = new ImageButton(leftPos + 4, topPos + 149, 71, 21, 0, 42, 22, BUTTON, (b) -> {
            taskListOpen = !taskListOpen;
            init();
        });
        this.addRenderableWidget(taskSwitch);
    }

    private void addRideButton() {
        ride = new StateSwitchingButton(leftPos + 51, topPos + 196, 20, 20, maid.isRideable()) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                this.isStateTriggered = !this.isStateTriggered;
                NetworkHandler.CHANNEL.sendToServer(new MaidConfigMessage(maid.getId(), maid.isHomeModeEnable(), maid.isPickup(), isStateTriggered, maid.getSchedule()));
            }
        };
        ride.initTextureValues(84, 0, 21, 21, BUTTON);
        this.addRenderableWidget(ride);
    }

    private void addPickButton() {
        pick = new StateSwitchingButton(leftPos + 30, topPos + 196, 20, 20, maid.isPickup()) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                this.isStateTriggered = !this.isStateTriggered;
                NetworkHandler.CHANNEL.sendToServer(new MaidConfigMessage(maid.getId(), maid.isHomeModeEnable(), isStateTriggered, maid.isRideable(), maid.getSchedule()));
            }
        };
        pick.initTextureValues(42, 0, 21, 21, BUTTON);
        this.addRenderableWidget(pick);
    }

    private void addHomeButton() {
        home = new StateSwitchingButton(leftPos + 9, topPos + 196, 20, 20, maid.isHomeModeEnable()) {
            @Override
            public void onClick(double mouseX, double mouseY) {
                this.isStateTriggered = !this.isStateTriggered;
                NetworkHandler.CHANNEL.sendToServer(new MaidConfigMessage(maid.getId(), isStateTriggered, maid.isPickup(), maid.isRideable(), maid.getSchedule()));
            }
        };
        home.initTextureValues(0, 0, 21, 21, BUTTON);
        this.addRenderableWidget(home);
    }

    private void addDownloadButton() {
        modelDownload = new ImageButton(leftPos + 20, topPos + 217, 20, 20, 0, 86, 20, BUTTON,
                (b) -> {
                    List<DownloadInfo> downloadInfoList;
                    int page = ModelDownloadGui.getCurrentPage();
                    if (page == 0) {
                        downloadInfoList = InfoGetManager.DOWNLOAD_INFO_LIST_ALL;
                    } else {
                        DownloadInfo.TypeEnum typeEnum = DownloadInfo.TypeEnum.getTypeByIndex(page - 1);
                        downloadInfoList = InfoGetManager.getTypedDownloadInfoList(typeEnum);
                    }
                    Minecraft.getInstance().setScreen(new ModelDownloadGui(downloadInfoList));
                });
        soundDownload = new ImageButton(leftPos + 41, topPos + 217, 20, 20, 21, 86, 20, BUTTON,
                (b) -> {
                });
        this.addRenderableWidget(modelDownload);
        this.addRenderableWidget(soundDownload);
    }

    private void drawTaskPageCount(PoseStack poseStack) {
        if (taskListOpen) {
            String text = String.format("%d/%d", TASK_PAGE + 1, TaskManager.getTaskIndex().size() / TASK_COUNT_PER_PAGE + 1);
            font.draw(poseStack, text, -48, 12, 0x333333);
        }
    }

    private void drawCurrentTaskText(PoseStack poseStack) {
        IMaidTask task = maid.getTask();
        itemRenderer.renderGuiItem(task.getIcon(), leftPos + 6, topPos + 151);
        List<FormattedCharSequence> splitTexts = font.split(task.getName(), 42);
        if (!splitTexts.isEmpty()) {
            font.draw(poseStack, splitTexts.get(0), leftPos + 28, topPos + 155, 0x333333);
        }
    }

    private void renderMaidInfo(PoseStack poseStack, int mouseX, int mouseY) {
        if (info.isHoveredOrFocused()) {
            List<Component> list = Lists.newArrayList();
            String prefix = "§a█\u0020";

            MutableComponent title = Component.literal("")
                    .append(Component.translatable("tooltips.touhou_little_maid.info.title")
                            .withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE))
                    .append(Component.literal("§r\u0020"));
            if (maid.isStruckByLightning()) {
                title.append(Component.literal("❀").withStyle(ChatFormatting.DARK_RED));
            }
            if (maid.isInvulnerable()) {
                title.append(Component.literal("✟").withStyle(ChatFormatting.BLUE));
            }
            list.add(title);

            if (maid.getOwner() != null) {
                list.add(Component.literal(prefix).withStyle(ChatFormatting.WHITE)
                        .append(Component.translatable("tooltips.touhou_little_maid.info.owner")
                                .append(":\u0020").withStyle(ChatFormatting.AQUA))
                        .append(maid.getOwner().getDisplayName()));
            }
            CustomPackLoader.MAID_MODELS.getInfo(maid.getModelId()).ifPresent((info) -> list.add(Component.literal(prefix)
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.translatable("tooltips.touhou_little_maid.info.model_name")
                            .append(":\u0020").withStyle(ChatFormatting.AQUA))
                    .append(ParseI18n.parse(info.getName()))));
            list.add(Component.literal(prefix).withStyle(ChatFormatting.WHITE)
                    .append(Component.translatable("tooltips.touhou_little_maid.info.experience")
                            .append(":\u0020").withStyle(ChatFormatting.AQUA))
                    .append(String.valueOf(maid.getExperience())));
            list.add(Component.literal(prefix).withStyle(ChatFormatting.WHITE)
                    .append(Component.translatable("tooltips.touhou_little_maid.info.favorability")
                            .append(":\u0020").withStyle(ChatFormatting.AQUA))
                    .append(String.valueOf(maid.getFavorability())));

            renderComponentTooltip(poseStack, list, mouseX, mouseY);
        }
    }

    private void renderScheduleInfo(PoseStack poseStack, int mouseX, int mouseY) {
        if (scheduleButton.isHoveredOrFocused()) {
            renderComponentTooltip(poseStack, scheduleButton.getTooltips(), mouseX, mouseY);
        }
    }

    private void drawMaidCharacter(int x, int y) {
        double scale = getMinecraft().getWindow().getGuiScale();
        RenderSystem.enableScissor((int) ((leftPos + 6) * scale), (int) ((topPos + 107 + 42) * scale),
                (int) (67 * scale), (int) (95 * scale));
        InventoryScreen.renderEntityInInventory(leftPos + 40, topPos + 100, 40, (leftPos + 40) - x, (topPos + 70 - 20) - y, maid);
        RenderSystem.disableScissor();
    }

    private void drawTaskListBg(PoseStack poseStack) {
        if (taskListOpen) {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, TASK);
            blit(poseStack, leftPos - 93, topPos + 5, 0, 0, 92, 251);
        }
    }

    @SuppressWarnings("all")
    private void drawBaseInfoGui(PoseStack poseStack) {
        poseStack.translate(0, 0, 200);
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SIDE);
            blit(poseStack, leftPos + 5, topPos + 113, 0, 0, 9, 9);
            blit(poseStack, leftPos + 27, topPos + 113, 0, 9, 47, 9);
            double hp = maid.getHealth() / maid.getMaxHealth();
            blit(poseStack, leftPos + 29, topPos + 115, 2, 18, (int) (43 * hp), 5);
            getMinecraft().font.draw(poseStack, String.format("%.0f", maid.getHealth()), leftPos + 15, topPos + 114, ChatFormatting.DARK_GRAY.getColor());
        }
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SIDE);
            blit(poseStack, leftPos + 5, topPos + 124, 9, 0, 9, 9);
            blit(poseStack, leftPos + 27, topPos + 124, 0, 9, 47, 9);
            double armor = maid.getAttributeValue(Attributes.ARMOR) / 20;
            blit(poseStack, leftPos + 29, topPos + 126, 2, 23, (int) (43 * armor), 5);
            getMinecraft().font.draw(poseStack, String.format("%d", maid.getArmorValue()), leftPos + 15, topPos + 125, ChatFormatting.DARK_GRAY.getColor());
        }
        {
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, SIDE);
            blit(poseStack, leftPos + 5, topPos + 135, 18, 0, 9, 9);
            blit(poseStack, leftPos + 27, topPos + 135, 0, 9, 47, 9);
            double hunger = maid.getHunger() / 20.0;
            blit(poseStack, leftPos + 29, topPos + 137, 2, 28, (int) (43 * hunger), 5);
            getMinecraft().font.draw(poseStack, String.format("%d", maid.getHunger()), leftPos + 15, topPos + 136, ChatFormatting.DARK_GRAY.getColor());
        }

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SIDE);
        blit(poseStack, leftPos + 94, topPos + 7, 107, 0, 149, 21);
        blit(poseStack, leftPos + 94, topPos + 5, 107, 21, 24, 26);
        blit(poseStack, leftPos + 98, topPos + 12, 107, 47, 16, 16);
        blit(poseStack, leftPos + 6, topPos + 168, 0, 47, 67, 25);
    }

    private void drawBackpackGui(PoseStack poseStack) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, BACKPACK);
        blit(poseStack, leftPos + 85, topPos + 36, 0, 0, 165, 122);

        int level = maid.getBackpackLevel();
        if (level < BackpackLevel.SMALL) {
            fill(poseStack, leftPos + 142, topPos + 58, leftPos + 250, topPos + 76, 0xaa222222);
            blit(poseStack, leftPos + 190, topPos + 62, 165, 0, 11, 11);
        }
        if (level < BackpackLevel.MIDDLE) {
            fill(poseStack, leftPos + 142, topPos + 81, leftPos + 250, topPos + 117, 0xaa222222);
            blit(poseStack, leftPos + 190, topPos + 92, 165, 0, 11, 11);
        }
        if (level < BackpackLevel.BIG) {
            fill(poseStack, leftPos + 142, topPos + 122, leftPos + 250, topPos + 158, 0xaa222222);
            blit(poseStack, leftPos + 190, topPos + 133, 165, 0, 11, 11);
        }
    }

    @Override
    public int getGuiLeft() {
        if (taskListOpen) {
            return leftPos - 93;
        }
        return leftPos;
    }

    @Override
    public int getXSize() {
        if (taskListOpen) {
            return imageWidth + 93;
        }
        return imageWidth;
    }

    public EntityMaid getMaid() {
        return maid;
    }

    private void renderTransTooltip(ImageButton button, PoseStack poseStack, int x, int y, String key) {
        if (button.isHoveredOrFocused()) {
            renderComponentTooltip(poseStack, Collections.singletonList(Component.translatable(key)), x, y);
        }
    }

    private void renderTransTooltip(StateSwitchingButton button, PoseStack poseStack, int x, int y, String key) {
        if (button.isHoveredOrFocused()) {
            renderComponentTooltip(poseStack, Lists.newArrayList(
                    Component.translatable(key + "." + button.isStateTriggered()),
                    Component.translatable(key + ".desc")
            ), x, y);
        }
    }
}
