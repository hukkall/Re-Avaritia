package committee.nova.mods.avaritia.client.screen;

import committee.nova.mods.avaritia.api.client.screen.BaseContainerScreen;
import committee.nova.mods.avaritia.api.client.screen.component.OperationButton;
import committee.nova.mods.avaritia.api.client.screen.component.OperationButtonType;
import committee.nova.mods.avaritia.api.client.util.GuiUtils;
import committee.nova.mods.avaritia.common.menu.InfinityClockBlockMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.Map;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/4/6 17:46
 * @Description:
 */
public class InfinityClockBlockScreen extends BaseContainerScreen<InfinityClockBlockMenu> {
    private int bgX;
    private int bgY;
    /**
     * 操作按钮
     */
    private final Map<Integer, OperationButton> OP_BUTTONS = new HashMap<>();
    public InfinityClockBlockScreen(InfinityClockBlockMenu container, Inventory inventory, Component title) {
        super(container, inventory, title);
    }

    @Override
    protected void subInit() {
        this.bgX = this.width / 2 - 92;
        this.bgY = this.height / 2 - 65;
        this.OP_BUTTONS.put(OperationButtonType.TYPE.getCode(), new OperationButton(OperationButtonType.TYPE.getCode(), context -> {
            // 绘制背景
            int lineColor = context.button().isHovered() ? 0xEEFFFFFF : 0xEE000000;
            GuiUtils.fill(context.graphics(), (int) context.button().getX(), (int) context.button().getY(), (int) context.button().getWidth(), (int) context.button().getHeight(), 0xEE707070, 2);
            GuiUtils.fillOutLine(context.graphics(), (int) context.button().getX(), (int) context.button().getY(), (int) context.button().getWidth(), (int) context.button().getHeight(), 1, lineColor, 2);
            //context.graphics().renderItem(itemStack, (int) context.button().getX() + 2, (int) context.button().getY() + 2);
            //Text text = this.inventoryMode ? Text.i18n("列出模式\n物品栏 (%s)", playerItemList.size()) : Text.i18n("列出模式\n所有物品 (%s)", allItemList.size());
            //context.button().setTooltip(text);
        }).setX(this.bgX - GuiUtils.ITEM_ICON_SIZE - 4).setY(this.bgY).setWidth(GuiUtils.ITEM_ICON_SIZE + 4).setHeight(GuiUtils.ITEM_ICON_SIZE + 4));
        this.OP_BUTTONS.put(OperationButtonType.TYPE.getCode(), new OperationButton(OperationButtonType.TYPE.getCode(), context -> {
            // 绘制背景
            int lineColor = context.button().isHovered() ? 0xEEFFFFFF : 0xEE000000;
            GuiUtils.fill(context.graphics(), (int) context.button().getX(), (int) context.button().getY(), (int) context.button().getWidth(), (int) context.button().getHeight(), 0xEE707070, 2);
            GuiUtils.fillOutLine(context.graphics(), (int) context.button().getX(), (int) context.button().getY(), (int) context.button().getWidth(), (int) context.button().getHeight(), 1, lineColor, 2);
            //context.graphics().renderItem(itemStack, (int) context.button().getX() + 2, (int) context.button().getY() + 2);
            //Text text = this.inventoryMode ? Text.i18n("列出模式\n物品栏 (%s)", playerItemList.size()) : Text.i18n("列出模式\n所有物品 (%s)", allItemList.size());
            //context.button().setTooltip(text);
        }).setX(this.bgX - GuiUtils.ITEM_ICON_SIZE - 4).setY(this.bgY + GuiUtils.ITEM_ICON_SIZE + 4).setWidth(GuiUtils.ITEM_ICON_SIZE + 4).setHeight(GuiUtils.ITEM_ICON_SIZE + 4));

    }

    @Override
    protected void renderBgOthers(GuiGraphics pGuiGraphics, int pX, int pY) {

    }

    @Override
    protected void renderFg(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        for (OperationButton button : OP_BUTTONS.values()) button.render(pGuiGraphics, pMouseX, pMouseY);
        for (OperationButton button : OP_BUTTONS.values()) button.renderPopup(pGuiGraphics, pMouseX, pMouseY);
    }
}
