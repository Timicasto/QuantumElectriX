package timicasto.quantumelectrix.tile.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import timicasto.quantumbase.network.PacketMoisture;
import timicasto.quantumelectrix.api.IDirectionalTile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class TileEntityMultiBlockPart<T extends TileEntityMultiBlockPart<T>> extends TileEntity implements ITickable, IDirectionalTile, IDirectionalTile.IBlockBounds {
    public boolean formed = false;
    public int pos = -1;
    public int[] offset = {0, 0, 0};
    public boolean mirrored = false;
    public EnumFacing facing = EnumFacing.NORTH;
    public long localDisassembly = -1;
    protected final int[] structureDimension;

    private ItemStackHandler inventory = new ItemStackHandler(16);

    protected TileEntityMultiBlockPart(int[] structureDimension) {
        this.structureDimension = structureDimension;
    }

    public EnumFacing getFacing() {
        return this.facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    @Override
    public int getFacingLimitation() {
        return 2;
    }

    @Override
    public boolean mirrorFacingOnPlacement(EntityLivingBase placer) {
        return false;
    }

    @Override
    public boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity) {
        return false;
    }

    @Override
    public boolean canRotate(EnumFacing axis) {
        return false;
    }

    @Nonnull
    protected abstract IFluidTank[] getAccessibleTanks(EnumFacing facing);
    protected abstract boolean canFill(int tank, EnumFacing facing, FluidStack stack);
    protected abstract boolean canDrain(int tank, EnumFacing facing);

    public static class FluidWrapper implements IFluidHandler {
        final TileEntityMultiBlockPart structure;
        final EnumFacing facing;

        public FluidWrapper(TileEntityMultiBlockPart structure, EnumFacing facing) {
            this.structure = structure;
            this.facing = facing;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            if (!this.structure.formed) {
                return new IFluidTankProperties[0];
            }
            IFluidTank[] tanks = this.structure.getAccessibleTanks(facing);
            IFluidTankProperties[] properties = new IFluidTankProperties[tanks.length];
            for (int i = 0 ; i < tanks.length ; i++) {
                properties[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity());
            }
            return properties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            if (!this.structure.formed || resource == null) {
                return 0;
            }
            IFluidTank[] tanks = this.structure.getAccessibleTanks(facing);
            int fill = -1;
            for (int i = 0 ; i < tanks.length ; i++) {
                IFluidTank tank = tanks[i];
                if (tank != null && this.structure.canFill(i, facing, resource) && tank.getFluid() != null && tank.getFluid().isFluidEqual(resource)) {
                    fill = tank.fill(resource, doFill);
                    if (fill > 0) {
                        break;
                    }
                }
            }
            if (fill == -1) {
                for (int i = 0 ; i < tanks.length ; i++) {
                    IFluidTank tank = tanks[i];
                    if (tank != null && this.structure.canFill(i, facing, resource) && tank.getFluid() != null && tank.getFluid().isFluidEqual(resource)) {
                        fill = tank.fill(resource, doFill);
                        if (fill > 0) {
                            break;
                        }
                    }
                }
            }
            if (fill > 0) {
                this.structure.updateMaster(null, true);
            }
            return fill < 0 ? 0 : fill;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if (!this.structure.formed || resource == null) {
                return null;
            }
            IFluidTank[] tanks = this.structure.getAccessibleTanks(facing);
            FluidStack drain = null;
            for (int i = 0 ; i < tanks.length ; i++) {
                IFluidTank tank = tanks[i];
                if (tank != null && this.structure.canDrain(i, facing)) {
                    if (tank instanceof IFluidHandler) {
                        drain = ((IFluidHandler)tank).drain(resource, doDrain);
                    } else {
                        drain = tank.drain(resource.amount, doDrain);
                    }
                    if (drain != null) {
                        break;
                    }
                }
            }
            if (drain != null) {
                this.structure.updateMaster(null, true);
            }
            return drain;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            if (!this.structure.formed || maxDrain == 0) {
                return null;
            }
            IFluidTank[] tanks = this.structure.getAccessibleTanks(facing);
            FluidStack drain = null;
            for (int i = 0 ; i < tanks.length ; i++) {
                IFluidTank tank = tanks[i];
                if (tank != null && this.structure.canDrain(i, facing)) {
                        drain = ((IFluidHandler)tank).drain(maxDrain, doDrain);
                    if (drain != null) {
                        break;
                    }
                }
            }
            if (drain != null) {
                this.structure.updateMaster(null, true);
            }
            return drain;
        }
    }
    

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        formed = compound.getBoolean("formed");
        pos = compound.getInteger("pos");
        offset = compound.getIntArray("offset");
        mirrored = compound.getBoolean("mirrored");
        facing = EnumFacing.getFront(compound.getInteger("facing"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setBoolean("formed", formed);
        compound.setInteger("pos", pos);
        compound.setIntArray("offset", offset);
        compound.setBoolean("mirrored", mirrored);
        compound.setInteger("facing", facing.ordinal());
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;
        return super.hasCapability(capability, facing);
    }
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T)this.inventory;
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidate() {
        super.invalidate();
    }

    public static boolean immovable() {
        return true;
    }

    public void markContainingBlockForUpdate(@Nullable IBlockState newState) {
        markBlockForUpdate(getPos(), newState);
    }
    public void markBlockForUpdate(BlockPos pos, @Nullable IBlockState newState) {
        IBlockState state = world.getBlockState(getPos());
        if(newState==null) {
            newState = state;
        }
        world.notifyBlockUpdate(pos,state,newState,3);
        world.notifyNeighborsOfStateChange(pos, newState.getBlock(), true);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public T master() {
        if(offset[0]==0&&offset[1]==0&&offset[2]==0)
            return (T)this;
        BlockPos masterPos = getPos().add(-offset[0],-offset[1],-offset[2]);
        TileEntity te = world.getTileEntity(masterPos);
        return this.getClass().isInstance(te)?(T)te: null;
    }

    public void updateMaster(IBlockState state, boolean update) {
        T master = master();
        if(master!=null) {
            master.markDirty();
            if(update)
                master.markContainingBlockForUpdate(state);
        }
    }

    public boolean isDummy() {
        return offset[0]!=0 || offset[1]!=0 || offset[2]!=0;
    }
    public abstract ItemStack getOriginalBlock();
    public void disassemble() {
        if(formed && !world.isRemote) {
            BlockPos startPos = getOrigin();
            BlockPos masterPos = getPos().add(-offset[0], -offset[1], -offset[2]);
            long time = world.getTotalWorldTime();
            for(int yy=0;yy<structureDimension[0] ; yy++)
                for(int ll=0;ll<structureDimension[1] ; ll++)
                    for(int ww=0;ww<structureDimension[2] ; ww++) {
                        int w = mirrored?-ww:ww;
                        BlockPos pos = startPos.offset(facing, ll).offset(facing.rotateY(), w).add(0, yy, 0);
                        ItemStack s = ItemStack.EMPTY;

                        TileEntity te = world.getTileEntity(pos);
                        if(te instanceof TileEntityMultiBlockPart) {
                            TileEntityMultiBlockPart part = (TileEntityMultiBlockPart) te;
                            Vec3i diff = pos.subtract(masterPos);
                            if (part.offset[0]!=diff.getX()||part.offset[1]!=diff.getY()||part.offset[2]!=diff.getZ())
                                continue;
                            else if (time!=part.localDisassembly) {
                                s = part.getOriginalBlock();
                                part.formed = false;
                            }
                        }
                        if(pos.equals(getPos()))
                            s = this.getOriginalBlock();
                        IBlockState state = Block.getBlockFromItem(s.getItem()).getStateFromMeta(s.getItemDamage());
                        if(state!=null) {
                            if(pos.equals(getPos()))
                                world.spawnEntity(new EntityItem(world, pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5, s));
                            else
                                replaceStructureBlock(pos, state, s, yy,ll,ww);
                        }
                    }
        }
    }
    public BlockPos getOrigin() {
        return getBlockPosForPos(0);
    }
    public BlockPos getBlockPosForPos(int targetPos) {
        int blocksPerLevel = structureDimension[1]*structureDimension[2];
        // dist = target position - current position
        int distH = (targetPos/blocksPerLevel)-(pos/blocksPerLevel);
        int distL = (targetPos%blocksPerLevel / structureDimension[2])-(pos%blocksPerLevel / structureDimension[2]);
        int distW = (targetPos%structureDimension[2])-(pos%structureDimension[2]);
        int w = mirrored?-distW:distW;
        return getPos().offset(facing, distL).offset(facing.rotateY(), w).add(0, distH, 0);
    }
    public void replaceStructureBlock(BlockPos pos, IBlockState state, ItemStack stack, int h, int l, int w) {
        if(state.getBlock()==this.getBlockType())
            world.setBlockToAir(pos);
        world.setBlockState(pos, state);
        TileEntity tile = world.getTileEntity(pos);
        if(tile instanceof ITileDrop) {
            ((ITileDrop) tile).readOnPlacement(null, stack);
        }
    }
}
