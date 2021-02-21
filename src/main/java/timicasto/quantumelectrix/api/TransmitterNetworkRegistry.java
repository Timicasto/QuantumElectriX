package timicasto.quantumelectrix.api;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.api.ints.IGridTransmitter;

import java.util.*;

public class TransmitterNetworkRegistry {
    public static TransmitterNetworkRegistry instance = new TransmitterNetworkRegistry();
    private static boolean loaderRegistered = false;
    private static Logger logger = LogManager.getLogger();
    private Set<DynamicNetwork> networks = new HashSet<>();
    private Set<DynamicNetwork> networksToChange = new HashSet<>();
    private Set<IGridTransmitter> invalidTransmitters = new HashSet<>();
    private Map<Coord4D, IGridTransmitter> orphanTransmitters = new HashMap<>();
    private Map<Coord4D, IGridTransmitter> newOrphanTransmitters = new HashMap<>();

    public static void initiate() {
        if (!loaderRegistered) {
            loaderRegistered = true;
            MinecraftForge.EVENT_BUS.register(instance);
        }
    }

    public static void reset() {
        instance.networks.clear();
        instance.networksToChange.clear();
        instance.invalidTransmitters.clear();
        instance.orphanTransmitters.clear();
        instance.newOrphanTransmitters.clear();
    }

    public static void invalidateTransmitter(IGridTransmitter transmitter) {
        instance.invalidTransmitters.add(transmitter);
    }

    public static void registerOrphanTransmitter(IGridTransmitter transmitter) {
        Coord4D coord = transmitter.coord();
        IGridTransmitter previous = instance.newOrphanTransmitters.put(coord, transmitter);
        if (previous != null && previous != transmitter) {
            logger.error("Different orphan transmitter was already registered at location {}", coord.toString());;
        }
    }

    public static void registerChangedNetwork(DynamicNetwork network) {
        instance.networksToChange.add(network);
    }

    public void registerNetwork(DynamicNetwork network) {
        networks.add(network);
    }

    public void removeNetwork(DynamicNetwork network) {
        networks.remove(network);
        networksToChange.remove(network);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side == Side.SERVER) {
            tickEnd();
        }
    }

    public void tickEnd() {
        removeInvalidTransmitters();
        assignOrphans();
        commitChanges();
        for (DynamicNetwork net : networks) {
            net.tick();
        }
    }

    public void removeInvalidTransmitters() {
        if (!invalidTransmitters.isEmpty()) {
            logger.info("Dealing with " + invalidTransmitters.size() + "invalid transmitters");
        }

        for (IGridTransmitter invalid : invalidTransmitters) {
            if (!(invalid.isOrphan() && invalid.isValid())) {
                DynamicNetwork net = invalid.getTransmitterNetwork();
                if (net != null) {
                    net.invalidate();
                }
            }
        }
        invalidTransmitters.clear();
    }

    public void assignOrphans() {
        orphanTransmitters = new HashMap<>(newOrphanTransmitters);
        newOrphanTransmitters.clear();
        if (!orphanTransmitters.isEmpty()) {
            logger.info("Dealing with " + orphanTransmitters.size() + " orphan transmitters");
        }

        for (IGridTransmitter orphanTransmitter : new HashMap<>(orphanTransmitters).values()) {
            DynamicNetwork network = getNetworkFromOrphan(orphanTransmitter);
            if (network != null) {
                networksToChange.add(network);
                network.register();
            }
        }
        orphanTransmitters.clear();
    }

    public <A, N extends DynamicNetwork<A, N, BUFFER>, BUFFER> DynamicNetwork<A, N, BUFFER> getNetworkFromOrphan(IGridTransmitter<A, N, BUFFER> startOrphan) {
        if (startOrphan.isValid() && startOrphan.isOrphan()) {
            OrphanPathFinder<A, N, BUFFER> finder = new OrphanPathFinder<>(startOrphan);
            finder.start();
            N network;
            switch (finder.networksFound.size()) {
                case 0:
                    network = startOrphan.createEmptyNetwork();
                    break;
                case 1:
                    network = finder.networksFound.iterator().next();
                    break;
                default :
                    network = startOrphan.mergeNetworks(finder.networksFound);
            }
            network.addNewTransmitter(finder.connectedTransmitters);
            if (finder.networksFailed) {
                startOrphan.connectionFailed();
            }
            return network;
        }
        return null;
    }

    public void commitChanges() {
        for (DynamicNetwork network : networksToChange) {
            network.commit();
        }
        networksToChange.clear();
    }

    @Override
    public String toString() {
        return "Network Registry:\n" + networks;
    }

    public String[] toStrings() {
        String[] strings = new String[networks.size()];
        int i = 0;
        for (DynamicNetwork network : networks) {
            strings[i++] = network.toString();
        }
        return strings;
    }

    public class OrphanPathFinder<A, N extends DynamicNetwork<A, N, BUFFER>, BUFFER> {
        public IGridTransmitter<A, N, BUFFER> startPoint;
        public HashSet<Coord4D> iterated = new HashSet<>();
        public HashSet<IGridTransmitter<A, N, BUFFER>> connectedTransmitters = new HashSet<>();
        public HashSet<N> networksFound = new HashSet<>();
        private Deque<Coord4D> queue = new LinkedList<>();
        public boolean networksFailed;

        public OrphanPathFinder(IGridTransmitter<A, N, BUFFER> start) {
            startPoint = start;
        }

        public void start() {
            if (queue.peek() != null) {
                logger.error("OrphanPathFinder queue was not empty..?");
                queue.clear();
            }
            queue.push(startPoint.coord());
            while (queue.peek() != null) {
                iterate(queue.removeFirst());
            }
        }

        public void iterate(Coord4D from) {
            if (iterated.contains(from)) {
                return;
            }
            iterated.add(from);

            if (orphanTransmitters.containsKey(from)) {
                IGridTransmitter<A, N, BUFFER> transmitter = orphanTransmitters.get(from);
                if (transmitter.isValid() && transmitter.isOrphan() && (connectedTransmitters.isEmpty() || connectedTransmitters.stream().anyMatch(existing -> existing.isCompatibleWith(transmitter)))) {
                    connectedTransmitters.add(transmitter);
                    transmitter.setOrphan(false);

                    for (EnumFacing facing : EnumFacing.VALUES) {
                        if (facing.getAxis().isHorizontal() && !transmitter.world().isBlockLoaded(from.getPos().offset(facing))) {
                            continue;
                        }
                        Coord4D coord = transmitter.getAdjacentConnectableTransmitterCoord(facing);
                        if (coord !=  null && !iterated.contains(coord)) {
                            queue.addLast(coord);
                        }
                    }
                }
            } else {
                addNetworkToIterated(from);
            }
        }

        public void addNetworkToIterated(Coord4D from) {
            N net = startPoint.getExternalNetwork(from);
            if (net != null && net.compatibleWithBuffer(startPoint.getBuffer())) {
                if (networksFound.isEmpty() || networksFound.iterator().next().isCompatibleWith(net)) {
                    networksFound.add(net);
                } else {
                    networksFailed = true;
                }
            }
        }
    }
}
