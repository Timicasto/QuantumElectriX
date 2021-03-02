package timicasto.quantumelectrix.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.Pair;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.api.ints.IActiveState;
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

    public static boolean isPowered(World world, Coord4D coord) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            Coord4D faceCoord = coord.offset(facing);
            if (faceCoord.exists(world) && faceCoord.offset(facing).exists(world)) {
                IBlockState state = faceCoord.getBlockState(world);
                boolean weakRS = state.getBlock().shouldCheckWeakPower(state, world, coord.getPos(), facing);
                if (weakRS && isDirectlyPowered(world, faceCoord)) {
                    return true;
                } else if (!weakRS && state.getWeakPower(world, faceCoord.getPos(), facing) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDirectlyPowered(World world, Coord4D coord) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            Coord4D faceCoord = coord.offset(facing);
            if (faceCoord.exists(world)) {
                if (world.getRedstonePower(coord.getPos(), facing) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void updateBlock(World world, BlockPos pos) {
        if (!world.isBlockLoaded(pos)) {
            return;
        }
        world.markBlockRangeForRenderUpdate(pos, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (!(tile instanceof IActiveState) || ((IActiveState)tile).lightUpdate()) {
            updateAllLightTypes(world, pos);
        }
    }

    public static void updateAllLightTypes(World world, BlockPos pos) {
        world.checkLightFor(EnumSkyBlock.BLOCK, pos);
        world.checkLightFor(EnumSkyBlock.SKY, pos);
    }

    public static Pair<Vec3d, Vec3d> getRayTraceVectors(EntityPlayer player) {
        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        float f1 = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f3 = -MathHelper.cos(-pitch * 0.017453292F);
        float f4 = MathHelper.sin(-pitch * 0.017453292F);
        float f5 = f2 * f3;
        float f6 = f1 * f3;
        double d3 = 5D;
        if (player instanceof EntityPlayerMP) {
            d3 = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
        }
        Vec3d end = start.add(new Vec3d(f5 * d3, f4 * d3, f6 * d3));
        return Pair.of(start, end);
    }

    public static class AdvancedRayTraceResultBase<T extends RayTraceResult> {
        public final AxisAlignedBB bounds;
        public final T hit;
        public AdvancedRayTraceResultBase(T mopper, AxisAlignedBB boundBox) {
            hit = mopper;
            bounds = boundBox;
        }
        public boolean valid() {
            return hit != null && bounds != null;
        }
        public double squareDistanceFrom(Vec3d vector) {
            return hit.hitVec.squareDistanceTo(vector);
        }
    }

    public static class AdvancedRayTraceResult extends AdvancedRayTraceResultBase<RayTraceResult> {
        public AdvancedRayTraceResult(RayTraceResult mopper, AxisAlignedBB boundBox) {
            super(mopper, boundBox);
        }
    }

    public static AdvancedRayTraceResult collisionTrace(BlockPos pos, Vec3d start, Vec3d end, Collection<AxisAlignedBB> boxes) {
        double minimumDistance = Double.POSITIVE_INFINITY;
        AdvancedRayTraceResult hit = null;
        int i = -1;
        for (AxisAlignedBB bb : boxes) {
            AdvancedRayTraceResult result = bb == null ? null : collisionTrace(pos, start, end, bb, i, null);
            if (result != null) {
                double d1 = result.squareDistanceFrom(start);
                if (d1 < minimumDistance) {
                    minimumDistance = d1;
                    hit = result;
                }
            }
            i++;
        }
        return hit;
    }

    public static AdvancedRayTraceResult collisionTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB bounds, int hit, Object hitInf) {
        RayTraceResult result = bounds.offset(pos).calculateIntercept(start, end);
        if (result == null) {
            return null;
        }
        result = new RayTraceResult(RayTraceResult.Type.BLOCK, result.hitVec, result.sideHit, pos);
        result.subHit = hit;
        result.hitInfo = hitInf;
        return new AdvancedRayTraceResult(result, bounds);
    }

    public static void notifyNeighborChange(World world, Coord4D coord, BlockPos pos) {
        IBlockState state = coord.getBlockState(world);
        state.getBlock().onNeighborChange(world, coord.getPos(), pos);
        state.neighborChanged(world, coord.getPos(), world.getBlockState(pos).getBlock(), pos);
    }

    public static void notifyTileNeighborsChanged(World world, Coord4D vector) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            Coord4D offsetVector = vector.offset(facing);
            if (offsetVector.exists(world)) {
                notifyNeighborChange(world, offsetVector, vector.getPos());
                if (offsetVector.getBlockState(world).isNormalCube()) {
                    offsetVector = offsetVector.offset(facing);
                    if (offsetVector.exists(world)) {
                        Block block = offsetVector.getBlock(world);
                        if (block.getWeakChanges(world, offsetVector.getPos())) {
                            block.onNeighborChange(world, offsetVector.getPos(), vector.getPos());
                        }
                    }
                }
            }
        }
    }
}
