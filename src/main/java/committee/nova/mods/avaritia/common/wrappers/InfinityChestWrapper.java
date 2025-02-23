package committee.nova.mods.avaritia.common.wrappers;

import committee.nova.mods.avaritia.api.common.wrapper.BaseItemWrapper;
import committee.nova.mods.avaritia.common.container.StoredItemStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/2/4 15:00
 * @Description:
 */
public class InfinityChestWrapper implements BaseItemWrapper {
    public final Int2ObjectArrayMap<StoredItemStack> storageItems = new Int2ObjectArrayMap<>();
    @Override
    public int getSlots() {
        return storageItems.size();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        ItemStack itemStack = storageItems.get(slot).getStack();
        itemStack.setCount((int) Math.min(Integer.MAX_VALUE, storageItems.get(slot).getQuantity()));
        return itemStack;
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.storageItems.put(slot, new StoredItemStack(stack));
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || stack.hasTag()) return stack;
        ItemStack remainingStack = ItemStack.EMPTY;
        StoredItemStack storedItemStack = new StoredItemStack(stack);
        if (storageItems.get(slot).getStack().is(stack.getItem())
                && storageItems.get(slot).getQuantity() < Long.MAX_VALUE
        ) {
            long storageCount = storageItems.get(slot).getQuantity();
            long remainingSpaces = Long.MAX_VALUE - storageCount;
            if (remainingSpaces >= stack.getCount()) {
                storedItemStack.setCount(storageCount + stack.getCount());
                if (!simulate) storageItems.replace(slot, storedItemStack);
            } else {
                storedItemStack.setCount( Long.MAX_VALUE);
                if (!simulate) storageItems.replace(slot, storedItemStack);
                remainingStack = stack.copy();
                remainingStack.setCount(stack.getCount() - (int) remainingSpaces);
            }
        } else {
            storedItemStack.setCount(stack.getCount());
            if (!simulate) {
                storageItems.put(slot, storedItemStack);
            }
        }
        return remainingStack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (storageItems.get(slot).getStack().isEmpty() || storageItems.get(slot).getQuantity() == 0) return ItemStack.EMPTY;
        StoredItemStack storedItemStack = storageItems.get(slot);
        ItemStack itemStack = new ItemStack(storedItemStack.getStack().getItemHolder(), 1);
        int count = Math.min(itemStack.getMaxStackSize(), amount);
        long storageCount = storedItemStack.getQuantity();
        if (count < storageCount) {
            if (!simulate) {
                storedItemStack.setCount(storageCount - count);
                storageItems.replace(slot, storedItemStack);
            }
        } else {
            if (!simulate) {
                storageItems.remove(slot);
            }
            count = (int) storageCount;
        }
        itemStack.setCount(count);
        return itemStack;
    }

    @Override
    public int getSlotLimit(int slot) {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return !stack.isEmpty() && !stack.hasTag();
    }


    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag items = new CompoundTag();
        storageItems.forEach((slot, storedItemStack) -> {
            CompoundTag storedItemTag = new CompoundTag();
            storedItemTag.put("Stack", storedItemStack.getStack().serializeNBT());
            storedItemTag.putLong("Count", storedItemStack.getQuantity());
            items.put(String.valueOf(slot), storedItemTag);
        });
        CompoundTag data = new CompoundTag();
        data.put("items", items);
        return data;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        if (nbt.contains("items")) {
            CompoundTag items = nbt.getCompound("items");
            items.getAllKeys().forEach(itemId -> {
                if (Integer.parseInt(itemId) >= 0) {
                    StoredItemStack storedItemStack = new StoredItemStack(ItemStack.of(items.getCompound(itemId).getCompound("Stack")));
                    storedItemStack.setCount(items.getCompound(itemId).getLong("Count"));
                    storageItems.put(Integer.parseInt(itemId), storedItemStack);
                }
            });
        }
    }


}
