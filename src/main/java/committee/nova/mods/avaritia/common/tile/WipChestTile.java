package committee.nova.mods.avaritia.common.tile;

import committee.nova.mods.avaritia.api.common.tile.BaseInventoryTileEntity;
import committee.nova.mods.avaritia.api.common.tile.BaseTileEntity;
import committee.nova.mods.avaritia.api.common.wrapper.BaseItemWrapper;
import committee.nova.mods.avaritia.api.utils.InventoryUtils;
import committee.nova.mods.avaritia.api.utils.ItemUtils;
import committee.nova.mods.avaritia.common.block.chest.InfinityChestBlock;
import committee.nova.mods.avaritia.common.container.StoredItemStack;
import committee.nova.mods.avaritia.common.menu.WipChestMenu;
import committee.nova.mods.avaritia.common.tile.collector.BaseNeutronCollectorTile;
import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper;
import committee.nova.mods.avaritia.init.registry.ModTileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 15:28
 * @Description:
 */
public class WipChestTile extends BaseInventoryTileEntity implements MenuProvider {
    private static final Component CONTAINER_NAME = Component.translatable("container.infinity_chest");
    private Map<StoredItemStack, Long> items = new HashMap<>();
    private int sort;
    private String lastSearch = "";
    private boolean updateItems;
    public WipChestTile(BlockPos pos, BlockState state) {
        super(ModTileEntities.infinity_chest_tile.get(), pos, state);
    }

    @Override
    protected AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory) {
        return new WipChestMenu(pContainerId, pInventory, this);
    }

    @Override
    public @NotNull BaseItemWrapper getInventory() {
        return new InfinityChestWrapper();
    }


    @Override
    public @NotNull Component getDisplayName() {
        return CONTAINER_NAME;
    }


    public Map<StoredItemStack, Long> getStacks() {
        updateItems = true;
        return items;
    }

    public StoredItemStack pullStack(StoredItemStack stack, long max) {
        if(stack != null && max > 0) {
            ItemStack st = stack.getStack();
            StoredItemStack ret = null;
            for (int i = getInventory().getSlots() - 1; i >= 0; i--) {
                ItemStack s = getInventory().getStackInSlot(i);
                if(ItemStack.isSameItemSameTags(s, st)) {
                    ItemStack pulled = getInventory().extractItem(i, (int) max, false);
                    if(!pulled.isEmpty()) {
                        if(ret == null)ret = new StoredItemStack(pulled);
                        else ret.grow(pulled.getCount());
                        max -= pulled.getCount();
                        if(max < 1)break;
                    }
                }
            }
            return ret;
        }
        return null;
    }

    public StoredItemStack pushStack(StoredItemStack stack) {
        if(stack != null) {
            ItemStack is = ItemHandlerHelper.insertItemStacked(getInventory(), stack.getActualStack(), false);
            if(is.isEmpty())return null;
            else {
                return new StoredItemStack(is);
            }
        }
        return stack;
    }

    public ItemStack pushStack(ItemStack itemstack) {
        StoredItemStack is = pushStack(new StoredItemStack(itemstack));
        return is == null ? ItemStack.EMPTY : is.getActualStack();
    }

    public void pushOrDrop(ItemStack st) {
        if(st.isEmpty())return;
        StoredItemStack st0 = pushStack(new StoredItemStack(st));
        if(st0 != null) {
            Containers.dropItemStack(level, worldPosition.getX() + .5f, worldPosition.getY() + .5f, worldPosition.getZ() + .5f, st0.getActualStack());
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, WipChestTile tile) {
        if(tile.updateItems) {
            IntStream.range(0, tile.getInventory().getSlots()).mapToObj(tile.getInventory()::getStackInSlot).filter(s -> !s.isEmpty()).
                    map(StoredItemStack::new).forEach(s -> tile.items.merge(s, s.getQuantity(), Long::sum));

            tile.updateItems = false;
        }
    }


    public int getSorting() {
        return sort;
    }

    public void setSorting(int newC) {
        sort = newC;
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("sort", sort);
    }

    @Override
    public void load(@NotNull CompoundTag compound) {
        super.load(compound);
        sort = compound.getInt("sort");
    }

    public String getLastSearch() {
        return lastSearch;
    }

    public void setLastSearch(String string) {
        lastSearch = string;
    }

    public static enum TerminalPos implements StringRepresentable {
        CENTER("center"),
        UP("up"),
        DOWN("down")
        ;
        private String name;
        private TerminalPos(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }
}
