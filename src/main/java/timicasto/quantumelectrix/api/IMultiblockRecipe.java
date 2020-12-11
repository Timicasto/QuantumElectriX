package timicasto.quantumelectrix.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public interface IMultiblockRecipe {
    List<ItemStack> getItemInputs();
    List<FluidStack> getFluidInputs();
    NonNullList<ItemStack> getItemOutputs();

    default NonNullList<ItemStack> getActualItemOutputs(TileEntity tile)
    {
        return getItemOutputs();
    }
    List<FluidStack> getFluidOutputs();

    default List<FluidStack> getActualFluidOutputs(TileEntity tile)
    {
        return getFluidOutputs();
    }

    int getTotalProcessTime();
    int getTotalProcessEnergy();
    int getMultipleProcessTicks();

    NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound);
}
