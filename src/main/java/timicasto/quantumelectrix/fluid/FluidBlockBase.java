package timicasto.quantumelectrix.fluid;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class FluidBlockBase extends BlockFluidClassic {
    public FluidBlockBase(Fluid fluid) {
        super(fluid, Material.WATER);
        setUnlocalizedName(fluid.getName());
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }
}
