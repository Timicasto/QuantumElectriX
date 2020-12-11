package timicasto.quantumelectrix.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import timicasto.quantumelectrix.energy.CustomEnergyStorage;

import java.util.Random;

public class TileEntityThermalGenerator extends TileEntity implements ITickable {
    public ItemStackHandler handler = new ItemStackHandler(2);
    private CustomEnergyStorage storage = new CustomEnergyStorage(400);
    public int energy = storage.getEnergyStored();
    public int cookTime;
    private final Random random = new Random();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) this.storage;
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.handler;
        }
        return super.getCapability(capability, facing);
    }


    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public void update() {
        if (!handler.getStackInSlot(0).isEmpty() && isItemFuel(handler.getStackInSlot(0))) {
            ++cookTime;
            ++energy;
            if (cookTime >= getFuelValue(handler.getStackInSlot(0))) {
                cookTime = 0;
                handler.extractItem(0, 1, false);
            }
        } else {
            cookTime = 0;
        }
    }

    private boolean isItemFuel(ItemStack stack) {
        return getFuelValue(stack) > 0;
    }

    public int getFuelValue(ItemStack stack) {
        if (stack.getItem() == Items.BOOK) {
            return 50;
        }
        if (stack.getItem() == Items.COAL) {
            return 1600;
        }
            return 300;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.handler.deserializeNBT(compound.getCompoundTag("Inventory"));
        this.cookTime = compound.getInteger("CookTime");
        this.energy = compound.getInteger("GuiEnergy");
        this.storage.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", this.handler.serializeNBT());
        compound.setInteger("CookTime", this.cookTime);
        compound.setInteger("GuiEnergy", this.energy);
        this.storage.writeToNBT(compound);
        return compound;
    }

    public int getField(int id)
    {
        switch(id)
        {
            case 0:
                return this.energy;
            case 1:
                return this.cookTime;
            default:
                return 0;
        }
    }

    public void setField(int id, int value)
    {
        switch(id)
        {
            case 0:
                this.energy = value;
            case 1:
                this.cookTime = value;
        }
    }

    public boolean isUsableByPlayer(EntityPlayer player)
    {
        return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
    }

    public int getEnergyStored()
    {
        return this.energy;
    }

    public int getMaxEnergyStored()
    {
        return this.storage.getMaxEnergyStored();
    }
}






















