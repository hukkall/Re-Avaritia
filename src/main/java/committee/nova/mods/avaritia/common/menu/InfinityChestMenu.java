package committee.nova.mods.avaritia.common.menu;

import committee.nova.mods.avaritia.api.common.container.OffsetContainer;
import committee.nova.mods.avaritia.api.common.menu.BaseTileMenu;
import committee.nova.mods.avaritia.common.tile.InfinityChestTile;
import committee.nova.mods.avaritia.init.config.ModConfig;
import committee.nova.mods.avaritia.init.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/31 15:38
 * @Description:
 */
public class InfinityChestMenu extends BaseTileMenu<InfinityChestTile> {
    private final OffsetContainer container;
    private final ContainerData chestData;
    private final ContainerData itemCounts;

    public InfinityChestMenu(int id, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(id, playerInventory, extraData.readBlockPos(), OffsetContainer.dummy(54), new SimpleContainerData(1));
    }

    public InfinityChestMenu(int id, Inventory playerInventory, @NotNull BlockPos pos, OffsetContainer container, final ContainerData chestData) {
        super(ModMenus.infinity_chest2.get(), id, playerInventory, pos);
        this.container = container;
        this.chestData = chestData;
        this.itemCounts = container.getItemCountAccessor();
        int rows = ModConfig.inventoryRows.get();
        int offset = (rows - 4) * 18;
        for(int i = 0; i < rows; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(this.container, j + i * 9, 8 + j * 18, 36 + i * 18));
            }
        }
        createInventorySlots(playerInventory, 0, 38 + offset);
        this.addDataSlots(this.chestData);
        for(int i = 0; i < this.itemCounts.getCount(); ++i) {
            int finalI = i;
            this.addDataSlot(new DataSlot() {
                private int lastKnownPage = -1;

                @Override public int get() {
                    return InfinityChestMenu.this.itemCounts.get(finalI);
                }

                @Override public void set(int value) {
                    InfinityChestMenu.this.itemCounts.set(finalI, value);
                }

                @Override
                public boolean checkAndClearUpdateFlag() {
                    if (super.checkAndClearUpdateFlag()) {
                        return true;
                    } else {
                        int page = chestData.get(0);
                        boolean flag = page != this.lastKnownPage;
                        this.lastKnownPage = page;
                        return flag;
                    }
                }
            });
        }
    }

    public void changePage(int page) {
        int currentPage = this.chestData.get(0);
        int nextPage = Mth.clamp(page, 0,  ModConfig.maxPageLimit.get() - 1);
        if (nextPage != currentPage) {
            this.chestData.set(0, nextPage);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getMaxPage() {
        return ModConfig.maxPageLimit.get();
    }

    @OnlyIn(Dist.CLIENT)
    public int getCurrentPage() {
        return this.chestData.get(0);
    }

    @OnlyIn(Dist.CLIENT)
    public long getItemCount(int slot) {
        return Integer.toUnsignedLong(this.itemCounts.get(slot));
    }

    @OnlyIn(Dist.CLIENT)
    public OffsetContainer getChestInventory() {
        return this.container;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot) {
        return super.quickMoveStack(player, slot);
    }
}
