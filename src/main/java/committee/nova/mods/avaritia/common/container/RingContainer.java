package committee.nova.mods.avaritia.common.container;

import com.google.common.collect.ImmutableList;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Nameable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/3/7 03:25
 * @Description:
 */
public class RingContainer implements Container, Nameable {
    public final NonNullList<ItemStack> items;
    public final Player player;
    private int timesChanged;

    public RingContainer(Player pPlayer) {
        this.items = NonNullList.withSize(54, ItemStack.EMPTY);
        this.player = pPlayer;
    }


    private boolean hasRemainingSpaceForItem(ItemStack pDestination, ItemStack pOrigin) {
        return !pDestination.isEmpty() && ItemStack.isSameItemSameTags(pDestination, pOrigin) && pDestination.isStackable() && pDestination.getCount() < pDestination.getMaxStackSize() && pDestination.getCount() < this.getMaxStackSize();
    }

    public int getFreeSlot() {
        for(int i = 0; i < this.items.size(); ++i) {
            if (this.items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }


    public int findSlotMatchingItem(ItemStack pStack) {
        for(int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty() && ItemStack.isSameItemSameTags(pStack, this.items.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public int findSlotMatchingUnusedItem(ItemStack pStack) {
        for(int i = 0; i < this.items.size(); ++i) {
            ItemStack itemstack = this.items.get(i);
            if (!this.items.get(i).isEmpty() && ItemStack.isSameItemSameTags(pStack, this.items.get(i)) && !this.items.get(i).isDamaged() && !itemstack.isEnchanted() && !itemstack.hasCustomHoverName()) {
                return i;
            }
        }

        return -1;
    }

    public int clearOrCountMatchingItems(Predicate<ItemStack> pStackPredicate, int pMaxCount, Container pInventory) {
        int i = 0;
        boolean flag = pMaxCount == 0;
        i += ContainerHelper.clearOrCountMatchingItems(this, pStackPredicate, pMaxCount - i, flag);
        i += ContainerHelper.clearOrCountMatchingItems(pInventory, pStackPredicate, pMaxCount - i, flag);
        ItemStack itemstack = this.player.containerMenu.getCarried();
        i += ContainerHelper.clearOrCountMatchingItems(itemstack, pStackPredicate, pMaxCount - i, flag);
        if (itemstack.isEmpty()) {
            this.player.containerMenu.setCarried(ItemStack.EMPTY);
        }

        return i;
    }

    private int addResource(ItemStack pStack) {
        int i = this.getSlotWithRemainingSpace(pStack);
        if (i == -1) {
            i = this.getFreeSlot();
        }

        return i == -1 ? pStack.getCount() : this.addResource(i, pStack);
    }

    private int addResource(int pSlot, ItemStack pStack) {
        int i = pStack.getCount();
        ItemStack itemstack = this.getItem(pSlot);
        if (itemstack.isEmpty()) {
            itemstack = pStack.copy();
            itemstack.setCount(0);
            if (pStack.hasTag()) {
                itemstack.setTag(pStack.getTag().copy());
            }

            this.setItem(pSlot, itemstack);
        }

        int j = i;
        if (i > itemstack.getMaxStackSize() - itemstack.getCount()) {
            j = itemstack.getMaxStackSize() - itemstack.getCount();
        }

        if (j > this.getMaxStackSize() - itemstack.getCount()) {
            j = this.getMaxStackSize() - itemstack.getCount();
        }

        if (j == 0) {
            return i;
        } else {
            i -= j;
            itemstack.grow(j);
            itemstack.setPopTime(5);
            return i;
        }
    }

    public int getSlotWithRemainingSpace(ItemStack pStack) {

            for(int i = 0; i < this.items.size(); ++i) {
                if (this.hasRemainingSpaceForItem((ItemStack)this.items.get(i), pStack)) {
                    return i;
                }
            }

            return -1;

    }

    public void tick() {
            for(int i = 0; i < this.items.size(); ++i) {
                if (!this.items.get(i).isEmpty()) {
                    this.items.get(i).inventoryTick(this.player.level(), this.player, i, true);
                }
            }
    }

    public boolean add(ItemStack pStack) {
        return this.add(-1, pStack);
    }

    public boolean add(int pSlot, ItemStack pStack) {
        if (pStack.isEmpty()) {
            return false;
        } else {
            try {
                if (pStack.isDamaged()) {
                    if (pSlot == -1) {
                        pSlot = this.getFreeSlot();
                    }

                    if (pSlot >= 0) {
                        this.items.set(pSlot, pStack.copyAndClear());
                        this.items.get(pSlot).setPopTime(5);
                        return true;
                    } else if (this.player.getAbilities().instabuild) {
                        pStack.setCount(0);
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    int i;
                    do {
                        i = pStack.getCount();
                        if (pSlot == -1) {
                            pStack.setCount(this.addResource(pStack));
                        } else {
                            pStack.setCount(this.addResource(pSlot, pStack));
                        }
                    } while(!pStack.isEmpty() && pStack.getCount() < i);

                    if (pStack.getCount() == i && this.player.getAbilities().instabuild) {
                        pStack.setCount(0);
                        return true;
                    } else {
                        return pStack.getCount() < i;
                    }
                }
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.forThrowable(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.addCategory("Item being added");
                crashreportcategory.setDetail("Registry Name", () -> String.valueOf(ForgeRegistries.ITEMS.getKey(pStack.getItem())));
                crashreportcategory.setDetail("Item Class", () -> pStack.getItem().getClass().getName());
                crashreportcategory.setDetail("Item ID", Item.getId(pStack.getItem()));
                crashreportcategory.setDetail("Item data", pStack.getDamageValue());
                crashreportcategory.setDetail("Item name", () -> pStack.getHoverName().getString());
                throw new ReportedException(crashreport);
            }
        }
    }


    public @NotNull ItemStack removeItem(int pIndex, int pCount) {
        List<ItemStack> list = this.items;
        return list != null && !list.get(pIndex).isEmpty() ? ContainerHelper.removeItem(list, pIndex, pCount) : ItemStack.EMPTY;
    }

    public void removeItem(ItemStack pStack) {
            for(int i = 0; i < this.items.size(); ++i) {
                if (this.items.get(i) == pStack) {
                    this.items.set(i, ItemStack.EMPTY);
                    break;
                }
            }
    }

    public @NotNull ItemStack removeItemNoUpdate(int pIndex) {
        NonNullList<ItemStack> nonnulllist = this.items;
        if (nonnulllist != null && !nonnulllist.get(pIndex).isEmpty()) {
            ItemStack itemstack = nonnulllist.get(pIndex);
            nonnulllist.set(pIndex, ItemStack.EMPTY);
            return itemstack;
        } else {
            return ItemStack.EMPTY;
        }
    }

    public void setItem(int pIndex, ItemStack pStack) {
        NonNullList<ItemStack> nonnulllist = this.items;
        if (nonnulllist != null) {
            nonnulllist.set(pIndex, pStack);
        }

    }

    public ListTag save(ListTag pListTag) {
        for(int i = 0; i < this.items.size(); ++i) {
            if (!this.items.get(i).isEmpty()) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putByte("Slot", (byte)i);
                this.items.get(i).save(compoundtag);
                pListTag.add(compoundtag);
            }
        }
        return pListTag;
    }

    public void load(ListTag pListTag) {
        this.items.clear();

        for(int i = 0; i < pListTag.size(); ++i) {
            CompoundTag compoundtag = pListTag.getCompound(i);
            int j = compoundtag.getByte("Slot") & 255;
            ItemStack itemstack = ItemStack.of(compoundtag);
            if (!itemstack.isEmpty()) {
                if (j < this.items.size()) {
                    this.items.set(j, itemstack);
                }
            }
        }

    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public @NotNull ItemStack getItem(int pIndex) {
        List<ItemStack> list = this.items;
        return list == null ? ItemStack.EMPTY : list.get(pIndex);
    }

    public @NotNull Component getName() {
        return Component.translatable("container.inventory");
    }

    public void dropAll() {
            for(int i = 0; i < this.items.size(); ++i) {
                ItemStack itemstack = this.items.get(i);
                if (!itemstack.isEmpty()) {
                    this.player.drop(itemstack, true, false);
                    this.items.set(i, ItemStack.EMPTY);
                }
            }


    }

    public void setChanged() {
        ++this.timesChanged;
    }

    public int getTimesChanged() {
        return this.timesChanged;
    }

    public boolean stillValid(Player pPlayer) {
        if (this.player.isRemoved()) {
            return false;
        } else {
            return !(pPlayer.distanceToSqr(this.player) > (double)64.0F);
        }
    }

    public boolean contains(ItemStack pStack) {
            for(ItemStack itemstack : this.items) {
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(itemstack, pStack)) {
                    return true;
                }
            }
        return false;
    }

    public boolean contains(TagKey<Item> pTag) {
            for(ItemStack itemstack : this.items) {
                if (!itemstack.isEmpty() && itemstack.is(pTag)) {
                    return true;
                }
            }
        return false;
    }

    public void clearContent() {
        this.items.clear();
    }

    public void fillStackedContents(StackedContents pStackedContent) {
        for(ItemStack itemstack : this.items) {
            pStackedContent.accountSimpleStack(itemstack);
        }

    }
}
