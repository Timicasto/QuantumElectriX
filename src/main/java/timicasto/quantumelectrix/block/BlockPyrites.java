package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockPyrites extends Block {
    public BlockPyrites() {
        super(Material.ROCK);
        setRegistryName("pyrites");
        setUnlocalizedName("pyrites");
        setCreativeTab(TabLoader.elecTab);
    }
}
