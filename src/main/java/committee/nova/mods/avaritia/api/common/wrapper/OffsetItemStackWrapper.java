package committee.nova.mods.avaritia.api.common.wrapper;

import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.util.StorageUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 22:33
 * @Description: 多页容器IItemHandler
 */
public class OffsetItemStackWrapper implements IItemHandlerModifiable {

    protected Int2ObjectMap<StorageItem> stacks;
    protected final int slotsPerPage;  // 每页槽位数
    protected final int totalPages;    // 总页数
    protected int page;    // 当前页

    public OffsetItemStackWrapper(int slotsPerPage) {
        this(StorageUtils.newContainers(), 0, slotsPerPage);
    }

    public OffsetItemStackWrapper(Int2ObjectMap<StorageItem> stacks, int page, int slotsPerPage) {
        this.stacks = stacks;
        this.page = page;
        this.slotsPerPage = slotsPerPage;
        this.totalPages = ModConfig.maxPageLimit.get();
    }

    public StorageItem getItemInSlot(int slot) {
        int slotInPage = slot % slotsPerPage;
        return this.stacks.get(page * slotsPerPage + slotInPage);
    }

    public void setItemInSlot(int slot, StorageItem container) {
        int slotInPage = slot % slotsPerPage;
        this.stacks.put(page * slotsPerPage + slotInPage, container);
    }

    public StorageItem removeItemInSlot(int slot) {
        int slotInPage = slot % slotsPerPage;
        return this.stacks.remove(page * slotsPerPage + slotInPage);
    }

    public long getSlotLimitLong(int slot) {
        return ModConfig.slotStackLimit.get();
    }

    public long getSlotFreeSpace(int slot) {
        return this.getSlotLimitLong(slot) - this.getItemInSlot(slot).getCount();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            this.removeItemInSlot(slot);
        } else {
            int size = (int) Math.min(stack.getCount(), this.getSlotLimitLong(slot));
            this.setItemInSlot(slot, StorageItem.create(stack, size));
        }
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        Static.LOGGER.info(slot);
        StorageItem container = this.getItemInSlot(slot);
        return ItemHandlerHelper.copyStackWithSize(container.getStack(), (int) container.getCount());
    }

    @Override
    public int getSlots() {
        int maxSlot = stacks.keySet().intStream().max().orElse(0);
        //Static.LOGGER.info(maxSlot + 1);
        if (maxSlot >= slotsPerPage) {
          int slots =  Mth.ceil((float) maxSlot / (float) slotsPerPage) * slotsPerPage;
          Static.LOGGER.info(slots);
          return slots;
        }
        else
            return slotsPerPage;
    }


    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.isItemValid(slot, stack)) {
            return stack;
        } else {
            StorageItem container = this.getItemInSlot(slot);
            ItemStack stackInSlot = container.getStack();
            long limit = this.getSlotLimitLong(slot);
            if (!container.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                    return stack;
                }

                limit -= container.getCount();
            }

            if (limit <= 0L) {
                return stack;
            } else {
                int toInsert = (int)Math.min(stack.getCount(), limit);
                if (!simulate) {
                    if (container.isEmpty()) {
                        this.setItemInSlot(slot, StorageItem.create(stack, toInsert));
                    } else {
                        container.growCount(toInsert);
                    }
                    onContentsChanged(slot);
                }

                return ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - toInsert);
            }
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            StorageItem container = this.getItemInSlot(slot);
            if (container.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack stackInSlot = container.getStack();
                long stackCount = container.getCount();
                int toExtract = (int)Math.min(Math.min(amount, stackCount), stackInSlot.getMaxStackSize());
                if (!simulate) {
                    if (stackCount > (long)toExtract) {
                        container.shrinkCount(toExtract);
                    } else {
                        this.removeItemInSlot(slot);
                    }
                    onContentsChanged(slot);
                }
                return ItemHandlerHelper.copyStackWithSize(stackInSlot, toExtract);
            }
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int)Math.min(Integer.MAX_VALUE , this.getSlotLimitLong(slot));
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack)
    {
        return true;
    }

    protected void onContentsChanged(int slot) {}
}
