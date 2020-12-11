package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockCoil extends Block {
    public BlockCoil() {
        super(Material.IRON);
        setRegistryName("coil");
        setUnlocalizedName("coil");
        setCreativeTab(TabLoader.elecTab);
    }
}
