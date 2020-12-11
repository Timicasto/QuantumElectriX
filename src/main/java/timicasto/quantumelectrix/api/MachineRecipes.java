package timicasto.quantumelectrix.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public abstract class MachineRecipes implements IMultiblockRecipe {
    protected List<ItemStack> inputs;

    @Override
    public List<ItemStack> getItemInputs() {
        return inputs;
    }

    protected NonNullList<ItemStack> outputs;
    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return outputs;
    }

    protected List<FluidStack> fluidInputs;

    @Override
    public List<FluidStack> getFluidInputs() {
        return fluidInputs;
    }

    protected List<FluidStack> fluidOutputs;

    @Override
    public List<FluidStack> getFluidOutputs() {
        return fluidOutputs;
    }

    int processTimeTotal;

    @Override
    public int getTotalProcessTime() {
        return processTimeTotal;
    }

    int energyTotal;

    @Override
    public int getTotalProcessEnergy() {
        return energyTotal;
    }
}
