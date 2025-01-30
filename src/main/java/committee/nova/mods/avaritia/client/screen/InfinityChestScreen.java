package committee.nova.mods.avaritia.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.client.screen.BaseContainerScreen;
import committee.nova.mods.avaritia.api.client.util.GuiUtils;
import committee.nova.mods.avaritia.api.utils.StringUtils;
import committee.nova.mods.avaritia.client.AvaritiaForgeClient;
import committee.nova.mods.avaritia.common.menu.InfinityChestMenu;
import committee.nova.mods.avaritia.common.net.C2SChangePagePacket;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.init.handler.NetworkHandler;
import committee.nova.mods.avaritia.init.registry.ModTooltips;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.client.searchtree.SearchTree;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/17 02:50
 * @Description:
 */
public class InfinityChestScreen extends BaseContainerScreen<InfinityChestMenu> {
    private static final KeyMapping[] SORT_KEYS = new KeyMapping[]{AvaritiaForgeClient.SORT_0, AvaritiaForgeClient.SORT_1, AvaritiaForgeClient.SORT_2, AvaritiaForgeClient.SORT_3, AvaritiaForgeClient.SORT_4, AvaritiaForgeClient.SORT_5, AvaritiaForgeClient.SORT_6, AvaritiaForgeClient.SORT_7, AvaritiaForgeClient.SORT_8, AvaritiaForgeClient.SORT_9};
    private static final ResourceLocation MULTI_PAGE_TEXTURE = Static.rl("textures/gui/infinity_chest.png");

    private final int inventoryRows;
    /**
     * 输入框
     */
    private EditBox inputField;
    /**
     * 输入框文本
     */
    private String inputFieldText = "";
    /**
     * 搜索结果
     */
    private final List<ItemStack> itemList = new ArrayList<>();
    /**
     * 显示的标签
     */
    private final Set<TagKey<Item>> visibleTags = new HashSet<>();

    public InfinityChestScreen(InfinityChestMenu container, Inventory inventory, Component title) {
        super(container, inventory, title, MULTI_PAGE_TEXTURE);
        this.inventoryRows = ModConfig.inventoryRows.get();
        this.imageHeight = 132 + this.inventoryRows * 18;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (inputField.isFocused()) inputField.tick();
    }

    @Override
    protected void subInit() {
        this.inputField = GuiUtils.newTextFieldWidget(this.font, this.leftPos + 6, this.topPos + 18, 80, 14, Component.literal(""));
        this.inputField.setVisible(false);
        this.inputField.setValue(this.inputFieldText);
        this.inputField.setTextColor(16777215);
        this.addRenderableWidget(this.inputField);
        this.addRenderableWidget(new ImageButton(this.leftPos + 121, this.topPos + 6, 11, 11, 187, 22, 11, MULTI_PAGE_TEXTURE, (button) -> NetworkHandler.CHANNEL.sendToServer(new C2SChangePagePacket((this.menu).getCurrentPage() - 10))));
        this.addRenderableWidget(new ImageButton(this.leftPos + 134, this.topPos + 6, 7, 11, 183, 0, 11, MULTI_PAGE_TEXTURE, (button) -> NetworkHandler.CHANNEL.sendToServer(new C2SChangePagePacket((this.menu).getCurrentPage() - 1))));
        this.addRenderableWidget(new ImageButton(this.leftPos + 149, this.topPos + 6, 7, 11, 176, 0, 11, MULTI_PAGE_TEXTURE, (button) -> NetworkHandler.CHANNEL.sendToServer(new C2SChangePagePacket((this.menu).getCurrentPage() + 1))));
        this.addRenderableWidget(new ImageButton(this.leftPos + 158, this.topPos + 6, 11, 11, 176, 22, 11, MULTI_PAGE_TEXTURE, (button) -> NetworkHandler.CHANNEL.sendToServer(new C2SChangePagePacket((this.menu).getCurrentPage() + 10))));
    }

    @Override
    protected void renderBgOthers(GuiGraphics pGuiGraphics, int pX, int pY) {
        pGuiGraphics.blit(MULTI_PAGE_TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.inventoryRows * 18 + 35);
        pGuiGraphics.blit(MULTI_PAGE_TEXTURE, this.leftPos, this.topPos + this.inventoryRows * 18 + 35, 0, 143, this.imageWidth, 97);
        int index = this.menu.getSwapIndex();
        Slot slot = index >= 0 && index < this.menu.slots.size() ? this.menu.getSlot(index) : null;
        if (slot != null && Objects.equals(slot.container, this.menu.getChestInventory())) {
            RenderSystem.disableDepthTest();
            int xPos = this.leftPos + slot.x;
            int yPos = this.topPos + slot.y;
            RenderSystem.colorMask(true, true, true, false);
            pGuiGraphics.fillGradient(xPos, yPos, xPos + 16, yPos + 16, -2130771968, -2130771968);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }
        this.inputFieldText = this.inputField.getValue();
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
        String page = (this.menu).getCurrentPage() + 1 + " / ";
        int pageWidth = this.font.width(page);
        String maxPage = Integer.toString((this.menu).getMaxPage());
        pGuiGraphics.drawString(font, page, 169 - 20 - pageWidth, 24, 4210752, false);
        pGuiGraphics.drawString(font, "∞", 169 - 20, 24, 4210752, false);
    }

