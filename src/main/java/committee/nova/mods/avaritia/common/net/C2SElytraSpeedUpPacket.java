package committee.nova.mods.avaritia.common.net;

import committee.nova.mods.avaritia.init.registry.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SElytraSpeedUpPacket {
    public C2SElytraSpeedUpPacket(FriendlyByteBuf buf) {
    }

    public C2SElytraSpeedUpPacket() {
    }

    public static void write(C2SElytraSpeedUpPacket msg, FriendlyByteBuf buf) {
    }

    public static void run(C2SElytraSpeedUpPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            if (player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.infinity_elytra.get()) && player.isFallFlying())
                player.serverLevel().addFreshEntity(new FireworkRocketEntity(player.serverLevel(), Items.AIR.getDefaultInstance(), player));
        });
        ctx.get().setPacketHandled(true);
    }
}
