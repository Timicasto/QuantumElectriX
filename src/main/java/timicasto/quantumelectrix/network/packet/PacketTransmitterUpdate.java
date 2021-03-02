package timicasto.quantumelectrix.network.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import timicasto.quantumelectrix.api.Coord4D;
import timicasto.quantumelectrix.api.DynamicNetwork;
import timicasto.quantumelectrix.api.TransmittionType;
import timicasto.quantumelectrix.api.ints.IGridTransmitter;
import timicasto.quantumelectrix.capability.CapabilitiesRegistryHandler;
import timicasto.quantumelectrix.capability.CapabilityUtils;
import timicasto.quantumelectrix.network.PacketHandler;

import java.util.Collection;
import java.util.HashSet;

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
            TileEntity tile = message.coord4D.getTileEntity(player.world);
            if (CapabilityUtils.hasCapability(tile, CapabilitiesRegistryHandler.GRID_TRANSMITTER, null)) {
                IGridTransmitter transmitter = CapabilityUtils.getCapability(tile, CapabilitiesRegistryHandler.GRID_TRANSMITTER, null);
                if (transmitter == null) {
                    return;
                }
                if (message.packetTypes == PacketTypes.UPDATE) {
                    DynamicNetwork network = transmitter.hasTransmitterNetwork() && !message.newNetwork ? transmitter.getTransmitterNetwork() : transmitter.createEmptyNetwork();
                    network.register();
                    transmitter.setTransmitterNetwork(network);
                    for (Coord4D coord : message.coord4DS) {
                        TileEntity tile1 = coord.getTileEntity(player.world);
                        if (CapabilityUtils.hasCapability(tile1, CapabilitiesRegistryHandler.GRID_TRANSMITTER, null)) {
                            CapabilityUtils.getCapability(tile1, CapabilitiesRegistryHandler.GRID_TRANSMITTER, null).setTransmitterNetwork(network);
                        }
                    }
                    network.updateCapacity();
                    return;
                }
                if (!transmitter.hasTransmitterNetwork()) {
                    return;
                }
                TransmittionType type = transmitter.getTransmissionType();

            }
        }, player);
        return null;
    }

    public enum PacketTypes {
        UPDATE
    }

    public static class TransmitterUpdateMessage implements IMessage {
        public PacketTypes packetTypes;
        public Coord4D coord4D;
        public double power;
        public boolean newNetwork;
        public Collection<IGridTransmitter> transmittersAdded;
        public Collection<Coord4D> coord4DS;

        public TransmitterUpdateMessage() {}

        public TransmitterUpdateMessage(PacketTypes type, Coord4D vector, Object... data) {
            packetTypes = type;
            coord4D = vector;
            switch (packetTypes) {
                case UPDATE:
                    newNetwork = (boolean) data[0];
                    transmittersAdded = (Collection<IGridTransmitter>) data[1];
                    break;
                default:
                    break;
            }
        }

        @Override
        public void toBytes(ByteBuf buf) {
            buf.writeInt(packetTypes.ordinal());
            coord4D.write(buf);
            PacketHandler.log("Sending a '" + packetTypes + "' update signal from vec " + coord4D);
            switch (packetTypes) {
                case UPDATE:
                    buf.writeBoolean(newNetwork);
                    buf.writeInt(transmittersAdded.size());
                    for (IGridTransmitter transmitter : transmittersAdded) {
                        transmitter.coord().write(buf);
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            packetTypes = PacketTypes.values()[buf.readInt()];
            coord4D = Coord4D.read(buf);
            if (packetTypes == PacketTypes.UPDATE) {
                newNetwork = buf.readBoolean();
                coord4DS = new HashSet<>();
                int transmittersCount = buf.readInt();
                for (int i = 0 ; i < transmittersCount ; i++) {
                    coord4DS.add(Coord4D.read(buf));
                }
            }
        }
    }
}
