package timicasto.quantumelectrix.api.ints;

import io.netty.buffer.ByteBuf;
import timicasto.quantumelectrix.api.TileNetworkList;

public interface INetworkTile {
    void handlePacketData(ByteBuf buf) throws Exception;
    TileNetworkList getNetworkedData(TileNetworkList data);
    default TileNetworkList getNetworkedData() {
        return getNetworkedData(new TileNetworkList());
    }
}
