package timicasto.quantumelectrix.network;

import io.netty.buffer.ByteBuf;

public abstract class PacketBase implements IPacket{
    public static final int INVALID_DIMENSION_ID = Integer.MIN_VALUE + 12;
    private int dimension;

    public PacketBase() {
        this.dimension = INVALID_DIMENSION_ID;
    }

    public PacketBase(int dimension) {
        this.dimension = dimension;
    }

    @Override
    public void encodeInto(ByteBuf buf) {
        if (dimension == INVALID_DIMENSION_ID) {
            throw new IllegalStateException("[QuantumElectriX/FATAL ERROR]Invalid Dimension ID! ");
        }
        buf.writeInt(this.dimension);
    }

    @Override
    public void decodeInto(ByteBuf buf) {
        this.dimension = buf.readInt();
    }

    @Override
    public int getDimensionID() {
        return dimension;
    }
}














