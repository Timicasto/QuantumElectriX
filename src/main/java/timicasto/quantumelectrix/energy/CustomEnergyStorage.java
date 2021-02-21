package timicasto.quantumelectrix.energy;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergyStorage extends EnergyStorage {
    int averageInsertion = 0;
    int averageExtraction = 0;
    double averageDecayFactor = 0.5;
    public CustomEnergyStorage(int capacity) {
        super(capacity, capacity, capacity, 0);
    }

    public CustomEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer, maxTransfer, 0);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract, 0);
    }

    public CustomEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (!simulate) {
            averageInsertion = (int)Math.round(averageInsertion * averageDecayFactor + received * (1 - averageDecayFactor));
        }
        return received;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int extracted = super.extractEnergy(maxExtract, simulate);
        if (!simulate) {
            averageExtraction = (int)Math.round(averageExtraction * averageDecayFactor + extracted * (1 - averageDecayFactor));
        }
        return extracted;
    }

    @Override
    public int getEnergyStored() {
        return super.getEnergyStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return super.getMaxEnergyStored();
    }

    @Override
    public boolean canExtract() {
        return super.canExtract();
    }

    @Override
    public boolean canReceive() {
        return super.canReceive();
    }

    public int getAverageInsertion() {
        return averageInsertion;
    }

    public int getAverageExtraction() {
        return averageExtraction;
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    public CustomEnergyStorage setDecayFactor(double value) {
        this.averageDecayFactor = value;
        return this;
    }

    public int getMaxReceive() {
        return this.maxReceive;
    }

    public int getMaxExtract() {
        return this.maxExtract;
    }

    public void readFromNBT(NBTTagCompound compound) {
        this.energy = compound.getInteger("Energy");
        this.capacity = compound.getInteger("Capacity");
        this.maxReceive = compound.getInteger("MaxReceive");
        this.maxExtract = compound.getInteger("MaxExtract");
    }

    public void writeToNBT(NBTTagCompound compound) {
        compound.setInteger("Energy", this.energy);
        compound.setInteger("Capacity", this.capacity);
        compound.setInteger("MaxReceive", this.maxReceive);
        compound.setInteger("MaxExtract", this.maxExtract);
    }

}
