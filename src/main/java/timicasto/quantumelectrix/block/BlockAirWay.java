package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockAirWay extends Block {
    public BlockAirWay() {
        super(Material.GLASS);
        setRegistryName("air_way");
        setUnlocalizedName("air_way");
        setCreativeTab(TabLoader.elecTab);
    }
}
