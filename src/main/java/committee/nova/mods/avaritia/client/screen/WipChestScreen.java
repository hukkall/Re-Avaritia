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
 * @CreateTime: 2025/2/24 00:39
 * @Description:
 */
public class WipChestScreen extends AbstractStorageTerminalScreen<WipChestMenu> {
    private static final ResourceLocation gui = Static.rl("textures/gui/storage_terminal.png");
    public WipChestScreen(WipChestMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn, 5, 202, 7, 17);
    }

    @Override
    protected void init() {
        imageWidth = 194;
        imageHeight = 202;
        super.init();
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
