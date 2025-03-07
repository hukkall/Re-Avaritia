package committee.nova.mods.avaritia.common.net;

import committee.nova.mods.avaritia.api.iface.IChangePage;
import committee.nova.mods.avaritia.common.menu.InfinityChestMenu;
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
public class C2SChangePagePack {
    private final int page;


    public C2SChangePagePack(FriendlyByteBuf buf) {
        this.page = buf.readInt();
    }

    public C2SChangePagePack(int page) {
        this.page = page;
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(page);
    }

    public void run(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (player.containerMenu instanceof IChangePage menu) {
                menu.changePage(page);
                player.containerMenu.broadcastChanges();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
