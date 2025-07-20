package committee.nova.mods.avaritia.init.handler;

import com.mojang.brigadier.CommandDispatcher;
import committee.nova.mods.avaritia.Const;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Const.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HungerCommandHandler {
    private static boolean keepFull = true;

    public static boolean keepFull() {
        return keepFull;
    }

    @SubscribeEvent
    public static void onRegister(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("avaritiahunger")
                .requires(cs -> true)
                .executes(ctx -> toggle(ctx.getSource())));
    }

    private static int toggle(CommandSourceStack source) {
        keepFull = !keepFull;
        source.sendSystemMessage(Component.translatable(keepFull ?
                "command.avaritia.hunger.enabled" :
                "command.avaritia.hunger.disabled"));
        return 1;
    }

    @SubscribeEvent
    public static void tick(LivingEvent.LivingTickEvent event) {
        if (!keepFull && event.getEntity() instanceof Player player) {
            if (player.getFoodData().getFoodLevel() <= 1) {
                player.getFoodData().setFoodLevel(1);
            }
        }
    }
}
