package committee.nova.mods.avaritia.init.handler;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import committee.nova.mods.avaritia.Const;
import committee.nova.mods.avaritia.init.config.ModConfig;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Const.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommandHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        LiteralArgumentBuilder<CommandSourceStack> cmd = Commands.literal("helmetHunger")
                .requires(s -> s.hasPermission(2))
                .executes(ctx -> toggle(ctx.getSource()))
                .then(Commands.literal("on").executes(ctx -> set(ctx.getSource(), true)))
                .then(Commands.literal("off").executes(ctx -> set(ctx.getSource(), false)));
        event.getDispatcher().register(cmd);
    }

    private static int toggle(CommandSourceStack source) {
        boolean newVal = !ModConfig.helmetKeepHungerFull.get();
        ModConfig.helmetKeepHungerFull.set(newVal);
        source.sendSuccess(() -> Component.translatable(newVal
                ? "command.avaritia.helmet_hunger.enabled"
                : "command.avaritia.helmet_hunger.disabled"), true);
        return 1;
    }

    private static int set(CommandSourceStack source, boolean value) {
        ModConfig.helmetKeepHungerFull.set(value);
        source.sendSuccess(() -> Component.translatable(value
                ? "command.avaritia.helmet_hunger.enabled"
                : "command.avaritia.helmet_hunger.disabled"), true);
        return 1;
    }
}
