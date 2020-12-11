package timicasto.quantumelectrix.tile;

import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import timicasto.quantumelectrix.GuiHandler;
import timicasto.quantumelectrix.api.*;
import timicasto.quantumelectrix.block.multiblocks.MultiBlockContactChamber;
import timicasto.quantumelectrix.tile.multiblock.TileEntityMultiblock;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TileEntityContactChamber extends TileEntityMultiblock<TileEntityContactChamber, ContactChamberRecipe> implements OtherInterfaces.ITileWithGui, OtherInterfaces.IAdvancedSelectionBounds, OtherInterfaces.IAdvancedCollisionBounds {
    public TileEntityContactChamber() {
        super(MultiBlockContactChamber.instance, new int[]{5, 9, 5}, 0, true);
    }
    public NonNullList<ItemStack> inventory = NonNullList.withSize(16, ItemStack.EMPTY);
    public FluidTank tank = new FluidTank(8000);

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public List<AxisAlignedBB> getAdvancedSelectionBound() {
        EnumFacing fl = facing;
        EnumFacing fw = facing.rotateY();
        if(mirrored) {
            fw = fw.getOpposite();
        }
        if ((double)pos / 25 < 1.00D) {
            List<AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            return list;
        } else if ((double)pos / 25 >= 2 && (double)pos / 25 < 3) {
            List<AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            return list;
        } else if ((double)pos / 25 >= 5 && (double)pos / 25 < 6) {
            List<AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            return list;
        } else if (pos % 5 == 0 || (pos + 1) % 5 == 0) {
            List<AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            return list;
        } else if ((pos % 25 >= 1 && pos % 25 <= 3) || (pos % 25 >= 21 && pos % 25 <= 23)) {
            List<AxisAlignedBB> list = Lists.newArrayList(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            list.add(new AxisAlignedBB(0, 0, 0, 1, 1, 1).offset(getPos().getX(), getPos().getY(), getPos().getZ()));
            return list;
        }
        return null;
    }

    @Override
    public float[] getBlockBounds() {
        return new float[]{0, 0, 0, 1, 1, 1};
    }

    @Override
    public void renderUpdate(int slot) {

    }

    @Override
    public boolean isOverrided(AxisAlignedBB box, EntityPlayer player, RayTraceResult mop, ArrayList<AxisAlignedBB> arrayList) {
        return false;
    }

    @Override
    public List<AxisAlignedBB> getCollisionBound() {
        return getAdvancedSelectionBound();
    }

    @Override
    protected int[] getEnergyPos() {
        return new int[]{};
    }

    @Override
    public int[] getRedstonePos() {
        return new int[]{222};
    }

    @Override
    public boolean isInWorldProcessingMachine() {
        return false;
    }

    @Override
    public boolean shouldRenderAsActive() {
        return super.shouldRenderAsActive();
    }

    @Override
    public boolean additionalCanProcessCheck(MultiblockProcess<ContactChamberRecipe> process) {
        if (process.recipe != null && !process.recipe.slag.isEmpty()) {
            if (this.inventory.get(15).isEmpty()) {
                return true;
            }
            if (!ItemHandlerHelper.canItemStacksStack(this.inventory.get(15), process.recipe.slag) || inventory.get(15).getCount() + process.recipe.slag.getCount() > getSlotLimit(15)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void doProcessOutput(ItemStack output) {
        BlockPos pos = getPos().add(0, -1, 0).offset(facing, -2);
        TileEntity invTile = this.world.getTileEntity(pos);
        if (invTile != null && invTile.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
            output = ItemHandlerHelper.insertItem(invTile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), output, false);
        }
        if (!output.isEmpty()) {
            Utils.dropStack(world, pos, output, facing);
        }
    }

    @Override
    public void doProcessFluidOutput(FluidStack output) {

    }

    @Override
    public void onProcessFinish(MultiblockProcess<ContactChamberRecipe> process) {
        if (!process.recipe.slag.isEmpty()) {
            if (this.inventory.get(15).isEmpty()) {
                this.inventory.set(15, process.recipe.slag.copy());
            } else if (ItemHandlerHelper.canItemStacksStack(this.inventory.get(15), process.recipe.slag) || inventory.get(15).getCount() + process.recipe.slag.getCount() > getSlotLimit(15)) {
                this.inventory.get(15).grow(process.recipe.slag.getCount());
            }
        }
    }

    @Override
    public int getMaxProcessTime() {
        return 12;
    }

    @Override
    public int getProcessQueueMaxLength() {
        return 12;
    }

    @Override
    public float getMinProcessDistance(MultiblockProcess<ContactChamberRecipe> process) {
        return 0;
    }

    @Override
    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    @Override
    public boolean isStackValid(int slot, ItemStack stack) {
        if (slot <= 12) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        if (slot <= 12) {
            return 1;
        } else {
            return 0;
        }
    }

    static int[] outputSlots = {12, 13, 14, 15};

    @Override
    public int[] getOutputSlot() {
        return outputSlots;
    }



    @Override
    public int[] getOutputTanks() {
        return new int[]{0};
    }

    @Override
    public IFluidTank[] getTanks() {
        return new IFluidTank[]{tank};
    }

    @Override
    protected IFluidTank[] getAccessibleTanks(EnumFacing facing) {
        return new IFluidTank[]{tank};
    }

    @Override
    protected boolean canFill(int tank, EnumFacing facing, FluidStack stack) {
        return true;
    }

    @Override
    protected boolean canDrain(int tank, EnumFacing facing) {
        return true;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if ((pos == 222 || facing == null) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    IItemHandler inputHandler = new InventoryHandler(12, this, 0, true, false) {
        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return stack;
            }
            stack = stack.copy();
            List<Integer> slots = new ArrayList<>(12);
            for(int i = 0 ; i < 12 ; i++) {
                ItemStack inSlot = inventory.get(i);
                if (inSlot.isEmpty()) {
                    if (!simulate) {
                        inventory.set(i, stack);
                    }
                    return ItemStack.EMPTY;
                } else if (ItemHandlerHelper.canItemStacksStack(stack, inSlot) && inSlot.getCount() < inSlot.getMaxStackSize()) {
                    slots.add(i);
                }
            }
            Collections.sort(slots, Comparator.comparingInt(a -> inventory.get(a).getCount()));
            for (int i : slots) {
                ItemStack inSlot = inventory.get(i);
                int filled = Math.min(inSlot.getMaxStackSize() - inSlot.getCount(), stack.getCount());
                if (!simulate) {
                    inSlot.grow(filled);
                }
                stack.shrink(filled);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
            return stack;
        }
    };
    IItemHandler slagHandler = new InventoryHandler(4, this, 12, false, true);

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            TileEntityContactChamber master = master();
            if (master == null) {
                return null;
            }
            if (pos == 222 && facing == EnumFacing.UP) {
                return (T)master.inputHandler;
            }
            if (pos == 222 && facing == EnumFacing.NORTH) {
                return (T)master.slagHandler;
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public ContactChamberRecipe findRecipeForInternal(ItemStack internal) {
        return null;
    }

    @Override
    protected ContactChamberRecipe readRecipeFromNBT(NBTTagCompound compound) {
        return ContactChamberRecipe.readFromNBT(compound);
    }

    @Override
    protected MultiblockProcess loadProcessFromNBT(NBTTagCompound tag)
    {
        IMultiblockRecipe recipe = readRecipeFromNBT(tag);
        if(recipe!=null && recipe instanceof ContactChamberRecipe)
            return new MultiblockContactChamberProcess((ContactChamberRecipe)recipe, tag.getIntArray("process_inputSlots"));
        return null;
    }

    @Override
    public boolean canInteractWith() {
        return formed && pos != 222;
    }

    @Override
    public int getGui() {
        return GuiHandler.GUI_CONTACT_CHAMBER;
    }

    @Override
    public TileEntity getContainerTile() {
        return master();
    }

    public static class MultiblockContactChamberProcess extends MultiblockProcessInMachine<ContactChamberRecipe> {
        public MultiblockContactChamberProcess(ContactChamberRecipe recipe, int... inputSlots) {
            super(recipe, inputSlots);
        }

        @Override
        protected List<ItemStack> getRecipeItemOutputs(TileEntityMultiblock structure) {
            ItemStack input = structure.getInventory().get(this.inputSlots[0]);
            return recipe.getOutput(input);
        }

        @Override
        protected void writeExtraDataToNBT(NBTTagCompound nbt) {
            super.writeExtraDataToNBT(nbt);
        }
    }
}












