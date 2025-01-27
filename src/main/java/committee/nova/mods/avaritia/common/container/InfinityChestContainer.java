package committee.nova.mods.avaritia.common.container;

import committee.nova.mods.avaritia.common.wrappers.InfinityChestWrapper;
import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/14 22:58
 * @Description:
 */
public interface InfinityChestContainer extends Container {

    static InfinityChestContainer dummy(int length) {
        final InfinityChestWrapper itemHandler = InfinityChestWrapper.dummy(length);
        return new InfinityChestContainer() {
            @Override
            public InfinityChestWrapper getItemHandler() {
                return itemHandler;
            }
            @Override
            public void setChanged() {
            }
            @Override
            public boolean stillValid(@NotNull Player player) {
                return true;
            }
        };
    }

    InfinityChestWrapper getItemHandler();
    default StorageItem getContainerInSlot(int index) {
        return this.getItemHandler().getContainerInSlot(index);
    }

    default ContainerData getItemCountAccessor() {
        return new ContainerData() {
            public int get(int index) {
                return index >= 0 && index < InfinityChestContainer.this.getContainerSize() ? (int) InfinityChestContainer.this.getContainerInSlot(index).getCount() : 0;
            }

            public void set(int index, int value) {
                if (index >= 0 && index < InfinityChestContainer.this.getContainerSize()) {
                    InfinityChestContainer.this.getContainerInSlot(index).setCount(Integer.toUnsignedLong(value));
                }

            }

            public int getCount() {
                return InfinityChestContainer.this.getContainerSize();
            }
        };
    }

    @Override
    default int getContainerSize() {
        return this.getItemHandler().getSlots();
    }

    @Override
    default boolean isEmpty() {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            if (!this.getContainerInSlot(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    default @NotNull ItemStack getItem(int index) {
        return this.getContainerInSlot(index).getStack().copy();
    }

    @Override
    default @NotNull ItemStack removeItem(int index, int count) {
        return this.getItemHandler().extractItem(index, count, false);
    }

    @Override
    default @NotNull ItemStack removeItemNoUpdate(int index) {
        StorageItem container = this.getItemHandler().removeContainerInSlot(index);
        if (container.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stack = container.getStack();
            int size = (int)Math.min(container.getCount(), stack.getMaxStackSize());
            return ItemHandlerHelper.copyStackWithSize(stack, size);
        }
    }

    @Override
    default void setItem(int index, @NotNull ItemStack stack) {
        this.getItemHandler().setStackInSlot(index, stack);
    }

    @Override
    default int getMaxStackSize() {
        return this.getItemHandler().getSlotLimit(0);
    }

    @Override
    default void clearContent() {
        for(int i = 0; i < this.getContainerSize(); ++i) {
            this.getItemHandler().removeContainerInSlot(i);
        }

    }
}
