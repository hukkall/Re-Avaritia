package committee.nova.mods.avaritia.client.screen;

import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.common.menu.WipChestMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/2/15 20:08
 * @Description:
 */
public class WipChestScreen extends AbstractWipChestScreen {
    private static final ResourceLocation gui = Static.rl("textures/gui/storage_terminal.png");

    public WipChestScreen(WipChestMenu container, Inventory inventory, Component title) {
        super(container, inventory, title, 5, 202, 7, 17);
    }

    @Override
    protected void subInit() {
        imageWidth = 194;
        imageHeight = 202;
        super.subInit();
        onPacket();
    }

    @Override
    public ResourceLocation getGui() {
        return gui;
    }

    @Override
    public void render(GuiGraphics st, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(st, mouseX, mouseY, partialTicks);
        super.render(st, mouseX, mouseY, partialTicks);
    }
}
