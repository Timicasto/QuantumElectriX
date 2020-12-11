package timicasto.quantumelectrix.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import timicasto.quantumelectrix.GuiHandler;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.api.CraftHandler;
import timicasto.quantumelectrix.creative.TabLoader;
import timicasto.quantumelectrix.fluid.FluidLoader;
import timicasto.quantumelectrix.world.OreGen;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event)
    {
        new TabLoader(event);
        FluidLoader.registryFluids();
    }

    public void init(FMLInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(QuantumElectriX.instance, new GuiHandler());
        GameRegistry.registerWorldGenerator(new OreGen(), 0);
        new CraftHandler();
        RegistryHandler.init();

    }

    public void postInit(FMLPostInitializationEvent event)
    {
        
    }
}