    @Override
    protected @NotNull List<Component> getTooltipFromContainerItem(@NotNull ItemStack pStack) {
        List<Component> tooltip = super.getTooltipFromContainerItem(pStack);
        Slot slot = this.getSlotUnderMouse();
        if (slot != null && Objects.equals(slot.container, (this.menu).getChestInventory())) {
            long count = (this.menu).getItemCount(slot.getSlotIndex());
            String text = ModTooltips.NUM_ITEMS.args(count).buildString();
            tooltip.add(1, Component.literal(text));
        }

        return tooltip;
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (Stream.of(SORT_KEYS).anyMatch(key -> key.matches(pKeyCode, pScanCode))){
            for (int i = 0; i < SORT_KEYS.length; i++) {
               if (SORT_KEYS[i].matches(pKeyCode, pScanCode)) {
                   super.slotClicked(null, 0, i, ClickType.CLONE);
                   return true;
               }
            }
            return false;
        } else if (pKeyCode == GLFW.GLFW_KEY_ESCAPE || (pKeyCode == GLFW.GLFW_KEY_BACKSPACE && !this.inputField.isFocused())) {
            Minecraft.getInstance().setScreen(null);
            return true;
        } else if ((pKeyCode == GLFW.GLFW_KEY_ENTER || pKeyCode == GLFW.GLFW_KEY_KP_ENTER) && this.inputField.isFocused()) {
            this.updateSearchResults();
            return true;
        } else {
            return super.keyPressed(pKeyCode, pScanCode, pModifiers);
        }
    }

    private void updateSearchResults() {
        String s = this.inputField == null ? null : this.inputField.getValue();
        this.itemList.clear();
        this.visibleTags.clear();
        if (StringUtils.isNotNullOrEmpty(s)) {
            SearchTree<ItemStack> isearchtree;
            if (s.startsWith("#")) {
                s = s.substring(1);
                isearchtree = Minecraft.getInstance().getSearchTree(SearchRegistry.CREATIVE_TAGS);
                this.updateVisibleTags(s);
            } else {
                isearchtree = Minecraft.getInstance().getSearchTree(SearchRegistry.CREATIVE_NAMES);
            }
            this.itemList.addAll(isearchtree.search(s.toLowerCase(Locale.ROOT)));
        } else {
            this.menu.getTileEntity().containers.forEach((integer, storageItem) -> {
                if (storageItem.getCount() > 0) {
                    this.itemList.add(storageItem.getStack());
                }
            });
        }
    }

    private void updateVisibleTags(String string) {
        int i = string.indexOf(58);
        Predicate<ResourceLocation> predicate;
        if (i == -1) {
            predicate = (resourceLocation) -> resourceLocation.getPath().contains(string);
        } else {
            String s = string.substring(0, i).trim();
            String s1 = string.substring(i + 1).trim();
            predicate = (resourceLocation) -> resourceLocation.getNamespace().contains(s) && resourceLocation.getPath().contains(s1);
        }
        BuiltInRegistries.ITEM.getTagNames().filter((tagKey) -> predicate.test(tagKey.location())).forEach(this.visibleTags::add);
    }

    @Override
    protected void slotClicked(@Nullable Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (type == ClickType.CLONE) {
            super.slotClicked(null, slotId, 1, type);
        } else {
            if (slotIn != null) {
                if (type == ClickType.PICKUP) {
                    if (hasAltDown()) {
                        if (Objects.equals(slotIn.container, this.menu.getChestInventory())) {
                            super.slotClicked(slotIn, slotId, 2, type);
                            return;
                        }
                    } else if (hasControlDown() && (Objects.equals(slotIn.container, this.menu.getChestInventory()) || Objects.equals(slotIn.container, this.menu.getPlayerInventory()))) {
                        super.slotClicked(slotIn, slotId, 3, type);
                        return;
                    }
                } else if (type == ClickType.QUICK_MOVE && (Objects.equals(slotIn.container, this.menu.getChestInventory()) || Objects.equals(slotIn.container, this.menu.getPlayerInventory())) && hasControlDown()) {
                    super.slotClicked(slotIn, slotId, 2, type);
                    return;
                }
            }

            if (slotIn != null) super.slotClicked(slotIn, slotId, mouseButton, type);
        }
    }
}
