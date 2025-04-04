package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.api.common.tile.BaseTileEntity;
import committee.nova.mods.avaritia.api.utils.lang.Localizable;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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
    public InfinityClockTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.infinity_clock_tile.get(), pos, state);
    }

    int range = 12;
    int amount = 100;
    Predicate<BlockState> states = (state) -> state.isAir();

    public static void serverTick(Level level, BlockPos pos, BlockState state, InfinityClockTile tile) {
        for (BlockPos targetPos : BlockPos.betweenClosed(pos.offset(-tile.range, -tile.range, -tile.range), pos.offset(tile.range, tile.range, tile.range))){
            BlockState targetState = level.getBlockState(targetPos);
            Block block = targetState.getBlock();

            BlockEntity tileEntity = level.getBlockEntity(pos);
            for (int i = 0; i < tile.amount /(tileEntity == null ? 5 : 1); i ++){
                if (tileEntity == null){
                    block.tick(targetState, (ServerLevel) level, pos, level.random);
                } else if (targetState.getBlock() instanceof EntityBlock entityBlock) {
                    BlockEntityTicker<BlockEntity> ticker = entityBlock.getTicker(level, targetState, (BlockEntityType<BlockEntity>) tileEntity.getType());
                    if (ticker != null) {
                        ticker.tick(level, pos, targetState, tileEntity);
                    }
                }
            }
        }
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
