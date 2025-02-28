package committee.nova.mods.avaritia.common.container.slot;

import net.minecraft.world.inventory.DataSlot;

import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/2/23 01:53
 * @Description:
 */
public class DataSlots extends DataSlot {
    private IntConsumer c;
    private IntSupplier s;
    private Runnable onUpdate;

    @Override
    public int get() {
        return s.getAsInt();
    }

    @Override
    public void set(int i) {
        c.accept(i);
        if(onUpdate != null)onUpdate.run();
    }

    private DataSlots(IntConsumer c, IntSupplier s) {
        this.c = c;
        this.s = s;
    }

    public static DataSlots set(IntConsumer c) {
        return new DataSlots(c, () -> 0);
    }

    public static DataSlots get(IntSupplier s) {
        return new DataSlots(a -> {}, s);
    }

    public static DataSlots create(IntConsumer c, IntSupplier s) {
        return new DataSlots(c, s);
    }

    public DataSlots onUpdate(Runnable r) {
        this.onUpdate = r;
        return this;
    }
}
