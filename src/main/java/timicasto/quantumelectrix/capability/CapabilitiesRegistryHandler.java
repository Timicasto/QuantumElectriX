package timicasto.quantumelectrix.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import timicasto.quantumelectrix.api.ints.IGridTransmitter;

public class CapabilitiesRegistryHandler {
    @CapabilityInject(IGridTransmitter.class)
    public static Capability<IGridTransmitter> GRID_TRANSMITTER = null;

    public static void registerCapabilities() {
        DefaultGridTransmitterImpl.register();
    }
}
