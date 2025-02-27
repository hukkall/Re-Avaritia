package committee.nova.mods.avaritia.init.handler;

import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.common.net.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/2 13:07
 * Version: 1.0
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkHandler {
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(Static.rl("main"), () -> {
        return "1.0";
    }, (s) -> {
        return true;
    }, (s) -> {
        return true;
    });
    public static int id = 0;
    ;

    @SubscribeEvent
    public static void init(FMLCommonSetupEvent event) {
        CHANNEL.registerMessage(id++, NbtDataPacket.class, NbtDataPacket::write, NbtDataPacket::new, NbtDataPacket::run);
        CHANNEL.registerMessage(id++, S2CSingularitiesPacket.class, S2CSingularitiesPacket::write, S2CSingularitiesPacket::new, S2CSingularitiesPacket::run, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, S2CTotemPacket.class, S2CTotemPacket::write, S2CTotemPacket::new, S2CTotemPacket::run, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, C2SJEIGhostPacket.class, C2SJEIGhostPacket::write, C2SJEIGhostPacket::new, C2SJEIGhostPacket::run, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, C2SICFilterPacket.class, C2SICFilterPacket::write, C2SICFilterPacket::new, C2SICFilterPacket::run, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, C2SItemFilterPacket.class, C2SItemFilterPacket::write, C2SItemFilterPacket::new, C2SItemFilterPacket::run, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, C2SRenamePacket.class, C2SRenamePacket::write, C2SRenamePacket::new, C2SRenamePacket::run, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        CHANNEL.registerMessage(id++, C2SChangePagePacket.class, C2SChangePagePacket::write, C2SChangePagePacket::new, C2SChangePagePacket::run, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void sendNbtDataToServer(CompoundTag tag) {
        CHANNEL.sendToServer(new NbtDataPacket(tag));
    }

    public static void sendNbtDataTo(ServerPlayer pl, CompoundTag tag) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> pl), new NbtDataPacket(tag));
    }
}
