package committee.nova.mods.avaritia.common.container;

import net.minecraft.util.Mth;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/3/5 13:14
 * @Description:
 */
public class InfinityContainer extends SimpleContainer {
    private int additionalSlots;
    private int differenceInAdditionalSlots;
    public InfinityContainer() {
        super(54);
        updateInfinitorySize();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        updateInfinitorySize();
    }

    public void updateInfinitorySize(){
        boolean isFull = true;
        boolean isFullBeforeLastItem = true;
        boolean lastRowEmpty = true;
        int lastItem = -1;
        for (int i = this.items.size()-1; i>=9; --i) { // last 5 of main are armor and offhand
            boolean empty = this.getItem(i).isEmpty();
            if (!empty && lastItem == -1)
                lastItem = i;
            if (empty) {
                if (lastItem != -1)
                    isFullBeforeLastItem = false;
                isFull = false;
            }
            if (i >= this.items.size()-9 && !empty)
                lastRowEmpty = false;
            this.setAdditionalSlots(lastItem - 53 + (((isFull || (isFullBeforeLastItem && lastRowEmpty)) && (lastItem+1) % 9 == 0) ? 9 : 0));
        }
    }

    public void setAdditionalSlots(int additionalSlots) {
        // bound between 0 to config max
        additionalSlots = Mth.clamp(additionalSlots, 0, Integer.MAX_VALUE);
        // must be multiple of 9
        if (additionalSlots % 9 != 0)
            additionalSlots = additionalSlots + (9 - additionalSlots % 9);

        // update main size
        while (items.size() < 54 + additionalSlots)
            items.add(ItemStack.EMPTY);
        while (items.size() > 54 + additionalSlots)
            items.remove(items.size() - 1);

        if (this.additionalSlots != additionalSlots) {
            this.differenceInAdditionalSlots = this.additionalSlots - additionalSlots;
            this.additionalSlots = additionalSlots;
            //this.syncInfinitoryValues();
            // update extra slots if needed
            this.updateInfinitorySize();
        }
    }
}
