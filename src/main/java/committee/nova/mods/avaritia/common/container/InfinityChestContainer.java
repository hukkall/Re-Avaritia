package committee.nova.mods.avaritia.common.container;

import net.minecraft.world.SimpleContainer;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/14 22:58
 * @Description:
 */
public class InfinityChestContainer extends SimpleContainer {


    public InfinityChestContainer() {
        super(99);
    }

    @Override
    public void setChanged() {
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }
}
