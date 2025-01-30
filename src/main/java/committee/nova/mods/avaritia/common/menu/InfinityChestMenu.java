package committee.nova.mods.avaritia.common.menu;

import committee.nova.mods.avaritia.api.common.menu.BaseTileMenu;
import committee.nova.mods.avaritia.common.container.InfinityChestContainer;
import committee.nova.mods.avaritia.common.tile.InfinityChestTile;
import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper;
import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.init.registry.ModMenus;
import committee.nova.mods.avaritia.util.StorageUtils;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.stream.IntStream;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/17 01:51
 * @Description:
 */
public class InfinityChestMenu extends BaseTileMenu<InfinityChestTile> {
    private final Inventory playerInventory;
    private final InfinityChestContainer chestInventory;
    private final ContainerData chestData;
    private final ContainerData itemCounts;
    private final int mainInventorySize;
    private int swapIndex;

    public InfinityChestMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, extraData.readBlockPos(), InfinityChestContainer.dummy(36), new SimpleContainerData(2));
    }

    public InfinityChestMenu(int id, Inventory playerInventory, BlockPos pos, InfinityChestContainer chestInventory, final ContainerData chestData) {
        super(ModMenus.infinity_chest.get(), id, playerInventory, pos);
        this.playerInventory = playerInventory;
        this.chestInventory = chestInventory;
        this.chestData = chestData;
        this.itemCounts = chestInventory.getItemCountAccessor();
        this.mainInventorySize = playerInventory.items.size() + chestInventory.getContainerSize();
        this.swapIndex = -1;
        this.slotInitializeMultiPage();
        this.addDataSlots(this.chestData);

        for(int i = 0; i < this.itemCounts.getCount(); ++i) {
            int finalI = i;
            this.addDataSlot(new DataSlot() {
                private int lastKnownPage = -1;

                public int get() {
                    return InfinityChestMenu.this.itemCounts.get(finalI);
                }

                public void set(int value) {
                    InfinityChestMenu.this.itemCounts.set(finalI, value);
                }

                public boolean checkAndClearUpdateFlag() {
                    if (super.checkAndClearUpdateFlag()) {
                        return true;
                    } else {
                        int page = chestData.get(1);
                        boolean flag = page != this.lastKnownPage;
                        this.lastKnownPage = page;
                        return flag;
                    }
                }
            });
        }
    }

    private void slotInitializeMultiPage() {
        int rows = ModConfig.inventoryRows.get();
        int offset = (rows - 4) * 18;

        int i;
        int j;
        for(i = 0; i < rows; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.chestInventory, j + i * 9, 8 + j * 18, 36 + i * 18));
            }
        }

        for(i = 0; i < 3; ++i) {
            for(j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.playerInventory, j + i * 9 + 9, 8 + j * 18, 122 + i * 18 + offset));
            }
        }

        for(i = 0; i < 9; ++i) {
            this.addSlot(new Slot(this.playerInventory, i, 8 + i * 18, 180 + offset));
        }
    }


    @OnlyIn(Dist.CLIENT)
    public int getMaxPage() {
        return ModConfig.maxPageLimit.get();
    }

    @OnlyIn(Dist.CLIENT)
    public int getCurrentPage() {
        return this.chestData.get(1);
    }

    @OnlyIn(Dist.CLIENT)
    public long getItemCount(int slot) {
        return Integer.toUnsignedLong(this.itemCounts.get(slot));
    }

    @OnlyIn(Dist.CLIENT)
    public Inventory getPlayerInventory() {
        return this.playerInventory;
    }

    @OnlyIn(Dist.CLIENT)
    public InfinityChestContainer getChestInventory() {
        return this.chestInventory;
    }

    @OnlyIn(Dist.CLIENT)
    public int getSwapIndex() {
        return this.swapIndex;
    }

    public void changePage(int page) {
        int currentPage = this.chestData.get(1);
        int nextPage = Mth.clamp(page, 0,  ModConfig.maxPageLimit.get() - 1);
        if (nextPage != currentPage) {
            this.chestData.set(1, nextPage);
        }
    }


    private void swap(int index1, int index2) {
        InfinityChestWrapper itemHandler = this.chestInventory.getItemHandler();
        StorageItem container1 = itemHandler.removeContainerInSlot(index1);
        StorageItem container2 = itemHandler.removeContainerInSlot(index2);
        if (!container2.isEmpty()) {
            itemHandler.setContainerInSlot(index1, container2);
        }

        if (!container1.isEmpty()) {
            itemHandler.setContainerInSlot(index2, container1);
        }

    }

    private void sort(Comparator<StorageItem> comparator, int index1, int index2) {
        if (index1 >= 0 && index2 >= 0 && index1 < index2) {
            InfinityChestWrapper itemHandler = this.chestInventory.getItemHandler();
            int length = index2 - index1;
            int h = length;
            boolean loop = false;

            while(h > 1 || loop) {
                if (h > 1) {
                    h = h * 10 / 13;
                }

                loop = false;

                for(int i = 0; i < length - h; ++i) {
                    int j = index1 + i;
                    int k = j + h;
                    StorageItem container1 = itemHandler.getContainerInSlot(j);
                    StorageItem container2 = itemHandler.getContainerInSlot(k);
                    boolean swap = container1.isEmpty() && !container2.isEmpty();
                    if (!container1.isEmpty() && !container2.isEmpty()) {
                        if (ItemHandlerHelper.canItemStacksStack(container1.getStack(), container2.getStack())) {
                            long freeSpace = itemHandler.getSlotFreeSpace(j);
                            if (freeSpace > 0L) {
                                long size = Math.min(container2.getCount(), freeSpace);
                                container1.growCount(size);
                                container2.shrinkCount(size);
                                loop = true;
                            }
                        }

                        swap = comparator.compare(container1, container2) > 0;
                    }

                    if (swap) {
                        this.swap(j, k);
                        loop = true;
                    }
                }
            }
        }

    }

    @Override
    protected boolean moveItemStackTo(@NotNull ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        IntStream stream = IntStream.range(startIndex, endIndex);
        if (reverseDirection) {
            stream = stream.map((i) -> endIndex - i + startIndex - 1);
        }

        PrimitiveIterator.OfInt it = stream.filter((i) -> i >= 0 && i < this.slots.size()).iterator();
        boolean result = false;
        IntList emptySlots = new IntArrayList(endIndex - startIndex);

        while(it.hasNext() && !stack.isEmpty()) {
            int index = it.nextInt();
            Slot slot = this.getSlot(index);
            ItemStack stackInSlot = slot.getItem();
            if (stackInSlot.isEmpty()) {
                emptySlots.add(index);
            } else if (ItemHandlerHelper.canItemStacksStack(stack, stackInSlot)) {
                int size;
                if (index < this.chestInventory.getContainerSize()) {
                    long freeSpace = this.chestInventory.getItemHandler().getSlotFreeSpace(index);
                    if (freeSpace > 0L) {
                        size = (int)Math.min(stack.getCount(), freeSpace);
                        this.chestInventory.getContainerInSlot(index).growCount(size);
                        stack.shrink(size);
                        result = true;
                    }
                } else {
                    int count = stackInSlot.getCount();
                    int freeSpace = Math.min(stackInSlot.getMaxStackSize(), slot.getMaxStackSize()) - count;
                    if (freeSpace > 0) {
                        size = Math.min(stack.getCount(), freeSpace);
                        stackInSlot.grow(size);
                        stack.shrink(size);
                        result = true;
                    }
                }
            }
        }

        if (!stack.isEmpty()) {
            IntListIterator emptiesIt = emptySlots.iterator();

            while(emptiesIt.hasNext()) {
                int emptySlot = emptiesIt.nextInt();
                Slot slot = this.getSlot(emptySlot);
                slot.set(stack.split(slot.getMaxStackSize()));
                result = true;
                if (stack.isEmpty()) {
                    break;
                }
            }
        }

        return result;
    }

    @Override
    public void clicked(int slotId, int dragType, @NotNull ClickType clickTypeIn, @NotNull Player player) {
        Slot slot = slotId >= 0 && slotId < this.mainInventorySize ? this.getSlot(slotId) : null;
        if (slot == null) {
            super.clicked(slotId, dragType, clickTypeIn, player);
        } else {
            ItemStack grabbedStack;
            ItemStack stackInSlot;
            ItemStack copy;
            int i;
            if (slotId < this.chestInventory.getContainerSize()) {
                this.swapIndex = clickTypeIn == ClickType.PICKUP ? this.swapIndex : -1;
                int size;
                if (clickTypeIn == ClickType.PICKUP) {
                    grabbedStack = getCarried().copy();
                    stackInSlot = slot.getItem();
                    if (this.swapIndex != -1) {
                        dragType = 2;
                    }

                    if (dragType == 0) {
                        if (grabbedStack.isEmpty()) {
                            if (!stackInSlot.isEmpty()) {
                                copy = slot.remove(stackInSlot.getMaxStackSize());
                                setCarried(copy);
                            }
                        } else {
                            if (stackInSlot.isEmpty()) {
                                slot.set(grabbedStack);
                                setCarried(ItemStack.EMPTY);
                            }

                            if (ItemHandlerHelper.canItemStacksStack(grabbedStack, stackInSlot) && this.moveItemStackTo(grabbedStack, slotId, slotId + 1, false)) {
                                setCarried(grabbedStack);
                            }
                        }
                    } else if (dragType == 1) {
                        if (grabbedStack.isEmpty()) {
                            if (!stackInSlot.isEmpty()) {
                                long count = this.chestInventory.getContainerInSlot(slotId).getCount();
                                size = (int)Math.min(count, stackInSlot.getMaxStackSize()) / 2;
                                ItemStack result = slot.remove(size);
                                setCarried(result);
                            }
                        } else {
                            if (stackInSlot.isEmpty()) {
                                slot.set(grabbedStack.split(1));
                                setCarried(grabbedStack);
                            }

                            if (ItemHandlerHelper.canItemStacksStack(grabbedStack, stackInSlot) && this.chestInventory.getItemHandler().getSlotFreeSpace(slotId) > 0L) {
                                grabbedStack.shrink(1);
                                this.chestInventory.getContainerInSlot(slotId).growCount(1L);
                                setCarried(grabbedStack);
                            }
                        }
                    } else if (dragType == 2) {
                        if (this.swapIndex >= 0) {
                            this.swap(this.swapIndex, slotId);
                            this.swapIndex = -1;
                        } else {
                            this.swapIndex = slotId;
                        }
                    } else if (dragType == 3 && !stackInSlot.isEmpty()) {
                        copy = ItemHandlerHelper.copyStackWithSize(stackInSlot, 1);
                        if (this.moveItemStackTo(copy, this.chestInventory.getContainerSize(), this.mainInventorySize, true)) {
                            this.chestInventory.getContainerInSlot(slotId).shrinkCount(1L);
                            if (this.chestInventory.getContainerInSlot(slotId).isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                            }
                        }
                    }
                } else if (clickTypeIn == ClickType.THROW) {
                    grabbedStack = getCarried();
                    if (grabbedStack.isEmpty()) {
                        stackInSlot = slot.getItem();
                        if (!stackInSlot.isEmpty()) {
                            StorageItem container = this.chestInventory.getContainerInSlot(slotId);
                            int stackSize = dragType == 1 ? stackInSlot.getMaxStackSize() : 1;
                            size = (int)Math.min(stackSize, container.getCount());
                            player.drop(ItemHandlerHelper.copyStackWithSize(stackInSlot, size), false);
                            container.shrinkCount(size);
                            if (container.isEmpty()) {
                                slot.set(ItemStack.EMPTY);
                            }
                        }
                    }
                } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                    if (dragType == 2) {
                        grabbedStack = slot.getItem().copy();
                        if (!grabbedStack.isEmpty()) {
                            for(i = 0; i < this.chestInventory.getContainerSize(); ++i) {
                                copy = this.getSlot(i).getItem();
                                if (ItemHandlerHelper.canItemStacksStack(grabbedStack, copy)) {
                                    while(!this.quickMoveStack(player, i).isEmpty()) {
                                    }
                                }
                            }
                        }
                    } else {
                        this.quickMoveStack(player, slotId);
                    }
                } else if (clickTypeIn == ClickType.CLONE) {
                    Comparator<StorageItem> comparator = StorageUtils.DEFAULT_1;
                    if (dragType == 2) {
                        comparator = StorageUtils.DEFAULT_2;
                    } else if (dragType == 3) {
                        comparator = StorageUtils.DEFAULT_3;
                    }

                    this.sort(comparator, 0, this.chestInventory.getContainerSize());
                }
            } else {
                this.swapIndex = -1;
                if (clickTypeIn == ClickType.PICKUP) {
                    if (dragType == 3) {
                        grabbedStack = slot.getItem();
                        if (!grabbedStack.isEmpty()) {
                            stackInSlot = ItemHandlerHelper.copyStackWithSize(grabbedStack, 1);
                            if (this.moveItemStackTo(stackInSlot, 0, this.chestInventory.getContainerSize(), false)) {
                                grabbedStack.shrink(1);
                            }
                        }
                    }
                } else if (clickTypeIn == ClickType.QUICK_MOVE && dragType == 2) {
                    grabbedStack = slot.getItem().copy();
                    if (!grabbedStack.isEmpty()) {
                        for(i = this.chestInventory.getContainerSize(); i < this.mainInventorySize; ++i) {
                            copy = this.getSlot(i).getItem();
                            if (ItemHandlerHelper.canItemStacksStack(grabbedStack, copy)) {
                                this.quickMoveStack(player, i);
                            }
                        }
                    }
                }
                super.clicked(slotId, dragType, clickTypeIn, player);
            }
        }
    }

    @Override
    public boolean canDragTo(Slot slotIn) {
        return Objects.equals(slotIn.container, this.playerInventory);
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slotIn) {
        return Objects.equals(slotIn.container, this.playerInventory);
    }

}
