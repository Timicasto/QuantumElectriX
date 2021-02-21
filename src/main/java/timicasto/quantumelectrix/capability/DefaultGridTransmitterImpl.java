package timicasto.quantumelectrix.capability;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.CapabilityManager;
import timicasto.quantumelectrix.api.Coord4D;
import timicasto.quantumelectrix.api.DynamicNetwork;
import timicasto.quantumelectrix.api.TransmittionType;
import timicasto.quantumelectrix.api.ints.IGridTransmitter;

import java.util.Collection;

public class DefaultGridTransmitterImpl<A, N extends DynamicNetwork<A, N, BUFFER>, BUFFER> implements IGridTransmitter<A, N, BUFFER> {

    public static void register() {
        CapabilityManager.INSTANCE.register(IGridTransmitter.class, new DefaultStorageHelper.NullStorage<>(), DefaultGridTransmitterImpl::new);
    }

    @Override
    public boolean hasTransmitterNetwork() {
        return false;
    }

    @Override
    public N getTransmitterNetwork() {
        return null;
    }

    @Override
    public void setTransmitterNetwork(N network) {

    }

    @Override
    public int getTransmitterNetworkSize() {
        return 0;
    }

    @Override
    public int getTransmitterNetworkAcceptorSize() {
        return 0;
    }

    @Override
    public String getTransmitterNetworkNeeded() {
        return null;
    }

    @Override
    public String getTransmitterNetworkFlow() {
        return null;
    }

    @Override
    public String getTransmitterNetworkBuffer() {
        return null;
    }

    @Override
    public double getTransmitterNetworkCapacity() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 0;
    }

    @Override
    public World world() {
        return null;
    }

    @Override
    public Coord4D coord() {
        return null;
    }

    @Override
    public Coord4D getAdjacentConnectableTransmitterCoord(EnumFacing side) {
        return null;
    }

    @Override
    public A getAcceptor(EnumFacing side) {
        return null;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public boolean isOrphan() {
        return false;
    }

    @Override
    public void setOrphan(boolean orphaned) {

    }

    @Override
    public N createEmptyNetwork() {
        return null;
    }

    @Override
    public N mergeNetworks(Collection<N> toMerge) {
        return null;
    }

    @Override
    public N getExternalNetwork(Coord4D from) {
        return null;
    }

    @Override
    public void takeShare() {

    }

    @Override
    public void updateShare() {

    }

    @Override
    public BUFFER getBuffer() {
        return null;
    }

    @Override
    public void setRequestsUpdate() {

    }

    @Override
    public TransmittionType getTransmissionType() {
        return null;
    }

    @Override
    public boolean isCompatibleWith(IGridTransmitter<A, N, BUFFER> other) {
        return false;
    }


}
