package timicasto.quantumelectrix.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.ArrayList;

public class MultiblockHandler {
    static ArrayList<IMultiblock> multiblocks = new ArrayList<IMultiblock>();
    
    public static void registerMultiBlock(IMultiblock multiblock) {
        multiblocks.add(multiblock);
    }

    public static ArrayList<IMultiblock> getMultiblocks() {
        return multiblocks;
    }

    public static MultiblockFormEvent postMultiblockFormationEvent(EntityPlayer player, IMultiblock multiblock, BlockPos hitPos, ItemStack tool) {
        MultiblockFormEvent e = new MultiblockFormEvent(player, multiblock, hitPos, tool);
        MinecraftForge.EVENT_BUS.post(e);
        return e;
    }

    @Cancelable
    public static class MultiblockFormEvent extends PlayerEvent {
        private final IMultiblock structure;
        private final BlockPos hitPos;
        private final ItemStack tool;
        public MultiblockFormEvent(EntityPlayer player, IMultiblock multiblock, BlockPos hitPos, ItemStack tool) {
            super(player);
            this.structure = multiblock;
            this.hitPos = hitPos;
            this.tool = tool;
        }

        public IMultiblock getStructure() {
            return structure;
        }

        public BlockPos getHitPos() {
            return hitPos;
        }

        public ItemStack getTool() {
            return tool;
        }
    }
}
