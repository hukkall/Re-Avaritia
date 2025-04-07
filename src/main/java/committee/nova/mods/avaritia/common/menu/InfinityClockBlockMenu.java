package committee.nova.mods.avaritia.common.menu;

import committee.nova.mods.avaritia.api.common.menu.BaseTileMenu;
import committee.nova.mods.avaritia.common.tile.InfinityClockTile;
import committee.nova.mods.avaritia.init.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/4/5 23:14
 * @Description:
 */
public class InfinityClockBlockMenu extends BaseTileMenu<InfinityClockTile> {
    public InfinityClockBlockMenu(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, buffer.readBlockPos());
    }

    public InfinityClockBlockMenu(int id, Inventory playerInventory, @NotNull BlockPos blockPos) {
        super(ModMenus.infinity_clock_block.get(), id, playerInventory, blockPos);
        createInventorySlots(playerInventory);
    }

}
