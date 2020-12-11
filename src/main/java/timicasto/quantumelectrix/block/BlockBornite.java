package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockBornite extends Block {
    public BlockBornite() {
        super(Material.ROCK);
        setRegistryName("bornite");
        setUnlocalizedName("bornite");
        setCreativeTab(TabLoader.elecTab);
    }
}
