package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockTurbineBlade extends Block {
    public BlockTurbineBlade() {
        super(Material.IRON);
        setRegistryName("turbine_blade");
        setUnlocalizedName("turbine_blade");
        setCreativeTab(TabLoader.elecTab);
    }
}
