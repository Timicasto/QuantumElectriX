package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockSphalerite extends Block {
    public BlockSphalerite() {
        super(Material.ROCK);
        setRegistryName("sphalerite");
        setUnlocalizedName("sphalerite");
        setCreativeTab(TabLoader.elecTab);
    }
}
