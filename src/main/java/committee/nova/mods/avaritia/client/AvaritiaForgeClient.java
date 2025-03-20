package committee.nova.mods.avaritia.client;

import com.mojang.blaze3d.platform.InputConstants;
import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.iface.IFilterItem;
import committee.nova.mods.avaritia.client.screen.ItemFilterScreen;
import committee.nova.mods.avaritia.common.menu.NeutronRingMenu;
import committee.nova.mods.avaritia.common.net.C2SElytraSpeedUpPacket;
import committee.nova.mods.avaritia.common.net.C2SOpenRingPack;
import committee.nova.mods.avaritia.init.handler.NetworkHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;

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
    public static final KeyMapping RING_KEY = new KeyMapping("key.avaritia.neutron_ring", InputConstants.KEY_N, CATEGORIES);

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

    private static int infinityElytraCooldown = 0;

    /**
     * 在客户端Tick事件触发时执行
     *
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        LocalPlayer player = Minecraft.getInstance().player;
        // 检测并消费点击事件
        while (FILTER_KEY.consumeClick() && player != null) {
            // 打开界面
            if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof IFilterItem) {
                Minecraft.getInstance().setScreen(new ItemFilterScreen());
            }
        }
        while (RING_KEY.consumeClick() && player != null) {
            NetworkHandler.CHANNEL.sendToServer(new C2SOpenRingPack());
        }

        infinityElytraCooldown = Math.max(infinityElytraCooldown - 1, 0);
        if (Minecraft.getInstance().options.keyJump.isDown() && infinityElytraCooldown <= 0) {
            infinityElytraCooldown = 50;
            NetworkHandler.CHANNEL.sendToServer(new C2SElytraSpeedUpPacket());
        }
    }



    // region tooltipExt
    private static Component[] tooltipExt = new Component[0];

    public static void setTooltip(Component... string) {
        tooltipExt = string;
    }

    @SubscribeEvent
    public static void getTooltip(ItemTooltipEvent evt) {
        Collections.addAll(evt.getToolTip(), tooltipExt);
    }

    // endregion

}
