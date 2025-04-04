package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.api.common.tile.BaseTileEntity;
import committee.nova.mods.avaritia.api.utils.lang.Localizable;
import committee.nova.mods.avaritia.init.registry.ModBlocks;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import committee.nova.mods.avaritia.util.ToolUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/4/4 01:05
 * @Description:
 */
public class InfinityClockTile extends BaseTileEntity {
    int range = 12;
    int speed = 100;
    private Iterable<BlockPos> targetBlocks;
    public InfinityClockTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.infinity_clock_tile.get(), pos, state);
        this.targetBlocks = BlockPos.betweenClosed(pos.getX() - this.range, pos.getY() - this.range, pos.getZ() - this.range,
                pos.getX() + this.range, pos.getY() + this.range, pos.getZ() + this.range);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, InfinityClockTile tile) {
        var randomTicks = level.getGameRules().getInt(GameRules.RULE_RANDOMTICKING);
        tile.targetBlocks.forEach(blockPos -> {
            if(level instanceof ServerLevel serverLevel && !serverLevel.getBlockState(blockPos).is(ModBlocks.infinity_clock.get()))
                ToolUtils.speedBlockTick(blockPos, serverLevel, tile.speed, randomTicks);
        });
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
