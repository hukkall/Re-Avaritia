package committee.nova.mods.avaritia.util;

import committee.nova.mods.avaritia.common.wrappers.StorageItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Comparator;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2024/11/21 12:49
 * @Description:
 */
public class StorageUtils {

    public static final Comparator<StorageItem> ITEM_REGISTRY_NAME = (item1, item2) -> {
        ResourceLocation registryName1 = ForgeRegistries.ITEMS.getKey(item1.getStack().getItem());
        ResourceLocation registryName2 = ForgeRegistries.ITEMS.getKey(item2.getStack().getItem());
        if (registryName1 != null && registryName2 != null) {
            return registryName1.getPath().compareTo(registryName2.getPath());
        } else if (registryName1 == null && registryName2 == null) {
            return 0;
        } else {
            return registryName1 == null ? 1 : -1;
        }
    };
    public static final Comparator<StorageItem> MOD_ID = (item1, item2) -> {
        ResourceLocation registryName1 = ForgeRegistries.ITEMS.getKey(item1.getStack().getItem());
        ResourceLocation registryName2 = ForgeRegistries.ITEMS.getKey(item2.getStack().getItem());
        if (registryName1 != null && registryName2 != null) {
            return registryName1.getNamespace().compareTo(registryName2.getNamespace());
        } else if (registryName1 == null && registryName2 == null) {
            return 0;
        } else {
            return registryName1 == null ? 1 : -1;
        }
    };
    public static final Comparator<StorageItem> ITEM_NAME = (item1, item2) -> {
        String name1 = item1.getStack().getDisplayName().getString();
        String name2 = item2.getStack().getDisplayName().getString();
        return name1.compareTo(name2);
    };
    public static final Comparator<StorageItem> ITEM_COUNT = Comparator.comparingLong(StorageItem::getCount);
    public static final Comparator<StorageItem> DEFAULT_1 = ITEM_REGISTRY_NAME.thenComparing(MOD_ID).thenComparing(ITEM_COUNT.reversed());
    public static final Comparator<StorageItem> DEFAULT_2 = ITEM_NAME.thenComparing(ITEM_COUNT.reversed());
    public static final Comparator<StorageItem> DEFAULT_3 = ITEM_COUNT.reversed().thenComparing(ITEM_NAME);

    public static Int2ObjectMap<StorageItem> newContainers() {
        Int2ObjectOpenHashMap<StorageItem> containers = new Int2ObjectOpenHashMap<>();
        containers.defaultReturnValue(StorageItem.EMPTY);
        return containers;
    }

    public static void saveAllItems(CompoundTag nbt, Int2ObjectMap<StorageItem> containers) {
        ListTag list = new ListTag();

        for (Int2ObjectMap.Entry<StorageItem> storageItemEntry : containers.int2ObjectEntrySet()) {
            int index = storageItemEntry.getIntKey();
            StorageItem item = storageItemEntry.getValue();
            if (!item.isEmpty()) {
                CompoundTag compound = item.serializeNBT();
                compound.putInt("Index", index);
                list.add(compound);
            }
        }
        nbt.put("Items", list);
    }

    public static void loadAllItems(CompoundTag nbt, Int2ObjectMap<StorageItem> containers, boolean clear) {
        if (clear) containers.clear();
        ListTag list = nbt.getList("Items", Tag.TAG_COMPOUND);

        for(int i = 0; i < list.size(); ++i) {
            CompoundTag compound = list.getCompound(i);
            int index = compound.getInt("Index");
            StorageItem item = StorageItem.read(compound);
            if (!item.isEmpty()) {
                containers.put(index, item);
            }
        }

    }
}
