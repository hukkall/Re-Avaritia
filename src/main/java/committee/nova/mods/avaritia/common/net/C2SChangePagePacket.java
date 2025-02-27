package committee.nova.mods.avaritia.common.net;

import committee.nova.mods.avaritia.common.menu.OffsetChestMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * C2SJEIGhostPacket
 *
 * @author cnlimiter
 * @version 1.0
 * @description
 * @date 2024/3/28 14:02
 */
public class C2SChangePagePacket {
    private final int page;


    public C2SChangePagePacket(FriendlyByteBuf buf) {
        this.page = buf.readInt();
    }

    public C2SChangePagePacket(int page) {
        this.page = page;
    }

    public static void write(C2SChangePagePacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.page);
    }

    public static void run(C2SChangePagePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (player.containerMenu instanceof OffsetChestMenu infinityChestMenu) {
                infinityChestMenu.changePage(msg.page);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
