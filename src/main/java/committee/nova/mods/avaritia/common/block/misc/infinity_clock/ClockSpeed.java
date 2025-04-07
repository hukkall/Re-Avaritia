package committee.nova.mods.avaritia.common.block.misc.infinity_clock;

import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/4/4 00:54
 * @Description:
 */
public enum ClockSpeed implements StringRepresentable {
    STOP("stop"),
    SLOW("slow"),
    COMMON("common"),
    FAST("fast");

    private final String name;

    private ClockSpeed(String pName) {
        this.name = pName;
    }

    public String toString() {
        return this.getSerializedName();
    }

    public @NotNull String getSerializedName() {
        return this.name;
    }
}
