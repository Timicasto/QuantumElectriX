package timicasto.quantumelectrix.network.wrapper;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Vector3B implements Cloneable {
    public double x;
    public double y;
    public double z;

    public Vector3B(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3B() {
        this(0, 0, 0);
    }

    public Vector3B(Vector3B vector) {
        this(vector.x, vector.y, vector.z);
    }

    public Vector3B(double amount) {
        this(amount, amount, amount);
    }

    public Vector3B(Entity posTar) {
        this(posTar.posX, posTar.posY, posTar.posZ);
    }

    public Vector3B(BlockPos pos)
    {
        this(pos.getX(), pos.getY(), pos.getZ());
    }

    public Vector3B(TileEntity par1)
    {
        this(par1.getPos());
    }

    public Vector3B(Vec3d par1)
    {
        this(par1.x, par1.y, par1.z);

    }

    public Vector3B(RayTraceResult par1)
    {
        this(par1.getBlockPos());
    }

    public Vector3B(EnumFacing direction)
    {
        this(direction.getFrontOffsetX(), direction.getFrontOffsetY(), direction.getFrontOffsetZ());
    }

    public Vector3B(NBTTagCompound nbt)
    {
        this(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
    }

    public int intX()
    {
        return (int) Math.floor(this.x);
    }

    public int intY()
    {
        return (int) Math.floor(this.y);
    }

    public int intZ()
    {
        return (int) Math.floor(this.z);
    }

    public float floatX()
    {
        return (float) this.x;
    }

    public float floatY()
    {
        return (float) this.y;
    }

    public float floatZ()
    {
        return (float) this.z;
    }

    @Override
    public final Vector3B clone()
    {
        return new Vector3B(this);
    }

    /**
     * Easy block access functions.
     *
     * @param world
     * @return
     */
    public Block getBlock(IBlockAccess world)
    {
        return world.getBlockState(new BlockPos(this.intX(), this.intY(), this.intZ())).getBlock();
    }

    public IBlockState getBlockMetadata(IBlockAccess world)
    {
        return world.getBlockState(new BlockPos(this.intX(), this.intY(), this.intZ()));
    }

    public TileEntity getTileEntity(IBlockAccess world)
    {
        return world.getTileEntity(new BlockPos(this.intX(), this.intY(), this.intZ()));
    }

    public boolean setBlock(World world, IBlockState state, int notify)
    {
        return world.setBlockState(new BlockPos(this.intX(), this.intY(), this.intZ()), state, notify);
    }

    public boolean setBlock(World world, IBlockState state)
    {
        return this.setBlock(world, state, 3);
    }

}
