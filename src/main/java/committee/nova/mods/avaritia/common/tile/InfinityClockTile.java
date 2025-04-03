package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.api.common.tile.BaseTileEntity;
import committee.nova.mods.avaritia.api.utils.lang.Localizable;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/4/4 01:05
 * @Description:
 */
public class InfinityClockTile extends BaseTileEntity {
    public InfinityClockTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.infinity_clock_tile.get(), pos, state);
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, InfinityClockTile tile) {

    }

    @Override
    public @NotNull Component getDisplayName() {
        return Localizable.of("container.infinity_clock").build();
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return null;
    }
}
