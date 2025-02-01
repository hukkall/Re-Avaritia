package committee.nova.mods.avaritia.api.common.wrapper;

import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.util.StorageUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 22:33
 * @Description:
 */
public class OffsetItemStackWrapper implements IItemHandler, IItemHandlerModifiable {

    protected Int2ObjectMap<StorageItem> stacks;
    protected int offset;
    protected int slots;

    public OffsetItemStackWrapper(int slots)
    {
        this(0, slots);
    }

    public OffsetItemStackWrapper(int offset, int slots)
    {
        this.stacks = new Int2ObjectOpenHashMap<>();
        this.stacks.defaultReturnValue(StorageItem.EMPTY);
        this.offset = offset;
        this.slots = slots;
    }

    public OffsetItemStackWrapper(Int2ObjectMap<StorageItem> stacks, int offset, int slots)
    {
        this.stacks = stacks;
        this.offset = offset;
        this.slots = slots;
    }

    public StorageItem getContainerInSlot(int slot) {
        return this.stacks.get(this.offset + slot);
    }

    public void setContainerInSlot(int slot, StorageItem container) {
        this.stacks.put(this.offset + slot, container);
    }

    public StorageItem removeContainerInSlot(int slot) {
        return this.stacks.remove(this.offset + slot);
    }

    public long getSlotLimitLong(int slot) {
        return ModConfig.slotStackLimit.get();
    }

    public long getSlotFreeSpace(int slot) {
        return this.getSlotLimitLong(slot) - this.getContainerInSlot(slot).getCount();
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        if (stack.isEmpty()) {
            this.removeContainerInSlot(slot);
        } else {
            int size = (int) Math.min(stack.getCount(), this.getSlotLimitLong(slot));
            this.setContainerInSlot(slot, StorageItem.create(stack, size));
        }
    }

    @Override
    public int getSlots() {
        return this.slots;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        StorageItem container = this.getContainerInSlot(slot);
        ItemStack stack = container.getStack();
        return ItemHandlerHelper.copyStackWithSize(stack, (int) container.getCount());
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
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
            StorageItem container = this.getContainerInSlot(slot);
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
                        this.removeContainerInSlot(slot);
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
