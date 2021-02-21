package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.AxisAlignedBB;
import timicasto.quantumelectrix.creative.TabLoader;

public class BlockWire extends Block implements ITileEntityProvider {
    public BlockWire() {
        super(Material.PISTON);
        setCreativeTab(TabLoader.elecTab);
        setHardness(1F);
        setResistance(1F);
    }

    private static AxisAlignedBB getDefaultForTile(TileEntity) {
    }
}
