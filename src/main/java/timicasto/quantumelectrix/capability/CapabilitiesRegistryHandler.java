package timicasto.quantumelectrix.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import timicasto.quantumelectrix.api.ints.IBlockableConnect;
import timicasto.quantumelectrix.api.ints.IConfigable;
import timicasto.quantumelectrix.api.ints.IGridTransmitter;
import timicasto.quantumelectrix.api.ints.INetworkTile;

public class CapabilitiesRegistryHandler {
    @CapabilityInject(IGridTransmitter.class)
    public static Capability<IGridTransmitter> GRID_TRANSMITTER = null;

    @CapabilityInject(IBlockableConnect.class)
    public static Capability<IBlockableConnect> BLOCKABLE_CONNECT = null;

    @CapabilityInject(IConfigable.class)
    public static Capability<IConfigable> CONFIGABLE = null;

    @CapabilityInject(INetworkTile.class)
    public static Capability<INetworkTile> NETWORK_TILE = null;

    public static void registerCapabilities() {
        DefaultGridTransmitterImpl.register();
    }
}
