//package committee.nova.mods.avaritia.api.common.container;
//
//import com.google.common.collect.Lists;
//import committee.nova.mods.avaritia.api.iface.ISortContainer;
//import committee.nova.mods.avaritia.api.utils.java.EnumUtils;
//import committee.nova.mods.avaritia.api.utils.math.SortingType;
//import lombok.Getter;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.util.Mth;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import org.spongepowered.asm.mixin.Unique;
//
//import java.util.ArrayList;
//
///**
// * @Project: Avaritia
// * @Author: cnlimiter
// * @CreateTime: 2025/3/3 00:59
// * @Description:
// */
//public class InfinityContainer extends SimpleContainer implements ISortContainer {
//    private int size = 54;
//    private SortingType sortingType;
//    private boolean sortAscending;
//    @Getter
//    private int additionalSlots;
//    @Getter
//    private int differenceInAdditionalSlots;
//    private boolean needToUpdateInfinitorySize;
//    private boolean needToUpdateClient;
//    private boolean needToSort;
//
//    public Player player;
//
//    public InfinityContainer(Player player) {
//        super(54);
//        this.player = player;
//        this.updateInfinitorySize();
//    }
//
//    private boolean hasRemainingSpaceForItem(ItemStack pDestination, ItemStack pOrigin) {
//        return !pDestination.isEmpty() && ItemStack.isSameItemSameTags(pDestination, pOrigin) && pDestination.isStackable() && pDestination.getCount() < pDestination.getMaxStackSize() && pDestination.getCount() < this.getMaxStackSize();
//    }
//
//    @Unique
//    private void sort() {
//        if (!this.player.level().isClientSide && this.getSortingType() != SortingType.NONE) {
//            // combine all items into separate list (only inventory - not hotbar!)
//            ArrayList<ItemStack> list = Lists.newArrayList();
//            for (int i=9; i<this.items.size(); ++i) {
//                ItemStack addingStack = this.getItem(i).copy();
//                outer:
//                if (!addingStack.isEmpty()) {
//                    // try to stack with existing items in list
//                    for (ItemStack stack : list) {
//                        if (this.hasRemainingSpaceForItem(stack, addingStack)) {
//                            int amountToAdd = addingStack.getCount();
//                            if (amountToAdd > this.getMaxStackSize() - stack.getCount())
//                                amountToAdd = this.getMaxStackSize() - stack.getCount();
//                            if (amountToAdd > 0) {
//                                stack.grow(amountToAdd);
//                                addingStack.shrink(amountToAdd);
//                                // addingStack empty - we can skip to the next item
//                                if (addingStack.isEmpty())
//                                    break outer;
//                            }
//                        }
//                    }
//                    // didn't find anything to stack with, add to list (don't worry about max size - should be handled already)
//                    list.add(addingStack);
//                }
//            }
//            // sort
//            this.getSortingType().sort(list, sortAscending);
//            // update main with list
//            for (int i=9; i<this.items.size(); ++i) {
//                ItemStack stack = ItemStack.EMPTY;
//                if (i-9 < list.size())
//                    stack = list.get(i-9);
//                this.setItem(i, stack.copy());
//            }
//        }
//    }
//
//
//    @Override
//    public SortingType getSortingType() {
//        if (this.sortingType == null)
//            this.sortingType = SortingType.NONE;
//        return this.sortingType;
//    }
//
//
//    @Override
//    public void setSortingType(SortingType type) {
//        if (type != null && type != this.sortingType) {
//            this.sortingType = type;
//            this.syncInfinitoryValues();
//        }
//    }
//
//
//    @Override
//    public boolean getSortingAscending() {
//        return this.sortAscending;
//    }
//
//
//    @Override
//    public void setSortAscending(boolean sortAscending) {
//        if (sortAscending != this.sortAscending) {
//            this.sortAscending = sortAscending;
//            this.syncInfinitoryValues();
//        }
//    }
//
//    @Override
//    public void needToSort() {
//        this.needToSort = true;
//        this.needToUpdateInfinitorySize();
//    }
//
//    @Override
//    public void updateInfinitorySize() {
//        if (!this.player.level().isClientSide) {
//            // get indexes of first and last items
//            boolean isFull = true;
//            boolean isFullBeforeLastItem = true;
//            boolean lastRowEmpty = true;
//            int lastItem = -1;
//            for (int i = this.items.size()-1; i>=9; --i) { // last 5 of main are armor and offhand
//                boolean empty = this.getItem(i).isEmpty();
//                if (!empty && lastItem == -1)
//                    lastItem = i;
//                if (empty) {
//                    if (lastItem != -1)
//                        isFullBeforeLastItem = false;
//                    isFull = false;
//                }
//                if (i >= this.items.size()-9 && !empty)
//                    lastRowEmpty = false;
//            }
//            // index of last item rounded up to multiple of 9 additional slots
//            this.setAdditionalSlots(lastItem - this.size - 1 + (((isFull || (isFullBeforeLastItem && lastRowEmpty)) && (lastItem+1) % 9 == 0) ? 9 : 0));
//        }
//    }
//
//    @Override
//    public void syncInfinitoryValues() {
//
//    }
//
//    @Override
//    public void setAdditionalSlots(int additionalSlots) {
//        // bound between 0 to config max
//        additionalSlots = Mth.clamp(additionalSlots, 0, Integer.MAX_VALUE);
//        // must be multiple of 9
//        if (additionalSlots % 9 != 0)
//            additionalSlots = additionalSlots + (9 - additionalSlots % 9);
//
//        // update main size
//        while (this.items.size() < this.size + additionalSlots)
//            this.addItem(ItemStack.EMPTY);
//        while (this.items.size() > this.size + additionalSlots)
//            this.removeItemNoUpdate(this.items.size() - 1);
//
//        if (this.additionalSlots != additionalSlots) {
//            this.differenceInAdditionalSlots = this.additionalSlots - additionalSlots;
//            this.additionalSlots = additionalSlots;
//            this.syncInfinitoryValues();
//            // update extra slots if needed
//            this.updateExtraSlots();
//        }
//    }
//
//    @Override
//    public void needToUpdateInfinitorySize() {
//        this.needToUpdateInfinitorySize = true;
//    }
//
//    private void updateExtraSlots() {
//        ((IScreenHandler)this.player.menu).updateExtraSlots();
//        if (this.player.currentScreenHandler != null)
//            ((IScreenHandler)this.player.currentScreenHandler).updateExtraSlots();
//    }
//
//    public void needToUpdateClient() {
//        if (!this.player.level().isClientSide)
//            this.needToUpdateClient = true;
//    }
//
//    @Override
//    public int getMaxStackSize() {
//        return Integer.MAX_VALUE;
//    }
//
//    @Override
//    public void setChanged() {
//        super.setChanged();
//        if (this.getSortingType() == SortingType.QUANTITY)
//            this.needToSort();
//    }
//
//    public void tick() {
//            for(int i = 0; i < this.items.size(); ++i) {
//                if (!this.getItem(i).isEmpty()) {
//                    (this.getItem(i)).inventoryTick(this.player.level(), this.player, i, false);
//                }
//        }
//        // sort
//        if (this.needToSort) {
//            this.sort();
//            this.needToSort = false;
//        }
//        // update infinitory size
//        if (this.needToUpdateInfinitorySize) {
//            this.updateInfinitorySize();
//            this.needToUpdateInfinitorySize = false;
//        }
//        else // update extra slots if needed (called by updateInfinitorySize as well)
//            this.updateExtraSlots();
//        // update client
//        if (this.needToUpdateClient) {
//            if (this.player.containerMenu != null)
//                (this.player.containerMenu).broadcastChanges();
//            this.needToUpdateClient = false;
//        }
//        // reset difference in additional slots
//        this.differenceInAdditionalSlots = 0;
//    }
//
//
//    public void readNbt(ListTag nbtList) {
//        for(int i = 0; i < nbtList.size(); ++i) {
//            CompoundTag nbtCompound = nbtList.getCompound(i);
//            if (nbtCompound.contains("InfinitorySlot")) {
//                int slot = nbtCompound.getInt("InfinitorySlot");
//                ItemStack itemStack = ItemStack.of(nbtCompound);
//                if (!itemStack.isEmpty()) {
//                    // increase main size if necessary
//                    while (slot > this.items.size()-1 && this.items.size() < Integer.MAX_VALUE)
//                        this.addItem(ItemStack.EMPTY);
//                    if (slot >= 0 && slot < this.items.size())
//                        this.setItem(slot, itemStack);
//                }
//            }
//            else if (nbtCompound.contains("InfinitorySortingType")) {
//                this.sortingType = EnumUtils.getEnumFromString(SortingType.class, nbtCompound.getString("InfinitorySortingType")).orElse(SortingType.NONE);
//                this.sortAscending = nbtCompound.getBoolean("InfinitorySortingAscending");
//            }
//        }
//        this.needToUpdateInfinitorySize();
//        this.needToUpdateClient();
//    }
//
//    public void writeNbt(ListTag nbtList) {
//        // write extra slots
//        for(int i = 0; i < this.items.size(); ++i) {
//            if (!this.getItem(i).isEmpty()) {
//                CompoundTag nbt = new CompoundTag();
//                nbt.putByte("Slot", (byte) 250); // put in a slot value that won't be read by vanilla
//                nbt.putInt("InfinitorySlot", i);
//                this.getItem(i).save(nbt);
//                nbtList.add(nbt);
//            }
//        }
//        // write sorting values
//        CompoundTag nbt = new CompoundTag();
//        nbt.putString("InfinitorySortingType", this.getSortingType().name());
//        nbt.putBoolean("InfinitorySortingAscending", this.getSortingAscending());
//        nbtList.add(nbt);
//    }
//}
