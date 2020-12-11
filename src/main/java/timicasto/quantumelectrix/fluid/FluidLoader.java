package timicasto.quantumelectrix.fluid;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.QuantumElectriX;

import java.awt.*;

public class FluidLoader {
    private static final Logger logger = LogManager.getLogger();
    public static final Fluid chemistrySO2 = new FluidBase("SO2", new ResourceLocation(QuantumElectriX.MODID, "so2_still"), new ResourceLocation(QuantumElectriX.MODID, "so2_flow"), new Color(230, 233, 213), 3, 400);

    public static void registryFluids() {
        registerFluid(chemistrySO2);
    }

    public static void registerFluid(Fluid fluid) {
        FluidRegistry.registerFluid(fluid);
        if (FluidRegistry.isFluidRegistered(fluid)) {
            logger.info(fluid + "IS ALREADY REGISTRIED");
        }
        if (FluidRegistry.addBucketForFluid(fluid)) {
            logger.info("ADDED Bucket for : " + fluid);
        } else {
            logger.error("ONE or MORE ERRORS OCCURRED DURING AddBucket for  " + fluid);
        }
    }

}
