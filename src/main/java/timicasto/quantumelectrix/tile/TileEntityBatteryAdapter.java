package timicasto.quantumelectrix.tile;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.energy.CustomEnergyStorage;

public class TileEntityBatteryAdapter extends TileEntity implements ITickable {
    private Logger logger = LogManager.getLogger();
    private final ItemStackHandler inventory = new ItemStackHandler(7);
    private final CustomEnergyStorage storage = new CustomEnergyStorage(1426800, 30);
    public int energy = storage.getEnergyStored();

    @Override
    public void update() {
        for (int i = 0 ; i <= 6 ; i++) {
            if (!inventory.getStackInSlot(i).isEmpty() && inventory.getStackInSlot(i).hasCapability(CapabilityEnergy.ENERGY, null)) {
                if (inventory.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored() >= 237800 && storage.getMaxEnergyStored() - storage.getEnergyStored() >= inventory.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored()) {
                    energy += 237800;
                    this.storage.setEnergyStored(this.storage.getEnergyStored() + 237800);
                    CustomEnergyStorage storage1 = (CustomEnergyStorage) inventory.getStackInSlot(i).getCapability(CapabilityEnergy.ENERGY, null);
                    storage1.setEnergyStored(0);
                }
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    public boolean canInsertBattery(Item item) {
        return item.equals(inventory.getStackInSlot(0).getItem()) || item.equals(inventory.getStackInSlot(1).getItem()) || item.equals(inventory.getStackInSlot(2).getItem()) || item.equals(inventory.getStackInSlot(3).getItem()) || item.equals(inventory.getStackInSlot(4).getItem()) || item.equals(inventory.getStackInSlot(5).getItem()) || allNull();
    }

    private boolean allNull() {
        return inventory.getStackInSlot(0).isEmpty() && inventory.getStackInSlot(1).isEmpty() && inventory.getStackInSlot(2).isEmpty() && inventory.getStackInSlot(3).isEmpty() && inventory.getStackInSlot(4).isEmpty() && inventory.getStackInSlot(5).isEmpty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return (T) this.storage;
        }
        return super.getCapability(capability, facing);
    }

    public int getEnergyStored()
    {
        return this.storage.getEnergyStored();
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.storage.readFromNBT(compound);
        Character
        this.energy = compound.getInteger("Energy");
        this.inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("Inventory", this.inventory.serializeNBT());
        compound.setInteger("Energy", this.energy);
        this.storage.writeToNBT(compound);
        return compound;
    }
}
