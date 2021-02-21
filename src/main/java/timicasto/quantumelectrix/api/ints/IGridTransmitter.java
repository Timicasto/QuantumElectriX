package timicasto.quantumelectrix.api.ints;

import com.sun.istack.internal.Nullable;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import timicasto.quantumelectrix.api.Coord4D;
import timicasto.quantumelectrix.api.DynamicNetwork;

import java.util.Collection;

public interface IGridTransmitter<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, BUFFER>, BUFFER> extends ITransmitter {
    boolean hasTransmitterNetwork();

    /**
     * Gets the network in use by this transmitter
     *
     * @return network this transmitter is using
     */
    NETWORK getTransmitterNetwork();

    /**
     * Sets the transmitter's network to a new value
     *
     * @param network — network to set to
     */
    void setTransmitterNetwork(NETWORK network);

    void setRequestsUpdate();

    int getTransmitterNetworkSize();

    int getTransmitterNetworkAcceptorSize();

    String getTransmitterNetworkNeeded();
    String getTransmitterNetworkFlow();

    String getTransmitterNetworkBuffer();

    double getTransmitterNetworkCapacity();

    int getCapacity();

    World world();

    Coord4D coord();

    Coord4D getAdjacentConnectableTransmitterCoord(EnumFacing side);

    ACCEPTOR getAcceptor(EnumFacing side);

    boolean isValid();

    boolean isOrphan();

    void setOrphan(boolean orphaned);

    NETWORK createEmptyNetwork();

    NETWORK mergeNetworks(Collection<NETWORK> toMerge);

    NETWORK getExternalNetwork(Coord4D from);

    void takeShare();

    void updateShare();

    /**
     * @return The transmitter's buffer.
     */
    @Nullable
    BUFFER getBuffer();

    /**
     * If the transmitter does not have a buffer this will try to fallback on the network's buffer.
     *
     * @return The transmitter's buffer, or if null the network's buffer.
     */
    @Nullable
    default BUFFER getBufferWithFallback() {
        BUFFER buffer = getBuffer();
        //If we don't have a buffer try falling back to the network's buffer
        if (buffer == null && hasTransmitterNetwork()) {
            return getTransmitterNetwork().getBuffer();
        }
        return buffer;
    }

    default boolean isCompatibleWith(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> other) {
        return true;
    }

    /**
     * Gets called on an orphan if at least one attempted network fails to connect due to having connected to another network that is incompatible with the next attempted
     * ones.
     *
     * This is primarily used for if extra handling needs to be done, such as refreshing the connections visually on a minor delay so that it has time to have the buffer
     * no longer be null and properly compare the connection.
     */
    default void connectionFailed() {
    }
}
