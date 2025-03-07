package committee.nova.mods.avaritia.common.capability;

import committee.nova.mods.avaritia.common.wrappers.RingStorageWrapper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/8/5 下午11:31
 * @Description:
 */
public class RingStorageProvider implements ICapabilitySerializable<CompoundTag> {
    private final RingStorageWrapper inv;
    private final LazyOptional<RingStorageWrapper> inventoryCap;

    public RingStorageProvider(RingStorageWrapper inv) {
        this.inv = inv;
        this.inventoryCap = LazyOptional.of(() -> inv);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return inv.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        inv.deserializeNBT(nbt);
    }
}
