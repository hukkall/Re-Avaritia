package committee.nova.mods.avaritia.common.item.misc;

import committee.nova.mods.avaritia.common.item.resources.ResourceItem;
import committee.nova.mods.avaritia.init.registry.ModRarities;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/3/25 19:21
 * @Description:
 */
public class InfinityUpgradeItem extends ResourceItem {
    public InfinityUpgradeItem(String registryName) {
        super(ModRarities.LEGEND, registryName, true,
                new Properties().defaultDurability(16)
                );
    }
}
