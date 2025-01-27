package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.common.container.InfinityChestContainer;
import committee.nova.mods.avaritia.common.menu.InfinityChestMenu;
import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper;
import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.util.StorageUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/17 02:36
 * @Description:
 */
public class InfinityChestTile extends BaseContainerBlockEntity implements MenuProvider, InfinityChestContainer {
    private final ContainerData chestData = new ContainerData() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return InfinityChestTile.this.maxPage;
            } else {
                return index == 1 ? InfinityChestTile.this.page : 0;
            }
        }
        @Override
        public void set(int index, int value) {
            if (index == 0) {
                InfinityChestTile.this.maxPage = value;
            }

            if (index == 1) {
                InfinityChestTile.this.page = value;
            }

        }
        @Override
        public int getCount() {
            return 2;
        }
    };

    private final Int2ObjectMap<StorageItem> containers = StorageUtils.newContainers();
    private int maxPage = 1;
    private int page = 0;
    static final Component CONTAINER_NAME = Component.translatable("container.infinity_chest");

    protected InfinityChestTile(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    @Override
    public InfinityChestWrapper getItemHandler() {
        int slots = ModConfig.inventoryRows.get() * 9;
        return InfinityChestWrapper.create(this.containers, () -> this.page * slots, () -> slots);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return CONTAINER_NAME;
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pInventory) {
        return new InfinityChestMenu(pContainerId, pInventory, this, this.chestData);
    }



    @Override
    public boolean stillValid(@NotNull Player pPlayer) {
        Level world = this.getLevel();
        BlockPos pos = this.getBlockPos();
        if (world != null && world.getBlockEntity(pos) == this) {
            return !(pPlayer.distanceToSqr((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5) > 64.0);
        } else {
            return false;
        }
    }


    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.containers.clear();
        StorageUtils.loadAllItems(pTag, this.containers);
        this.maxPage = pTag.getInt("MaxPage");
        this.page = pTag.getInt("Page");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        StorageUtils.saveAllItems(pTag, this.containers);
        pTag.putInt("MaxPage", this.maxPage);
        pTag.putInt("Page", this.page);
    }

    @Override
    protected @NotNull IItemHandler createUnSidedHandler() {
        return this.getItemHandler();
    }

}
