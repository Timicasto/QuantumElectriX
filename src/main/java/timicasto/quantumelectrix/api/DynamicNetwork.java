package timicasto.quantumelectrix.api;

import com.sun.istack.internal.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import timicasto.quantumelectrix.INetData;
import timicasto.quantumelectrix.api.ints.IGridTransmitter;

import java.util.*;
import java.util.concurrent.DelayQueue;

public abstract class DynamicNetwork<ACCEPTOR, NETWORK extends DynamicNetwork<ACCEPTOR, NETWORK, BUFFER>, BUFFER> implements IClientTicker, INetData {
    protected Set<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> transmitters = new HashSet<>();
    protected Set<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> transmittersToAdd = new HashSet<>();
    protected Set<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> transmittersAdded = new HashSet<>();

    protected double meanCapacity = 0;
    protected Set<Coord4D> possibleAcceptors = new HashSet<>();
    protected Map<Coord4D, EnumSet<EnumFacing>> acceptorDirections = new HashMap<>();
    protected Map<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>, EnumSet<EnumFacing>> changedAcceptors = new HashMap<>();
    protected Range4D packetRange = null;
    protected int capacity = 0;
    protected double doubleCapacity = 0;
    protected boolean needUpdate = false;
    protected int updateDelay = 0;
    protected boolean firstUpdate = true;
    protected World world = null;
    private Set<DelayQueue> updateQueue = new LinkedHashSet<>();

    public void addNewTransmitter(Collection<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> newTransmitters) {
        transmittersToAdd.addAll(newTransmitters);
    }

    public void commit() {
        if (!transmittersToAdd.isEmpty()) {
            for (IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter : transmittersToAdd) {
                if (transmitter.isValid()) {
                    if (world == null) {
                        world = transmitter.world();
                    }

                    for (EnumFacing side : EnumFacing.VALUES) {
                        updateTransmitterOnSide(transmitter, side);
                    }
                    transmitter.setTransmitterNetwork((NETWORK)this);
                    absorbBuffer(transmitter);
                    transmitters.add(transmitter);
                }
            }

            updateCapacity();
            clampBuffer();
            queueClientUpdate(transmittersToAdd);
        }

        if (!changedAcceptors.isEmpty()) {
            for (Map.Entry<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>, EnumSet<EnumFacing>> entry : changedAcceptors.entrySet()) {
                IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter = entry.getKey();
                if (transmitter.isValid()) {
                    for (EnumFacing side : entry.getValue()) {
                        updateTransmitterOnSide(transmitter, side);
                    }
                }
            }
            changedAcceptors.clear();
        }
    }

    public void updateTransmitterOnSide(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter, EnumFacing facing) {
        ACCEPTOR acceptor = transmitter.getAcceptor(facing);
        Coord4D acceptorCoord = transmitter.coord().offset(facing);
        EnumSet<EnumFacing> directions = acceptorDirections.get(acceptorCoord);

        if (acceptor != null) {
            possibleAcceptors.add(acceptorCoord);
            if (directions != null) {
                directions.add(facing.getOpposite());
            } else {
                acceptorDirections.put(acceptorCoord, EnumSet.of(facing.getOpposite()));
            }
        } else if (directions != null) {
            directions.remove(facing.getOpposite());
            if (directions.isEmpty()) {
                possibleAcceptors.remove(acceptorCoord);
                acceptorDirections.remove(acceptorCoord);
            }
        } else {
            possibleAcceptors.remove(acceptorCoord);
            acceptorDirections.remove(acceptorCoord);
        }
    }

    @Nullable
    public BUFFER getBuffer() {
        return null;
    }

    public abstract void absorbBuffer(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter);

    public abstract void clampBuffer();

    public void invalidate() {
        transmitters.removeIf(transmitter -> !transmitter.isValid());

        clampBuffer();

        for (IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter : transmitters) {
            transmitter.updateShare();
        }

        for (IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter : transmitters) {
            invalidateTransmitter(transmitter);
        }

        transmitters.clear();
        deregister();
    }

    public void invalidateTransmitter(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter) {
        if (!world.isRemote && transmitter.isValid()) {
            transmitter.takeShare();
            transmitter.setTransmitterNetwork(null);
            TransmitterNetworkRegistry.registerOrphanTransmitter(transmitter);
        }
    }

    public void acceptorChanged(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter, EnumFacing facing) {
        EnumSet<EnumFacing> facings = changedAcceptors.get(transmitter);
        if (facings != null) {
            facings.add(facing);
        } else {
            changedAcceptors.put(transmitter, EnumSet.of(facing));
        }
        TransmitterNetworkRegistry.registerChangedNetwork(this);
    }

    public void adoptTransmittersAndAcceptorsFrom(NETWORK network) {
        for (IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter : network.transmitters) {
            transmitter.setTransmitterNetwork((NETWORK)this);
            transmitters.add(transmitter);
            transmittersAdded.add(transmitter);
        }
        transmittersAdded.addAll(network.transmittersToAdd);
        possibleAcceptors.addAll(network.possibleAcceptors);

        for (Map.Entry<Coord4D, EnumSet<EnumFacing>> entry : network.acceptorDirections.entrySet()) {
            Coord4D coord = entry.getKey();
            if (acceptorDirections.containsKey(coord)) {
                acceptorDirections.get(coord).addAll(entry.getValue());
            } else {
                acceptorDirections.put(coord, entry.getValue());
            }
        }
    }

    public Range4D getPacketRange() {
        return packetRange == null ? genPacketRange() : packetRange;
    }

