package committee.nova.mods.avaritia.api.common.wrapper;

import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.util.StorageUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import lombok.Getter;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 22:33
 * @Description: 多页容器IItemHandler
 */
public abstract class OffsetItemStackWrapper implements IItemHandlerModifiable {

    public OffsetItemStackWrapper() {
    }

    public static OffsetItemStackWrapper create(final Int2ObjectMap<StorageItem> containers, final IntSupplier offset, final IntSupplier length) {
        return new OffsetItemStackWrapper() {
            protected Int2ObjectMap<StorageItem> getContainers() {
                return containers;
            }

            protected int getOffset() {
                return offset.getAsInt();
            }

            public int getSlots() {
                return length.getAsInt();
            }
        };
    }

    public static OffsetItemStackWrapper create(final Int2ObjectMap<StorageItem> containers, final int offset, final int length) {
        return new OffsetItemStackWrapper() {
            protected Int2ObjectMap<StorageItem> getContainers() {
                return containers;
            }

            protected int getOffset() {
                return offset;
            }

            public int getSlots() {
                return length;
            }
        };
    }

    public static OffsetItemStackWrapper create(Int2ObjectMap<StorageItem> containers) {
        IntIterator it = containers.keySet().iterator();

        int slots;
        for(slots = -1; it.hasNext(); slots = Math.max(slots, it.nextInt())) {
        }

        return create(containers, 0, slots + 1);
    }

    public static OffsetItemStackWrapper dummy(final int length) {
        final Int2ObjectMap<StorageItem> containers = StorageUtils.newContainers();
        return new OffsetItemStackWrapper() {
            protected Int2ObjectMap<StorageItem> getContainers() {
                return containers;
            }

            protected int getOffset() {
                return 0;
            }

            public int getSlots() {
                return length;
            }
        };
    }

    protected abstract Int2ObjectMap<StorageItem> getContainers();

    protected abstract int getOffset();

    public abstract int getSlots();

    public StorageItem getContainerInSlot(int slot) {
        return this.getContainers().get(this.getOffset() + slot);
    }

    public void setContainerInSlot(int slot, StorageItem container) {
        this.getContainers().put(this.getOffset() + slot, container);
    }

    public StorageItem removeContainerInSlot(int slot) {
        return this.getContainers().remove(this.getOffset() + slot);
    }

    public long getSlotLimitLong(int slot) {
        return ModConfig.slotStackLimit.get();
    }

    public long getSlotFreeSpace(int slot) {
        return this.getSlotLimitLong(slot) - this.getContainerInSlot(slot).getCount();
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            this.removeContainerInSlot(slot);
        } else {
            int size = (int)Math.min(stack.getCount(), this.getSlotLimitLong(slot));
            this.setContainerInSlot(slot, StorageItem.create(stack, size));
        }

    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        StorageItem container = this.getContainerInSlot(slot);
        ItemStack stack = container.getStack();
        int size = (int)Math.min(container.getCount(), stack.getMaxStackSize());
        return ItemHandlerHelper.copyStackWithSize(stack, size);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.isItemValid(slot, stack)) {
            return stack;
        } else {
            StorageItem container = this.getContainerInSlot(slot);
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
                        this.setContainerInSlot(slot, StorageItem.create(stack, toInsert));
                    } else {
                        container.grow(toInsert);
                    }
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
            StorageItem container = this.getContainerInSlot(slot);
            if (container.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                ItemStack stackInSlot = container.getStack();
                long stackCount = container.getCount();
                int toExtract = (int)Math.min(Math.min(amount, stackCount), stackInSlot.getMaxStackSize());
                if (!simulate) {
                    if (stackCount > (long)toExtract) {
                        container.shrink(toExtract);
                    } else {
                        this.removeContainerInSlot(slot);
                    }
                }

                return ItemHandlerHelper.copyStackWithSize(stackInSlot, toExtract);
            }
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return (int)Math.min(Integer.MAX_VALUE, this.getSlotLimitLong(slot));
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
