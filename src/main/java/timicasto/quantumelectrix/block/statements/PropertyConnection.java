package timicasto.quantumelectrix.block.statements;

import net.minecraftforge.common.property.IUnlistedProperty;
import timicasto.quantumelectrix.tile.TileEntityAbsWire;

import java.util.Arrays;

public class PropertyConnection implements IUnlistedProperty<PropertyConnection> {
    public static PropertyConnection INSTANCE = new PropertyConnection();
    public byte connection;
    public byte transmitterConnections;
    public TileEntityAbsWire.ConnectionType[] connectionTypes;
    public boolean renderCenter;

    public PropertyConnection() {

    }

    public PropertyConnection(byte b, byte b1, TileEntityAbsWire.ConnectionType[] types, boolean center) {
        connection = b;
        transmitterConnections = b1;
        connectionTypes = types;
        renderCenter = center;
    }

    @Override
    public String getName() {
        return "connections";
    }

    @Override
    public boolean isValid(PropertyConnection value) {
        return true;
    }

    @Override
    public Class<PropertyConnection> getType() {
        return PropertyConnection.class;
    }

    @Override
    public String valueToString(PropertyConnection value) {
        return value.connection + "." + value.transmitterConnections + "." + Arrays.toString(value.connectionTypes) + "." + value.renderCenter;
    }
}
