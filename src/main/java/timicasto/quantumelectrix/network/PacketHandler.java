package timicasto.quantumelectrix.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import timicasto.quantumelectrix.QuantumElectriX;

import java.util.ArrayList;
import java.util.Arrays;

public class PacketHandler {
    public SimpleNetworkWrapper wrapper = NetworkRegistry.INSTANCE.newSimpleChannel("QEX");

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

    public static EntityPlayer getPlayer(MessageContext ctx) {
        return QuantumElectriX.proxy.getPlayer(ctx);
    }

    public static void handlePacket(Runnable runnable, EntityPlayer player) {
        QuantumElectriX.proxy.handlePacket(runnable, player);
    }

    public void initialize() {
        wrapper.registerMessage(PacketTransmitterUpdate.class, TransmitterUpdateMessage.class, 1, Side.CLIENT);
    }
}
