package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockPermanentMagnet extends Block {
    public BlockPermanentMagnet() {
        super(Material.ROCK);
        setRegistryName("permanent_magnet");
        setUnlocalizedName("permanent_magnet");
        setCreativeTab(TabLoader.elecTab);
    }
}
