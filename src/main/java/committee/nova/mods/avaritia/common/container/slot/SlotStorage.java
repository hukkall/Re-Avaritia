package committee.nova.mods.avaritia.common.container.slot;

import committee.nova.mods.avaritia.common.container.StoredItemStack;
import committee.nova.mods.avaritia.common.tile.WipChestTile;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/17 02:29
 * @Description:
 */
public class SlotStorage{
    /** display position of the inventory slot on the screen x axis */
    public int xDisplayPosition;
    /** display position of the inventory slot on the screen y axis */
    public int yDisplayPosition;
    /** The index of the slot in the inventory. */
    private final int slotIndex;
    /** The inventory we want to extract a slot from. */
    public final WipChestTile inventory;
    public StoredItemStack stack;

    public SlotStorage(WipChestTile inventory, int slotIndex, int xPosition, int yPosition) {
        this.xDisplayPosition = xPosition;
        this.yDisplayPosition = yPosition;
        this.slotIndex = slotIndex;
        this.inventory = inventory;
    }

    public ItemStack pullFromSlot(long max) {
        if (stack == null || max < 1 || inventory == null)
            return ItemStack.EMPTY;
        StoredItemStack r = inventory.pullStack(stack, max);
        if (r != null) {
            return r.getActualStack();
        } else
            return ItemStack.EMPTY;
    }

    public ItemStack pushStack(ItemStack pushStack) {
        if(inventory == null)return pushStack;
        StoredItemStack r = inventory.pushStack(new StoredItemStack(pushStack, pushStack.getCount()));
        if (r != null) {
            return r.getActualStack();
        } else
            return ItemStack.EMPTY;
    }

    public int getSlotIndex() {
        return slotIndex;
    }
}
