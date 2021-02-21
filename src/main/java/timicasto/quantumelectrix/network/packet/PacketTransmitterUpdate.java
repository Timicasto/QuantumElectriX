package timicasto.quantumelectrix.network.packet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import timicasto.quantumelectrix.api.Coord4D;
import timicasto.quantumelectrix.network.PacketHandler;

import java.util.Collection;

public class PacketTransmitterUpdate implements IMessageHandler<PacketTransmitterUpdate.TransmitterUpdateMessage, IMessage> {
    @Override
    public IMessage onMessage(TransmitterUpdateMessage message, MessageContext ctx) {
        EntityPlayer player = PacketHandler.getPlayer(ctx);
        if (player == null) {
            return null;
        }

        PacketHandler.handlePacket(() -> {
            if (message.coord4D == null) {
                return;
            }
        });
    }

    public static class TransmitterUpdateMessage implements IMessage {
        public Coord4D coord4D;
        public double power;
        public boolean newNetwork;
        public Collection<IGridTransmitter> transmittersAdded;
        public Collection<Coord4D> coord4DS;
    }
}
