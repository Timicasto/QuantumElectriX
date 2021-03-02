package timicasto.quantumelectrix.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import timicasto.quantumelectrix.api.Coord4D;
import timicasto.quantumelectrix.api.TileNetworkList;
import timicasto.quantumelectrix.api.ints.INetworkTile;
import timicasto.quantumelectrix.network.PacketHandler;

public class PacketTileEntity {

    public static class TileEntityMessage implements IMessage {
        public Coord4D coord4D;
        public TileNetworkList params;
        public ByteBuf buf = null;

        public TileEntityMessage() {}

        public <TILE extends TileEntity & INetworkTile> TileEntityMessage(TILE tile) {
            this(Coord4D.get(tile), tile.getNetworkedData());
        }

        public TileEntityMessage(TileEntity tile, TileNetworkList params) {
            this(Coord4D.get(tile), params);
        }
        public TileEntityMessage(Coord4D coord, TileNetworkList params) {
            coord4D = coord;
            this.params = params;
        }

        @Override
        public void toBytes(ByteBuf buf) {
            coord4D.write(buf);
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            if (server != null) {
                World world = server.getWorld(coord4D.dimensionId);
                PacketHandler.log("Sending TE packet from coord " + coord4D + " to " + coord4D.getTileEntity(world));
            }
            PacketHandler.encode(params.toArray(), buf);
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            coord4D = Coord4D.read(buf);
            this.buf = buf.copy();
        }
    }

}
