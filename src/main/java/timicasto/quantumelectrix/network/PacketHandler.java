package timicasto.quantumelectrix.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.api.ints.INetworkTile;
import timicasto.quantumelectrix.network.packet.PacketTileEntity;
import timicasto.quantumelectrix.network.packet.PacketTransmitterUpdate;

import java.util.ArrayList;
import java.util.Arrays;

public class PacketHandler {
    public SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("QEX");

    private static final Logger logger = LogManager.getLogger("network");

    public static void encode(Object[] data, ByteBuf out) {
        for (Object data1 : data) {
            if (data1 instanceof Byte) {
                out.writeByte((Byte)data1);
            } else if (data1 instanceof Integer) {
                out.writeInt((Integer)data1);
            } else if (data1 instanceof Short) {
                out.writeShort((Short)data1);
            } else if (data1 instanceof Long) {
                out.writeLong((Long)data1);
            } else if (data1 instanceof Boolean) {
                out.writeBoolean((Boolean)data1);
            } else if (data1 instanceof Double) {
                out.writeDouble((Double)data1);
            } else if (data1 instanceof Float) {
                out.writeFloat((Float)data1);
            } else if (data1 instanceof String) {
                writeString(out, (String)data1);
            } else if (data1 instanceof EnumFacing) {
                out.writeInt(((EnumFacing) data1).ordinal());
            } else if (data1 instanceof ItemStack) {
                writeStack(out, (ItemStack)data1);
            } else if (data1 instanceof NBTTagCompound) {
                writeNBT(out, (NBTTagCompound)data1);
            } else if (data1 instanceof int[]) {
                for (int i : (int[])data1) {
                    out.writeInt(i);
                }
            } else if (data1 instanceof byte[]) {
                for (byte b : (byte[])data1) {
                    out.writeByte(b);
                }
            } else if (data1 instanceof ArrayList) {
                encode(((ArrayList<?>) data1).toArray(), out);
            } else if (data1 instanceof NonNullList) {
                encode(((NonNullList<?>) data1).toArray(), out);
            } else {
                throw new RuntimeException("Unencodable data!  " + data1 + ", full ver.:  " + Arrays.toString(data));
            }
        }
    }

    public static void writeString(ByteBuf out, String s) {
        ByteBufUtils.writeUTF8String(out, s);
    }

    public static String readString(ByteBuf in) {
        return ByteBufUtils.readUTF8String(in);
    }

    public static void writeStack(ByteBuf out, ItemStack stack) {
        ByteBufUtils.writeItemStack(out, stack);
    }

    public static ItemStack readStack(ByteBuf in) {
        return ByteBufUtils.readItemStack(in);
    }

    public static void writeNBT(ByteBuf out, NBTTagCompound tag) {
        ByteBufUtils.writeTag(out, tag);
    }

    public static NBTTagCompound readNBT(ByteBuf in) {
        return ByteBufUtils.readTag(in);
    }

    public static void log(String log) {
        logger.info(log);
    }

    public static EntityPlayer getPlayer(MessageContext ctx) {
        return QuantumElectriX.proxy.getPlayer(ctx);
    }

    public static void handlePacket(Runnable runnable, EntityPlayer player) {
        QuantumElectriX.proxy.handlePacket(runnable, player);
    }

    public void initialize() {
        wrapper.registerMessage(PacketTransmitterUpdate.class, PacketTransmitterUpdate.TransmitterUpdateMessage.class, 1, Side.CLIENT);
    }

    public void sendTo(IMessage message, EntityPlayerMP player) {
        wrapper.sendTo(message, player);
    }

    public void sendToAll(IMessage message) {
        wrapper.sendToAll(message);
    }

    public void sendToAllAround(IMessage message, NetworkRegistry.TargetPoint point) {
        wrapper.sendToAllAround(message, point);
    }

    public void sendToDimension(IMessage message, int dimensionId) {
        wrapper.sendToDimension(message, dimensionId);
    }

    public void sendToServer(IMessage message) {
        wrapper.sendToServer(message);
    }

    public void sendToCuboid(IMessage message, AxisAlignedBB cuboid, int dim) {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null && cuboid != null) {
            for (EntityPlayerMP player : server.getPlayerList().getPlayers()) {
                if (player.dimension == dim && cuboid.contains(new Vec3d(player.posX, player.posY, player.posZ))) {
                    sendTo(message, player);
                }
            }
        }
    }

    public <TILE extends TileEntity & INetworkTile> void sendUpdatePacket(TILE tile) {
        sendToAllTracking(new PacketTileEntity.TileEntityMessage(tile), tile);
    }

    public void sendToAllTracking(IMessage message, TileEntity tile) {
        BlockPos pos = tile.getPos();
        sendToAllTracking(message, tile.getWorld().provider.getDimension(), pos.getX(), pos.getY(), pos.getZ());
    }

    public void sendToAllTracking(IMessage message, int dimension, double x, double y, double z) {
        sendToAllTracking(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, 1));
    }

    public void sendToAllTracking(IMessage message, NetworkRegistry.TargetPoint point) {
        wrapper.sendToAllTracking(message, point);
    }
}
