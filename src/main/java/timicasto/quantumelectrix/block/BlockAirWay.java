package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import timicasto.quantumelectrix.creative.TabLoader;

import java.util.Random;

public class BlockAirWay extends Block {
    public BlockAirWay() {
        super(Material.GLASS);
        setRegistryName("air_way");
        setUnlocalizedName("air_way");
        setCreativeTab(TabLoader.elecTab);
    }
}
