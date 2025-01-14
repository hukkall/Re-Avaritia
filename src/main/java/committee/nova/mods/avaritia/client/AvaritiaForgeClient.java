package committee.nova.mods.avaritia.client;

import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.iface.IFilterItem;
import committee.nova.mods.avaritia.client.screen.ItemFilterScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

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
    public static final KeyMapping SIGN_IN_SCREEN_KEY = new KeyMapping("key.avaritia.filter",
            GLFW.GLFW_KEY_H, CATEGORIES);


    /**
     * 在客户端Tick事件触发时执行
     *
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        // 检测并消费点击事件
        if (SIGN_IN_SCREEN_KEY.consumeClick()) {
            // 打开界面
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof IFilterItem) {
                Minecraft.getInstance().setScreen(new ItemFilterScreen());
            }
        }
    }

}
