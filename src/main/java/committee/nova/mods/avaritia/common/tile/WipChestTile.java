package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.api.common.tile.BaseTileEntity;
import committee.nova.mods.avaritia.common.menu.WipChestMenu;
import committee.nova.mods.avaritia.common.sync.ServerChannelManager;
import committee.nova.mods.avaritia.common.wrappers.channel.Channel;
import committee.nova.mods.avaritia.common.wrappers.channel.NullChannel;
import committee.nova.mods.avaritia.common.wrappers.channel.ServerChannel;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 15:28
 * @Description:
 */
public class WipChestTile extends BaseTileEntity {
    private static final Component CONTAINER_NAME = Component.translatable("container.infinity_chest");
    private final int slotIndex;
    @Getter private UUID owner;
    @Getter private boolean locked = false;
    @Getter private boolean craftingMode = false;
    @Getter private String filter = "";
    @Getter private byte sortType = 4;
    @Getter private byte viewType = 0;
    @Getter private UUID channelOwner;
    @Getter private int channelID = -1;
    private boolean waterlogged = false;
    private final HashSet<ServerPlayer> channelSelectors = new HashSet<>();


    @Getter private ServerChannel channel = NullChannel.INSTANCE;
    @Getter private LazyOptional<?> capability = LazyOptional.of(() -> channel);


    public WipChestTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.infinity_chest_tile.get(), pos, state);
        this.slotIndex = -2;
        onBlockStateChange();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WipChestTile blockEntity) {
        if (level.isClientSide) return;
        if (blockEntity.channel.isRemoved()) {
            if (blockEntity.channelID >= 0) {
                blockEntity.channelOwner = null;
                blockEntity.channelID = -1;
            }
        } else {
            if (blockEntity.waterlogged) blockEntity.channel.addFluid(new FluidStack(Fluids.WATER, 1000));
        }
    }

    public void onBlockStateChange() {
        BlockState state = getBlockState();
        waterlogged = state.getValue(BlockStateProperties.WATERLOGGED);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("owner")) {
            owner = pTag.getUUID("owner");
            locked = pTag.getBoolean("locked");
        }
        if (pTag.contains("craftingMode")) craftingMode = pTag.getBoolean("craftingMode");
        if (pTag.contains("filter")) filter = pTag.getString("filter");
        if (pTag.contains("sortType")) sortType = pTag.getByte("sortType");
        if (pTag.contains("viewType")) viewType = pTag.getByte("viewType");
        if (pTag.contains("channel")) {
            CompoundTag channel = pTag.getCompound("channel");
            channelOwner = channel.getUUID("channelOwner");
            channelID = channel.getInt("channelID");
        }
        channel = ServerChannelManager.getInstance().getChannel(channelOwner, channelID);
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (owner != null) {
            pTag.putUUID("owner", owner);
            pTag.putBoolean("locked", locked);
        }
        pTag.putBoolean("craftingMode", craftingMode);
        pTag.putString("filter", filter);
        pTag.putByte("sortType", sortType);
        pTag.putByte("viewType", viewType);
        if (channelID >= 0) {
            CompoundTag channel =  new CompoundTag();
            channel.putUUID("channelOwner", channelOwner);
            channel.putInt("channelID", channelID);
            pTag.put("channel", channel);
        }
    }

    @Override
    public @NotNull AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory, @NotNull Player pPlayer) {
        return new WipChestMenu(pContainerId, pInventory.player, this, slotIndex);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return CONTAINER_NAME;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (channel.isRemoved()) return LazyOptional.empty();
        else if (cap == ForgeCapabilities.ITEM_HANDLER
                || cap == ForgeCapabilities.FLUID_HANDLER
                || cap == ForgeCapabilities.ENERGY) {
            return capability.cast();
        }
        return LazyOptional.empty();
    }

    public void inhaleItem(ItemEntity itemEntity) {
        if (channel.isRemoved()) return;
        ItemStack itemStack = itemEntity.getItem();
        channel.addItem(itemStack);
        if (!itemStack.isEmpty()) {
            BlockPos blockPos = getBlockPos();
            itemEntity.teleportTo(blockPos.getX() + 0.5, blockPos.getY() - 0.26, blockPos.getZ() + 0.5);
            itemEntity.setDeltaMovement(0, -0.1, 0);
        }
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.setChanged();
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        this.setChanged();
    }

    public void setCraftingMode(Boolean craftingMode) {
        this.craftingMode = craftingMode;
        this.setChanged();
    }

    public void setFilter(String filter) {
        this.filter = filter;
        this.setChanged();
    }

    public void setSortType(byte sortType) {
        this.sortType = sortType;
        this.setChanged();
    }

    public void setViewType(byte viewType) {
        this.viewType = viewType;
        this.setChanged();
    }

    public void setChannelOwner(UUID owner) {
        this.channelOwner = owner;
        this.setChanged();
    }

    public void setChannelId(int id) {
        this.channelID = id;
        this.setChanged();
    }

    public void setChannel(ServerChannel channel) {
        this.channel = channel;
        this.setChanged();
    }

    public void setCapability(LazyOptional<?> capability) {
        this.capability = capability;
        this.setChanged();
    }
}
