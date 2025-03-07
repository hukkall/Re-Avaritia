package committee.nova.mods.avaritia.common.capability;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/3/6 14:01
 * @Description:
 */
public interface IItemPage {
    String KEY = "Page";
    int getCurrentPage(@NotNull ItemStack var1);

}
