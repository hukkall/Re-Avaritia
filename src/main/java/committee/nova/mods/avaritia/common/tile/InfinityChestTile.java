package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.api.common.container.OffsetContainer;
import committee.nova.mods.avaritia.api.common.wrapper.OffsetItemStackWrapper;
import committee.nova.mods.avaritia.common.menu.InfinityChestMenu;
import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import committee.nova.mods.avaritia.util.StorageUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 15:28
 * @Description:
 */
public class InfinityChestTile extends BaseContainerBlockEntity implements OffsetContainer {
    public Int2ObjectMap<StorageItem> containers = StorageUtils.newContainers();
    private static final Component CONTAINER_NAME = Component.translatable("container.infinity_chest");
    private int page = 0;
    public InfinityChestTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.infinity_chest2_tile.get(), pos, state);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory) {
        return new InfinityChestMenu(pContainerId, pInventory, this.getBlockPos(), this, this.chestData);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return CONTAINER_NAME;
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        StorageUtils.loadAllItems(pTag, this.containers, true);
        this.page = pTag.getInt("Page");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        StorageUtils.saveAllItems(pTag, this.containers);
        pTag.putInt("Page", this.page);
    }


    private final ContainerData chestData = new ContainerData() {
        @Override
        public int get(int index) {
            return index == 0 ? InfinityChestTile.this.page : 0;
        }
        @Override
        public void set(int index, int value) {
            if (index == 0) InfinityChestTile.this.page = value;

        }
        @Override
        public int getCount() {
            return 1;
        }
    };

    @Override
    public OffsetItemStackWrapper getItemHandler() {
        int slots = ModConfig.inventoryRows.get() * 9;
        return new OffsetItemStackWrapper(this.containers, this.page * slots, slots);
    }

    @Override
    protected @NotNull IItemHandler createUnSidedHandler() {
        return this.getItemHandler();
    }

}
