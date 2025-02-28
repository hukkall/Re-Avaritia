package committee.nova.mods.avaritia.client;

import com.mojang.blaze3d.platform.InputConstants;
import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.iface.IFilterItem;
import committee.nova.mods.avaritia.client.screen.ItemFilterScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;

/**
 * Author cnlimiter
 * CreateTime 2023/6/17 23:24
 * Name AvaritiaForgeClient
 * Description
 */

@Mod.EventBusSubscriber(modid = Static.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AvaritiaForgeClient {
    private static final String CATEGORIES = "key.avaritia.categories";

    // 定义按键绑定
    public static final KeyMapping FILTER_KEY = new KeyMapping("key.avaritia.filter",
            InputConstants.KEY_H, CATEGORIES);

    public static final KeyMapping SORT_0 = new KeyMapping("key.avaritia.infinity_chest.sort0", InputConstants.KEY_0, CATEGORIES);
    public static final KeyMapping SORT_1 = new KeyMapping("key.avaritia.infinity_chest.sort1", InputConstants.KEY_1, CATEGORIES);
    public static final KeyMapping SORT_2 = new KeyMapping("key.avaritia.infinity_chest.sort2", InputConstants.KEY_2, CATEGORIES);
    public static final KeyMapping SORT_3 = new KeyMapping("key.avaritia.infinity_chest.sort3", InputConstants.KEY_3, CATEGORIES);
    public static final KeyMapping SORT_4 = new KeyMapping("key.avaritia.infinity_chest.sort4", InputConstants.KEY_4, CATEGORIES);
    public static final KeyMapping SORT_5 = new KeyMapping("key.avaritia.infinity_chest.sort5", InputConstants.KEY_5, CATEGORIES);
    public static final KeyMapping SORT_6 = new KeyMapping("key.avaritia.infinity_chest.sort6", InputConstants.KEY_6, CATEGORIES);
    public static final KeyMapping SORT_7 = new KeyMapping("key.avaritia.infinity_chest.sort7", InputConstants.KEY_7, CATEGORIES);
    public static final KeyMapping SORT_8 = new KeyMapping("key.avaritia.infinity_chest.sort8", InputConstants.KEY_8, CATEGORIES);
    public static final KeyMapping SORT_9 = new KeyMapping("key.avaritia.infinity_chest.sort9", InputConstants.KEY_9, CATEGORIES);

    /**
     * 在客户端Tick事件触发时执行
     *
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 检测并消费点击事件
        if (FILTER_KEY.consumeClick()) {
            // 打开界面
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof IFilterItem) {
                Minecraft.getInstance().setScreen(new ItemFilterScreen());
            }
        }
    }

    private static Component[] tooltipExt = new Component[0];
    public static void setTooltip(Component... string) {
        tooltipExt = string;
    }

    @SubscribeEvent
    public static void getTooltip(ItemTooltipEvent evt) {
        Collections.addAll(evt.getToolTip(), tooltipExt);
    }

}
