package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockTurbineHouse extends Block {
    public BlockTurbineHouse() {
        super(Material.IRON);
        setRegistryName("turbine_house");
        setUnlocalizedName("turbine_house");
        setCreativeTab(TabLoader.elecTab);
    }
}
