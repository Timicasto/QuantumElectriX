package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockChalcocite extends Block {
    public BlockChalcocite() {
        super(Material.ROCK);
        setRegistryName("chalcocite");
        setUnlocalizedName("chalcocite");
        setCreativeTab(TabLoader.elecTab);
    }
}