    protected Range4D genPacketRange() {
        if (getSize() == 0) {
            deregister();
            return null;
        }
        IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> initTransmitter = transmitters.iterator().next();
        Coord4D initCoord = initTransmitter.coord();

        int minX = initCoord.x;
        int minY = initCoord.y;
        int minZ = initCoord.z;
        int maxX = initCoord.x;
        int maxY = initCoord.y;
        int maxZ = initCoord.z;

        for (IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter : transmitters) {
            Coord4D coord4D = transmitter.coord();
            if (coord4D.x < minX) {
                minX = coord4D.x;
            } else if (coord4D.x > maxX) {
                maxX = coord4D.x;
            }
            if (coord4D.y < minY) {
                minY = coord4D.y;
            } else if (coord4D.y > maxY) {
                maxY = coord4D.y;
            }
            if (coord4D.z < minZ) {
                minZ = coord4D.z;
            } else if (coord4D.z > maxZ) {
                maxZ = coord4D.z;
            }
        }
        return new Range4D(minX, minY, minZ, maxX, maxY, maxZ, initTransmitter.world().provider.getDimension());
    }

    public void register() {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            TransmitterNetworkRegistry.instance.registerNetwork(this);
        } else {
            MinecraftForge.EVENT_BUS.post(new ClientTickUpdate(this, (byte) 1));
        }
    }

    public void deregister() {
        transmitters.clear();
        transmittersAdded.clear();
        transmittersToAdd.clear();

        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            TransmitterNetworkRegistry.instance.removeNetwork(this);
        } else {
            MinecraftForge.EVENT_BUS.post(new ClientTickUpdate(this, (byte) 0));
        }
    }

    public int getSize() {
        return transmitters.size();
    }

    public int getAcceptorSize() {
        return possibleAcceptors.size();
    }

    public synchronized void updateCapacity() {
        doubleCapacity = transmitters.stream().mapToDouble(IGridTransmitter::getCapacity).sum();
        capacity = doubleCapacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) doubleCapacity;
        updateMeanCapacity();
    }

    protected synchronized void updateMeanCapacity() {
        meanCapacity = transmitters.size() > 0 ? doubleCapacity / transmitters.size() : 0;
    }

    public int getCapacity() {
        return capacity;
    }

    public double getCapacityAsDouble() {
        return doubleCapacity;
    }

    public World getWorld() {
        return world;
    }

    public void tick() {
        onUpdate();
    }

    public void onUpdate() {
        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            Iterator<DelayQueue> iterator = updateQueue.iterator();

            try {
                while (iterator.hasNext()) {
                    DelayQueue queue = iterator.next();
                    if (queue.delay > 0) {
                        queue.delay--;
                    } else {
                        transmittersAdded.addAll(transmitters);
                        updateDelay = 1;
                        iterator.remove();
                    }
                }
            } catch (Exception ignore) {}

            if (updateDelay > 0) {
                updateDelay--;
                if (updateDelay == 0) {
                    MinecraftForge.EVENT_BUS.post(new TransmittersAddedEvent(this, firstUpdate, (Collection)transmittersAdded));
                    firstUpdate = false;
                    transmittersAdded.clear();
                    needUpdate = true;
                }
            }
        }
    }

    @Override
    public boolean needTicks() {
        return getSize() > 0;
    }

    @Override
    public void clientTick() {

    }

    public void queueClientUpdate(Collection<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> transmitters) {
        transmittersAdded.addAll(transmitters);
        updateDelay = 5;
    }

    public void addUpdate(EntityPlayer player) {
        updateQueue.add(new DelayQueue(player));
    }

    public boolean isCompatibleWith(NETWORK other) {
        return true;
    }

    public boolean compatibleWithBuffer(BUFFER buffer) {
        return true;
    }

    public Set<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> getTransmitters() {
        return transmitters;
    }

    public boolean addTransmitter(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter) {
        return transmitters.remove(transmitter);
    }

    public boolean removeTransmitter(IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> transmitter) {
        boolean removed = transmitters.remove(transmitters);
        if (transmitters.isEmpty()) {
            deregister();
        }
        return removed;
    }

    public IGridTransmitter<ACCEPTOR, NETWORK, BUFFER> firstTransmitter() {
        return transmitters.iterator().next();
    }

    public int transmittersSize() {
        return transmitters.size();
    }

    public Set<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> getTransmittersToAdd() {
        return transmittersToAdd;
    }

    public Set<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>> getTransmittersAdded() {
        return transmittersAdded;
    }

    public Set<Coord4D> getPossibleAcceptors() {
        return possibleAcceptors;
    }

    public Map<Coord4D, EnumSet<EnumFacing>> getAcceptorDirections() {
        return acceptorDirections;
    }

    public Map<IGridTransmitter<ACCEPTOR, NETWORK, BUFFER>, EnumSet<EnumFacing>> getChangedAcceptors() {
        return changedAcceptors;
    }

    public static class TransmittersAddedEvent extends Event {
        public DynamicNetwork<?, ?, ?> network;
        public boolean newNetwork;
        public Collection<IGridTransmitter> newTransmitters;

        public TransmittersAddedEvent(DynamicNetwork net, boolean newNet, Collection<IGridTransmitter> added) {
            network = net;
            newNetwork = newNet;
            newTransmitters = added;
        }
    }

    public static class ClientTickUpdate extends Event {
        public DynamicNetwork network;
        public byte operation; // 0 = remove, 1 = add
        public ClientTickUpdate(DynamicNetwork net, byte b) {
            network = net;
            operation = b;
        }
    }

    public static class DelayQueue {
        public EntityPlayer player;
        public int delay;
        public DelayQueue(EntityPlayer player1) {
            player = player1;
            delay = 5;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof DelayQueue && ((DelayQueue)o).player.equals(this.player);
        }

        @Override
        public int hashCode() {
            return player.hashCode();
        }
    }


}



























