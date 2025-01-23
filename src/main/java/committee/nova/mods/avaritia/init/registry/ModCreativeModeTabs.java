package committee.nova.mods.avaritia.init.registry;

import committee.nova.mods.avaritia.Static;
import committee.nova.mods.avaritia.init.handler.SingularityRegistryHandler;
import committee.nova.mods.avaritia.util.SingularityUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/3/31 10:36
 * Version: 1.0
 */
public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Static.MOD_ID);
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = TABS.register("avaritia_group", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tab.Infinity"))
            .icon(() -> ModItems.infinity_catalyst.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (var singularity : SingularityRegistryHandler.getInstance().getSingularities()) {
                    if (singularity.isEnabled()) {
                        output.accept(SingularityUtils.getItemForSingularity(singularity));
                    }
                }
                ModItems.ITEMS.getEntries().forEach(itemRegistryObject -> {
                    output.accept(itemRegistryObject.get());
                });
            })
            .build());

}
