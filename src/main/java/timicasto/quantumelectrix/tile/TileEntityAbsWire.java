package timicasto.quantumelectrix.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.api.Tiers;
import timicasto.quantumelectrix.api.ints.IBlockableConnect;
import timicasto.quantumelectrix.api.ints.INetworkTile;
import timicasto.quantumelectrix.api.ints.ITransmitter;

public abstract class TileEntityAbsWire extends TileEntity implements ITickable, IBlockableConnect, ITransmitter, INetworkTile {
    public int delay;
    public byte acceptorConnections = 0x00;
    public byte transmitterConnections = 0x00;
    public boolean sendDesc = false;
    private boolean powered = false;
    private boolean reactive = false;
    public boolean update = true;
    public boolean rsSet = false;
    public NetworkDispatcher.ConnectionType[] connectionTypes = {NetworkDispatcher.ConnectionType.MODDED, NetworkDispatcher.ConnectionType.MODDED, NetworkDispatcher.ConnectionType.MODDED, NetworkDispatcher.ConnectionType.MODDED, NetworkDispatcher.ConnectionType.MODDED, NetworkDispatcher.ConnectionType.MODDED};
    public TileEntity[] cachedAcceptors = new TileEntity[6];

    public static boolean connectionMapContain(byte connections, EnumFacing facing) {
        byte test = (byte) (1 << facing.ordinal());
        return (connections & test) > 0;
    }

    public static byte setConnectionByte(byte connections, boolean tar, EnumFacing facing) {
        return (byte)((connections & ~(byte) (1 << facing.ordinal())) | (byte)((tar ? 1 : 0) << facing.ordinal()));
    }

    public static NetworkDispatcher.ConnectionType getConnectionType(EnumFacing facing, byte allConnections, byte transmitterConnections, NetworkDispatcher.ConnectionType[] types) {
        if (!connectionMapContain(allConnections, facing)) {
            return NetworkDispatcher.ConnectionType.VANILLA;
        } else if (connectionMapContain(transmitterConnections, facing)) {
            return NetworkDispatcher.ConnectionType.MODDED;
        }
        return types[facing.ordinal()];
    }

    @Override
    public void update() {
        if (world.isRemote) {
            if (delay == 5) {
                delay = 6;
                refreshConnections();
            } else if (delay < 5) {
                delay++;
            }
        } else {
            if (update) {
                refreshConnections();
                update = false;
            }
            if (sendDesc) {
                QuantumElectriX.packetHandler.sendToAll(this);
                sendDesc = false;
            }
        }
    }

    public Tiers getTiers() {
        return Tiers.max2A;
    }

    public void setTier(Tiers tier) {

    }

    public boolean handlesRedstone() {
        return true;
    }

    public boolean renderCenter() {
        return false;
    }

    public byte getPossibleTransmitterConnections() {
        byte connections = 0x00;
        if (handlesRedstone() && reactive && powered) {
            return connections;
        }
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (canConnect(facing)) {
                TileEntity tile = world.getTileEntity(getPos().offset(facing));
                if ()
            }
        }
    }
}
