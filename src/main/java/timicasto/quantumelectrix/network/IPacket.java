package timicasto.quantumelectrix.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public interface IPacket {
    void encodeInto(ByteBuf buf);
    void decodeInto(ByteBuf buf);
    void handlerClientSide(EntityPlayer player);
    void handlerServerSide(EntityPlayer player);
    int getDimensionID();
}
