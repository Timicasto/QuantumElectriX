package timicasto.quantumelectrix.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.api.ints.INetworkTile;
import timicasto.quantumelectrix.energy.CustomEnergyStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Utils {
    public static void dropStack(World world, BlockPos pos, ItemStack stack, EnumFacing facing) {
        if (!stack.isEmpty()) {
            EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
            entity.motionY = 0.025000000372529D;
            if (facing != null) {
                entity.motionX = (0.075 * facing.getFrontOffsetX());
                entity.motionZ = (0.075 * facing.getFrontOffsetZ());
            }
            world.spawnEntity(entity);
        }
    }

    public static NonNullList<ItemStack> createListFromItemStack(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.create();
        if (stack != null) {
            list.add(0, stack);
        }
        return list;
    }

    public static boolean isActiveTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItem() == RegistryHandler.TOOL_BOX;
    }

    public static void encodeData_ObjectCollection(ByteBuf buffer, Collection<Object> sendData) throws IOException {
        for (Object dataValue : sendData) {
            if (dataValue instanceof Integer) {
                buffer.writeInt((Integer) dataValue);
            } else if (dataValue instanceof Float) {
                buffer.writeFloat((Float) dataValue);
            } else if (dataValue instanceof Double) {
                buffer.writeDouble((Double) dataValue);
            } else if (dataValue instanceof Byte) {
                buffer.writeByte((Byte) dataValue);
            } else if (dataValue instanceof Boolean) {
                buffer.writeBoolean((Boolean) dataValue);
            } else if (dataValue instanceof String) {
                ByteBufUtils.writeUTF8String(buffer, (String) dataValue);
            } else if (dataValue instanceof Short) {
                buffer.writeShort((Short) dataValue);
            } else if (dataValue instanceof Long) {
                buffer.writeLong((Long) dataValue);
            } else if (dataValue instanceof CustomEnergyStorage) {
                CustomEnergyStorage storage = (CustomEnergyStorage) dataValue;
                buffer.writeFloat(storage.getMaxEnergyStored());
                buffer.writeFloat(storage.getMaxReceive());
                buffer.writeFloat(storage.getMaxExtract());
                buffer.writeFloat(storage.getEnergyStored());
            }
        }
    }

    public static ArrayList<Object> decodeData(Class<?>[] types, ByteBuf buf) {
        ArrayList<Object> list = new ArrayList<Object>();

        for (Class clazz : types) {
            if (clazz.equals(Integer.class)) {
                list.add(buf.readInt());
            } else if (clazz.equals(Float.class)) {
                list.add(buf.readFloat());
            } else if (clazz.equals(Double.class)) {
                list.add(buf.readDouble());
            } else if (clazz.equals(Byte.class)) {
                list.add(buf.readByte());
            } else if (clazz.equals(Boolean.class)) {
                list.add(buf.readBoolean());
            } else if (clazz.equals(String.class)) {
                list.add(ByteBufUtils.readUTF8String(buf));
            } else if (clazz.equals(Short.class)) {
                list.add(buf.readShort());
            } else if (clazz.equals(Long.class)) {
                list.add(buf.readLong());
            } else if (clazz.equals(byte[].class)) {
                int size = buf.readInt();
                byte[] bytes = new byte[size];
                buf.readBytes(bytes, 0, size);
                list.add(bytes);
            } else if (clazz.equals(CustomEnergyStorage.class)) {
                CustomEnergyStorage storage = new CustomEnergyStorage(buf.readInt(), buf.readInt(), buf.readInt());
                storage.setEnergyStored(buf.readInt());
                list.add(storage);
            }
        }
        return list;
    }

    public static Side getSide() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER || Thread.currentThread().getName().startsWith("Netty Epoll Server IO")) {
            return Side.SERVER;
        }
        return Side.CLIENT;
    }
}
