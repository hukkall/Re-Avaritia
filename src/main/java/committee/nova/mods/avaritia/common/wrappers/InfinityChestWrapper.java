//package committee.nova.mods.avaritia.common.wrappers;
//
//import committee.nova.mods.avaritia.api.common.wrapper.BaseItemWrapper;
//import committee.nova.mods.avaritia.common.container.StoredItemStack;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.world.item.ItemStack;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
///**
// * @Project: Avaritia
// * @Author: cnlimiter
// * @CreateTime: 2025/2/4 15:00
// * @Description:
// */
//public class InfinityChestWrapper implements BaseItemWrapper {
//    public final List<StoredItemStack> storageItems = new CopyOnWriteArrayList<>();
//    @Override
//    public int getSlots() {
//        return storageItems.size();
//    }
//
//    @Override
//    public @NotNull ItemStack getStackInSlot(int slot) {
//        ItemStack itemStack = storageItems.get(slot).getStack();
//        itemStack.setCount((int) Math.min(Integer.MAX_VALUE, storageItems.get(slot).getCount()));
//        return itemStack;
//    }
//
//    @Override
//    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
//        this.storageItems.add(slot, new StoredItemStack(stack));
//    }
//
//    @Override
//    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
//        if (stack.isEmpty() || stack.hasTag()) return stack;
//        ItemStack remainingStack = ItemStack.EMPTY;
//        StoredItemStack storedItemStack = new StoredItemStack(stack);
//        if (storageItems.get(slot).getStack().is(stack.getItem())
//                && storageItems.get(slot).getCount() < Long.MAX_VALUE
//        ) {
//            long storageCount = storageItems.get(slot).getCount();
//            long remainingSpaces = Long.MAX_VALUE - storageCount;
//            if (remainingSpaces >= stack.getCount()) {
//                storedItemStack.setCount(storageCount + stack.getCount());
//                if (!simulate) storageItems.add(slot, storedItemStack);
//            } else {
//                storedItemStack.setCount( Long.MAX_VALUE);
//                if (!simulate) storageItems.add(slot, storedItemStack);
//                remainingStack = stack.copy();
//                remainingStack.setCount(stack.getCount() - (int) remainingSpaces);
//            }
//        } else {
//            storedItemStack.setCount(stack.getCount());
//            if (!simulate) {
//                storageItems.add(storedItemStack);
//            }
//        }
//        return remainingStack;
//    }
//
//    public StoredItemStack addItem(StoredItemStack pStack) {
//        if (storageItems.isEmpty()) {
//            storageItems.add(pStack);
//            return pStack;
//        } else {
//            if (!pStack.isEmpty()){
//                if (this.storedContains(pStack) >= 0){
//                    StoredItemStack storedItemStack = this.storageItems.get(this.storedContains(pStack));
//
//                        long remianingSpaces = Long.MAX_VALUE - storedItemStack.getCount();
//                        if (remianingSpaces >= pStack.getCount()) {
//                            storedItemStack.grow(pStack.getCount());
//                            this.storageItems.add(storedItemStack);
//                            return storedItemStack;
//                        } else {
//                            storedItemStack.setCount(Long.MAX_VALUE);
//                            this.storageItems.add(storedItemStack);
//                            long remaining = pStack.getCount() - remianingSpaces;
//                            StoredItemStack leftStack = new StoredItemStack(pStack.getStack(), remaining);
//                            this.storageItems.add(leftStack);
//                            return leftStack;
//                        }
//
//                } else {
//                    this.storageItems.add(pStack);
//                    return pStack;
//                }
//            } else {
//                return StoredItemStack.EMPTY;
//            }
//        }
//    }
//
//    private int storedContains(StoredItemStack stack) {
//        for (int i = 0; i < storageItems.size(); i++) {
//            if (storageItems.get(i).getStack().is(stack.getStack().getItem()) &&
//                    storageItems.get(i).getCount() < Long.MAX_VALUE &&
//                    storageItems.get(i).getCount() >= stack.getCount()
//            ) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    @Override
//    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
//        if (storageItems.get(slot).getStack().isEmpty() || storageItems.get(slot).getCount() == 0) return ItemStack.EMPTY;
//        StoredItemStack storedItemStack = storageItems.get(slot);
//        ItemStack itemStack = new ItemStack(storedItemStack.getStack().getItemHolder(), 1);
//        int count = Math.min(itemStack.getMaxStackSize(), amount);
//        long storageCount = storedItemStack.getCount();
//        if (count < storageCount) {
//            if (!simulate) {
//                storedItemStack.setCount(storageCount - count);
//                storageItems.add(slot, storedItemStack);
//            }
//        } else {
//            if (!simulate) {
//                storageItems.remove(slot);
//            }
//            count = (int) storageCount;
//        }
//        itemStack.setCount(count);
//        return itemStack;
//    }
//
//    @Override
//    public int getSlotLimit(int slot) {
//        return Integer.MAX_VALUE;
//    }
//
//    @Override
//    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
//        return !stack.isEmpty() && !stack.hasTag();
//    }
//
//
//    @Override
//    public CompoundTag serializeNBT()
//    {
//        CompoundTag items = new CompoundTag();
//        storageItems.forEach((storedItemStack) -> {
//            CompoundTag storedItemTag = new CompoundTag();
//            storedItemTag.put("Stack", storedItemStack.getStack().serializeNBT());
//            storedItemTag.putLong("Count", storedItemStack.getCount());
//            items.put(String.valueOf(storageItems.indexOf(storedItemStack)), storedItemTag);
//        });
//        CompoundTag data = new CompoundTag();
//        data.put("items", items);
//        return data;
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag nbt)
//    {
//        if (nbt.contains("items")) {
//            CompoundTag items = nbt.getCompound("items");
//            items.getAllKeys().forEach(itemId -> {
//                if (Integer.parseInt(itemId) >= 0) {
//                    StoredItemStack storedItemStack = new StoredItemStack(ItemStack.of(items.getCompound(itemId).getCompound("Stack")));
//                    storedItemStack.setCount(items.getCompound(itemId).getLong("Count"));
//                    storageItems.add(Integer.parseInt(itemId), storedItemStack);
//                }
//            });
//        }
//    }
//
//
//}
