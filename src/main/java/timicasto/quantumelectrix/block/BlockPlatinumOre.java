package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockPlatinumOre extends Block {
    public BlockPlatinumOre() {
        super(Material.ROCK);
        setRegistryName("platinum_ore");
        setUnlocalizedName("platinum_ore");
        setCreativeTab(TabLoader.elecTab);
    }
}
