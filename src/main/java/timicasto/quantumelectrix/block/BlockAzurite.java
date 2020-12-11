package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockAzurite extends Block {
    public BlockAzurite() {
        super(Material.ROCK);
        setRegistryName("azurite");
        setUnlocalizedName("azurite");
        setCreativeTab(TabLoader.elecTab);
    }
}
