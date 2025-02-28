package committee.nova.mods.avaritia.common.block.chest;

import committee.nova.mods.avaritia.api.common.block.BaseTileEntityBlock;
import committee.nova.mods.avaritia.common.sync.ClientChannelManager;
import committee.nova.mods.avaritia.common.sync.ServerChannelManager;
import committee.nova.mods.avaritia.common.tile.WipChestTile;
import committee.nova.mods.avaritia.common.wrappers.channel.ServerChannel;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/7/13 下午12:38
 * @Description:
 */
public class InfinityChestBlock extends BaseTileEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public InfinityChestBlock() {
        super(Properties.of().mapColor(MapColor.GOLD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.GLASS).ignitedByLava());
        this.registerDefaultState(defaultBlockState().setValue(WATERLOGGED, Boolean.FALSE).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> getServerTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModTileEntities.infinity_chest_tile.get(), WipChestTile::tick);
    }

    @Override
    protected <T extends BlockEntity> BlockEntityTicker<T> getClientTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTicker(type, ModTileEntities.infinity_chest_tile.get(), WipChestTile::tick);
    }


    @Override
    public void appendHoverText(ItemStack pStack, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        if (Minecraft.getInstance().player == null) return;
        if (!pStack.hasTag()) return;
        if (pStack.getTag().contains("BlockEntityTag")) {
            CompoundTag nbt = pStack.getTag().getCompound("BlockEntityTag");
            if (nbt.contains("owner")) {
                UUID selfUUID = Minecraft.getInstance().player.getUUID();
                UUID ownerUUID = nbt.getUUID("owner");
                String ownerName = ClientChannelManager.getInstance().getUserName(nbt.getUUID("owner"));
                boolean lock = nbt.getBoolean("locked");
                if (selfUUID.equals(ownerUUID)) pTooltip.add(Component.translatable("gui.avaritia.owner", "§a" + ownerName));
                else if (lock) pTooltip.add(Component.translatable("gui.avaritia.owner", "§c" + ownerName));
                else pTooltip.add(Component.translatable("gui.avaritia.owner", ownerName));
            }
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult trace) {
        if (!level.isClientSide()) {
            var tile = level.getBlockEntity(pos);

            if (tile instanceof WipChestTile chestTile) {
                NetworkHooks.openScreen((ServerPlayer) player, chestTile, buf -> {
                    buf.writeBlockPos(pos);
                    buf.writeInt(-2);
                    buf.writeUUID(chestTile.getOwner());
                    buf.writeBoolean(chestTile.isLocked());
                    buf.writeBoolean(chestTile.isCraftingMode());
                    buf.writeUtf(chestTile.getFilter(), 64);
                    buf.writeByte(chestTile.getSortType());
                    buf.writeByte(chestTile.getViewType());
                    buf.writeUUID(chestTile.getChannelOwner());
                    buf.writeInt(chestTile.getChannelID());
                });
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
            Direction direction = context.getClickedFace().getOpposite();
            FluidState ifluidstate = context.getLevel().getFluidState(context.getClickedPos());
            if(direction.getAxis() == Direction.Axis.Y) {
                direction = context.getHorizontalDirection();
            }
            return this.defaultBlockState().setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.NORTH : direction).
                    setValue(WATERLOGGED, ifluidstate.getType() == Fluids.WATER);
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(@NotNull BlockPos pPos, @NotNull BlockState pState) {
        return new WipChestTile(pPos, pState);
    }

    @Override
    public void setPlacedBy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @Nullable LivingEntity pPlacer, @NotNull ItemStack pStack) {
        if (pPlacer instanceof ServerPlayer player && !pStack.getOrCreateTag().contains("BlockEntityTag")) {
            WipChestTile blockEntity = (WipChestTile) pLevel.getBlockEntity(pPos);
            if (blockEntity != null) {
                blockEntity.setOwner(pPlacer.getUUID());
                blockEntity.setChannelOwner(pPlacer.getUUID());
                blockEntity.setChannelId(1);
                ServerChannel channel = new ServerChannel("block");
                ServerChannelManager.getInstance().tryAddChannel(player, channel, true);
                blockEntity.setChannel(channel);
                blockEntity.setCapability(LazyOptional.of(() -> channel));
            }
        }
    }

    @Override
    public void entityInside(@NotNull BlockState pState, Level pLevel, @NotNull BlockPos pPos, @NotNull Entity pEntity) {
        if (pLevel.isClientSide) return;
        BlockEntity blockentity = pLevel.getBlockEntity(pPos);
        if (blockentity instanceof WipChestTile wipChestTile && pEntity instanceof ItemEntity itemEntity) {
            wipChestTile.inhaleItem(itemEntity);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(WATERLOGGED, FACING);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState pState, @NotNull Direction pFacing, @NotNull BlockState pFacingState, @NotNull LevelAccessor pLevel, @NotNull BlockPos pCurrentPos, @NotNull BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }
        return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull PathComputationType pType) {
        return false;
    }

}
