package committee.nova.mods.avaritia.common.net.channel;

import committee.nova.mods.avaritia.common.sync.ServerChannelManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/3/1 15:05
 * @Description:
 */
public class ChannelAddPack {

    private final String name;
    private final boolean pub;

    public ChannelAddPack(FriendlyByteBuf buf) {
        this.name = buf.readUtf(64);
        this.pub = buf.readBoolean();
    }

    public ChannelAddPack(String name, boolean pub) {
        this.name = name;
        this.pub = pub;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(name, 64);
        buf.writeBoolean(pub);
    }

    public void run(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            if (name.isEmpty()) return;
            ServerPlayer player = context.get().getSender();
            if (player == null) return;
            ServerChannelManager.getInstance().tryAddChannel(player, name, pub);
        });
        context.get().setPacketHandled(true);
    }
}

