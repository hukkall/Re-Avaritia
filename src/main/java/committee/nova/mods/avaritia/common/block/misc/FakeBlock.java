package committee.nova.mods.avaritia.common.block.misc;

import committee.nova.mods.avaritia.api.common.block.BaseBlock;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

/**
 * @Project: Avaritia
 * @Author: cnlimiter
 * @CreateTime: 2025/1/22 19:27
 * @Description: 
 */
public class FakeBlock extends BaseBlock {
        private final Block block;
        public FakeBlock(Block block) {
            super(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .sound(SoundType.STONE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .strength(500F, 3600000.0F)
                    .isValidSpawn((state, level, pos, value) -> false)
                    .pushReaction(PushReaction.BLOCK));
            this.block = block;
        }

        @Override
        public @NotNull MutableComponent getName() {
            return block.getName();
        }
}
