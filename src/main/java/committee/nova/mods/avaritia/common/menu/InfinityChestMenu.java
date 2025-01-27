package committee.nova.mods.avaritia.common.menu;

import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.api.common.menu.BaseTileMenu;
import committee.nova.mods.avaritia.common.container.InfinityChestContainer;
import committee.nova.mods.avaritia.common.tile.InfinityChestTile;
import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper;
import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper.*;
import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.init.registry.ModMenus;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

}
