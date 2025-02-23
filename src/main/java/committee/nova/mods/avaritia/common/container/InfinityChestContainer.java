//package committee.nova.mods.avaritia.common.container;
//
//import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper;
//import committee.nova.mods.avaritia.util.StorageUtils;
//import committee.nova.mods.avaritia.util.StorageUtils.*;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.Container;
//import net.minecraft.world.ContainerHelper;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.entity.player.StackedContents;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraftforge.registries.ForgeRegistries;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @Project: Avaritia
// * @Author: cnlimiter
// * @CreateTime: 2025/2/4 14:47
// * @Description:
// */
//public class InfinityChestContainer extends SimpleContainer {
//    public InfinityChestWrapper channel;
//    public ArrayList<ItemStack> viewingItems = new ArrayList<>();
//    public ArrayList<String> formatCount = new ArrayList<>();
//    public ArrayList<ItemStack> sortedItems = new ArrayList<>();
//    private double scrollTo = 0.0D;
//
//    public Level level;
//    public String filter;
//    public byte sortType;
//    public boolean LShifting = false;
//
//    public InfinityChestContainer(InfinityChestWrapper channel, Level level, String filter, byte sortType) {
//        super(99);
//        this.channel = channel;
//        this.level = level;
//        this.filter = filter;
//        this.sortType = sortType;
//    }
//
//    public List<ItemStack> getItems() {
//        return this.channel.storageItems.values().stream().map(StoredItemStack::getStack).collect(Collectors.toList());
//    }
//
//    public void onScrollTo(double scrollTo) {
//        this.scrollTo = scrollTo;
//        scrollOffset(0);
//    }
//
//    public double getScrollOn() {
//        return scrollTo;
//    }
//
//    public void scrollOffset(int offset) {
//        if (sortedItems.size() <= 99) {
//            viewingItems.clear();
//            viewingItems.addAll(sortedItems);
//        }
//        else {
//            int i = (int) Math.ceil(sortedItems.size() / 11.0D);
//            i -=  9;
//            int j = Math.round(i * (float) scrollTo);
//            if (offset != 0) {
//                j += offset;
//                j = Math.max(0, Math.min(i, j));
//                scrollTo = (double) j / (double) i;
//            }
//            viewingItems.clear();
//            viewingItems.addAll(sortedItems.subList(j * 11, Math.min(sortedItems.size(), j * 11 + 99)));
//        }
//        updateDummySlots(true);
//    }
//
//    public double onMouseScrolled(boolean isUp) {
//        if (isUp) scrollOffset(-1);
//        else scrollOffset(1);
//        return scrollTo;
//    }
//
//    public void refreshContainer(boolean fullUpdate) {
//        if (!level.isClientSide) return;
//        if ((fullUpdate || sortType >= 6) && !LShifting) {
//            sortedItems = new ArrayList<>(channel.storageItems.keySet());
//            if (!filter.isEmpty()) {
//                ArrayList<ItemStack> temp = new ArrayList<>();
//                char head = filter.charAt(0);
//                if (head == '*') {
//                    String s = filter.substring(1);
//                    for (ItemStack itemName : sortedItems) if (itemName.is(StorageUtils.getItem(s))) temp.add(itemName);
//                }
//                else if (head == '$') {
//                    String s = filter.substring(1);
//                    for (ItemStack itemName : sortedItems) {
//                        ArrayList<String> tags = new ArrayList<>();
//                        itemName.getTags().forEach(itemTagKey -> tags.add(itemTagKey.location().getPath()));
//                        for (String tag : tags) {
//                            if (tag.contains(s)) {
//                                temp.add(itemName);
//                                break;
//                            }
//                        }
//                    }
//                }
//                else {
//                    for (ItemStack itemName : sortedItems) {
//                        if (StorageUtils.getItemId(itemName.getItem()).contains(filter)) temp.add(itemName);
//                        else {
//                            if (itemName.getDisplayName().getString().toLowerCase().contains(filter)) temp.add(itemName);
//                        }
//                    }
//                }
//                sortedItems = temp;
//            }
//            switch (sortType) {
//                case Sort.ID_ASCENDING -> sortedItems.sort(StorageUtils::sortFromRightID);
//                case Sort.ID_DESCENDING -> sortedItems.sort(Collections.reverseOrder(StorageUtils::sortFromRightID));
//                //case Sort.NAMESPACE_ID_ASCENDING -> sortedItems.sort(ItemStack::compareTo);
//                //case Sort.NAMESPACE_ID_DESCENDING -> sortedItems.sort(Collections.reverseOrder(String::compareTo));
//                case Sort.MIRROR_ID_ASCENDING -> sortedItems.sort(StorageUtils::sortFromMirrorID);
//                case Sort.MIRROR_ID_DESCENDING -> sortedItems.sort(Collections.reverseOrder(StorageUtils::sortFromMirrorID));
//                case Sort.COUNT_ASCENDING -> sortedItems.sort((s1, s2) -> StorageUtils.sortFromCount(s1, s2, channel.storageItems, false));
//                case Sort.COUNT_DESCENDING -> sortedItems.sort((s1, s2) -> StorageUtils.sortFromCount(s1, s2, channel.storageItems, true));
//            }
//            return;
//        }
//        updateDummySlots(fullUpdate);
//    }
//
//    public void updateDummySlots(boolean fullUpdate) {
//        formatCount.clear();
//        for (int j = 0; j < 99; j++) {
//            if (j < viewingItems.size() && viewingItems.get(j) != null) {
//                ItemStack id = viewingItems.get(j);
//                {
//                    //叠堆数为1避开原版的数字渲染
//                    if (fullUpdate) this.setItem(j, id);
//                    long count;
//
//                        if (channel.storageItems.containsKey(id)) {
//                            count = channel.storageItems.get(id);
//                        }
//                        else {
//                            formatCount.add(j, "§c0");
//                            continue;
//                        }
//
//                    if (count < 1000L) formatCount.add(j, String.valueOf(count));
//                    else if (count < Long.MAX_VALUE) {
//                        String stringCount = StorageUtils.DECIMAL_FORMAT.format(count);
//                        stringCount = stringCount.substring(0, 4);
//                        if (stringCount.endsWith(",")) stringCount = stringCount.substring(0, 3);
//                        stringCount = stringCount.replace(",", ".");
//                        if (count < 1000000L) stringCount += "K";
//                        else if (count < 1000000000L) stringCount += "M";
//                        else if (count < 1000000000000L) stringCount += "G";
//                        else if (count < 1000000000000000L) stringCount += "T";
//                        else if (count < 1000000000000000000L) stringCount += "P";
//                        else stringCount += "E";
//                        formatCount.add(j, stringCount);
//                        // 9,223,372,036,854,775,807L
//                        // e  p   t   g   m   k
//                    }
//                    else formatCount.add(j, "MAX");
//                }
//            }
//            else this.setItem(j, ItemStack.EMPTY);
//        }
//    }
//
//    public void nextSort() {
//        sortType += 2;
//        if (sortType > 7) sortType %= 8;
//        if (level.isClientSide) refreshContainer(true);
//    }
//
//    public void reverseSort() {
//        if (sortType % 2 == 0) sortType++;
//        else sortType--;
//        if (level.isClientSide) refreshContainer(true);
//    }
//
//    @Override
//    public void setItem(int pIndex, @NotNull ItemStack pStack) {
//       this.channel.setStackInSlot(pIndex, pStack);
//    }
//
//    @Override
//    public @NotNull ItemStack getItem(int pIndex) {
//        return this.channel.getStackInSlot(pIndex);
//    }
//
//    /**
//     * @param itemStack 会被修改，塞不进去会有余，
//     * @return 存进去的量
//     */
//    @Override
//    public @NotNull ItemStack addItem(ItemStack itemStack) {
//        if (itemStack.hasTag() || itemStack.isEmpty()) return ItemStack.EMPTY;
//        if (this.channel.storageItems.containsKey(itemStack)) {
//            long storageCount = this.channel.storageItems.get(itemStack);
//            long remainingSpaces = Long.MAX_VALUE - storageCount;
//            if (remainingSpaces >= itemStack.getCount()) {
//                this.channel.storageItems.replace(itemStack, storageCount + itemStack.getCount());
//                itemStack.setCount(0);
//                this.channel.onItemChanged(false);
//                this.setChanged();
//                return itemStack;
//            } else {
//                this.channel.storageItems.replace(itemStack, Long.MAX_VALUE);
//                itemStack.setCount(itemStack.getCount() - (int) remainingSpaces);
//                this.channel.onItemChanged(false);
//                this.setChanged();
//                return itemStack;
//            }
//        } else {
//            this.channel.storageItems.put(itemStack, (long) itemStack.getCount());
//            itemStack.setCount(0);
//            this.channel.onItemChanged(true);
//            this.setChanged();
//            return itemStack;
//        }
//    }
//
//
//    @Override
//    public @NotNull ItemStack removeItem(int pIndex, int pCount) {
//            ItemStack itemstack = ContainerHelper.removeItem(this.getItems(), pIndex, pCount);
//            if (!itemstack.isEmpty()) {
//                this.setChanged();
//            }
//            return itemstack;
//
//    }
//
//
//
//
//    public boolean hasItem(ItemStack item) { return this.channel.storageItems.containsKey(item); }
//
//    public long getRealStorageAmount(ItemStack item) {
//        return this.channel.storageItems.getOrDefault(item, 0L);
//    }
//
//    public int getStorageAmount(ItemStack item) {
//        return (int) Long.min(Integer.MAX_VALUE, this.channel.storageItems.getOrDefault(item, 0L));
//    }
//
//    public int canStorageAmount(ItemStack itemStack) {
//        if (itemStack.hasTag()) return 0;
//        long a = this.channel.storageItems.getOrDefault(itemStack, 0L);
//        if (a == 0L) return Integer.MAX_VALUE;
//        return (int) Math.min(Integer.MAX_VALUE, Long.MAX_VALUE - a);
//    }
//
//
//    public int canStorageItemAmount(ItemStack item) {
//        long a = this.channel.storageItems.getOrDefault(item, 0L);
//        if (a == 0L) return Integer.MAX_VALUE;
//        return (int) Math.min(Integer.MAX_VALUE, Long.MAX_VALUE - a);
//    }
//
//
//    @Override
//    public boolean canAddItem(@NotNull ItemStack pStack) {
//        if (this.channel.storageItems.containsKey(pStack)) {
//            return this.channel.storageItems.get(pStack) < Long.MAX_VALUE;
//        } else return true;
//    }
//
//    @Override
//    public boolean canPlaceItem(int pIndex, @NotNull ItemStack pStack) {
//        return super.canPlaceItem(pIndex, pStack);
//    }
//
//    @Override
//    public boolean canTakeItem(@NotNull Container pTarget, int pIndex, @NotNull ItemStack pStack) {
//        return super.canTakeItem(pTarget, pIndex, pStack);
//    }
//
//    @Override
//    public void fillStackedContents(@NotNull StackedContents pHelper) {
//        super.fillStackedContents(pHelper);
//    }
//
//    /**
//     * @return 成功进入的
//     */
//    public long addItem(ItemStack itemId, long count) {
//        if (itemId.isEmpty() || count == 0) return 0L;
//        if (this.channel.storageItems.containsKey(itemId)) {
//            long storageCount = this.channel.storageItems.get(itemId);
//            long remainingSpaces = Long.MAX_VALUE - storageCount;
//            if (remainingSpaces >= count) {
//                this.channel.storageItems.replace(itemId, storageCount + count);
//                this.channel.onItemChanged(false);
//                return count;
//            } else {
//                this.channel.storageItems.replace(itemId, Long.MAX_VALUE);
//                this.channel.onItemChanged(false);
//                return remainingSpaces;
//            }
//        } else {
//            this.channel.storageItems.put(itemId, count);
//            this.channel.onItemChanged(true);
//            return count;
//        }
//    }
//
//
//    /**
//     * 填充物品叠堆，不限制数量。
//     *
//     * @param itemStack 要填充的物品
//     * @param count     要填充的数量，负数为扣除。
//     */
//    public void fillItemStack(ItemStack itemStack, int count) {
//        if (itemStack.isEmpty() || count == 0 || itemStack.hasTag()) return;
//        if (this.channel.storageItems.containsKey(itemStack)) {
//            long storageCount = this.channel.storageItems.get(itemStack);
//            long remainingSpaces = Long.MAX_VALUE - storageCount;
//            if (count >= storageCount) {
//                this.channel.storageItems.remove(itemStack);
//                itemStack.setCount(itemStack.getCount() + (int) storageCount);
//                this.channel.onItemChanged(true);
//            } else if (remainingSpaces < -count) {
//                this.channel.storageItems.replace(itemStack, Long.MAX_VALUE);
//                itemStack.setCount(itemStack.getCount() - (int) remainingSpaces);
//                this.channel.onItemChanged(false);
//            } else {
//                this.channel.storageItems.replace(itemStack, storageCount - count);
//                itemStack.setCount(itemStack.getCount() + count);
//                this.channel.onItemChanged(false);
//            }
//        } else {
//            if (count < 0) {
//                this.channel.storageItems.put(itemStack, (long) -count);
//                itemStack.setCount(itemStack.getCount() + count);
//                this.channel.onItemChanged(true);
//            }
//        }
//    }
//
//
//    /**
//     * 从频道获取物品，但不限制数量。
//     */
//    public ItemStack takeItem(ItemStack itemId, int count) {
//        if (!this.channel.storageItems.containsKey(itemId) || itemId.isEmpty() || count == 0) return ItemStack.EMPTY;
//        long storageCount = this.channel.storageItems.get(itemId);
//        if (count < storageCount) {
//            this.channel.storageItems.replace(itemId, storageCount - count);
//            this.channel.onItemChanged(false);
//        } else {
//            this.channel.storageItems.remove(itemId);
//            count = (int) storageCount;
//            this.channel.onItemChanged(true);
//        }
//        return new ItemStack(itemId.getItemHolder(), count);
//    }
//
//    /**
//     * 从频道获取物品，数量限制在叠堆最大值。
//     */
//    public ItemStack saveTakeItem(ItemStack itemId, int count) {
//        if (!this.channel.storageItems.containsKey(itemId) || itemId.isEmpty() || count == 0) return ItemStack.EMPTY;
//        ItemStack itemStack = new ItemStack(itemId.getItemHolder(), 1);
//        count = Integer.min(count, itemStack.getMaxStackSize());
//        long storageCount = this.channel.storageItems.get(itemId);
//        if (count < storageCount) {
//            this.channel.storageItems.replace(itemId, storageCount - count);
//            this.channel.onItemChanged(false);
//        } else {
//            this.channel.storageItems.remove(itemId);
//            count = (int) storageCount;
//            this.channel.onItemChanged(true);
//        }
//        itemStack.setCount(count);
//        return itemStack;
//    }
//
//    public ItemStack saveTakeItem(ItemStack itemId, boolean half) {
//        if (!this.channel.storageItems.containsKey(itemId)) return ItemStack.EMPTY;
//        ItemStack itemStack = new ItemStack(itemId.getItemHolder(), 1);
//        int count = half ? (itemStack.getMaxStackSize() + 1) / 2 : itemStack.getMaxStackSize();
//        long storageCount = this.channel.storageItems.get(itemId);
//        if (count < storageCount) {
//            this.channel.storageItems.replace(itemId, storageCount - count);
//            this.channel.onItemChanged(false);
//        } else {
//            this.channel.storageItems.remove(itemId);
//            count = (int) storageCount;
//            this.channel.onItemChanged(true);
//        }
//        itemStack.setCount(count);
//        return itemStack;
//    }
//
//    public void removeItem(ItemStack itemStack) {
//        if (itemStack.isEmpty()) return;
//        if (!this.channel.storageItems.containsKey(itemStack)) return;
//        long storageCount = this.channel.storageItems.get(itemStack);
//        if (itemStack.getCount() < storageCount) {
//            this.channel.storageItems.replace(itemStack, storageCount - itemStack.getCount());
//            this.channel.onItemChanged(false);
//        } else {
//            this.channel.storageItems.remove(itemStack);
//            this.channel.onItemChanged(true);
//        }
//    }
//
//    public void removeItem(ItemStack itemId, long count) {
//        if (!this.channel.storageItems.containsKey(itemId)) return;
//        long storageCount = this.channel.storageItems.get(itemId);
//        if (count < storageCount) {
//            this.channel.storageItems.replace(itemId, storageCount - count);
//            this.channel.onItemChanged(false);
//        } else {
//            this.channel.storageItems.remove(itemId);
//            this.channel.onItemChanged(true);
//        }
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return this.channel.storageItems.isEmpty();
//    }
//
//
//    @Override
//    public boolean stillValid(@NotNull Player pPlayer) {
//        return true;
//    }
//
//    @Override
//    public void setChanged() {
//        refreshContainer(true);
//        this.channel.onItemChanged(true);
//    }
//
//    @Override
//    public int getMaxStackSize() {
//        return Integer.MAX_VALUE;
//    }
//}
