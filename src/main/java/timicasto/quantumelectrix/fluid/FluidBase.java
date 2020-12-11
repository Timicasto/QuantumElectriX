package timicasto.quantumelectrix.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public class FluidBase extends Fluid {
    public FluidBase(String fluidName, ResourceLocation still, ResourceLocation flow, java.awt.Color color, int density, int viscosity) {
        super(fluidName, still, flow, color);
        setUnlocalizedName(fluidName);
        setDensity(density);
        setViscosity(viscosity);
        setTemperature(350);
    }
}
