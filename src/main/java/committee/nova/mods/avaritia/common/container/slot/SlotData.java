package committee.nova.mods.avaritia.common.container.slot;

import net.minecraft.world.inventory.Slot;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/2/23 01:54
 * @Description:
 */
public record SlotData(Slot slot, int x, int y) {

    public SlotData(Slot s) {
        this(s, s.x, s.y);
    }

    public void setOffset(int x, int y) {
        slot.x = this.x + x;
        slot.y = this.y + y;
    }
}
