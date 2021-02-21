package timicasto.quantumelectrix;

import net.minecraft.init.Items;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.network.PacketHandler;
import timicasto.quantumelectrix.proxy.CommonProxy;

@Mod(modid = QuantumElectriX.MODID, name = QuantumElectriX.NAME, version = QuantumElectriX.VERSION)
public class QuantumElectriX {
    public static final String MODID = "quantumelectrix";
    public static final String NAME = "Quantum ElectriX";
    public static final String VERSION = "Pre-1.0";
    private static Logger logger = LogManager.getLogger();

    private SimpleNetworkWrapper networkWrapper;

    static {
        FluidRegistry.enableUniversalBucket();
    }

    @Mod.Instance(QuantumElectriX.MODID)
    public static QuantumElectriX instance;
    
    @SidedProxy(clientSide = "timicasto.quantumelectrix.proxy.ClientProxy", serverSide = "timicasto.quantumelectrix.proxy.CommonProxy")
    public static CommonProxy proxy;

    // Mod Packet Pipeline
    public static PacketHandler handler = new PacketHandler();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        instance = this;
        proxy.preInit(event);
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    public static SimpleNetworkWrapper getNetwork() {
        return instance.networkWrapper;
    }

}
