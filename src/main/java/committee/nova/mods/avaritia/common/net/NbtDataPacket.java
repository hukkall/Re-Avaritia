package committee.nova.mods.avaritia.common.net;

import committee.nova.mods.avaritia.api.iface.IDataReceiver;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/2/23 01:45
 * @Description:
 */
public class NbtDataPacket {
    public CompoundTag tag;

    public NbtDataPacket(CompoundTag tag) {
        this.tag = tag;
    }

    public NbtDataPacket(FriendlyByteBuf pb) {
        tag = pb.readAnySizeNbt();
    }

    public static void write(NbtDataPacket msg, FriendlyByteBuf pb) {
        pb.writeNbt(msg.tag);
    }

    public static void run(NbtDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            ctx.get().enqueueWork(() -> {
                ServerPlayer sender = ctx.get().getSender();
                if (sender != null && sender.containerMenu instanceof IDataReceiver dataReceiver) {
                    dataReceiver.receive(msg.tag);
                }
            });
        } else if(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            ctx.get().enqueueWork(() -> {
                if(Minecraft.getInstance().screen instanceof IDataReceiver dataReceiver) {
                    dataReceiver.receive(msg.tag);
                }
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
