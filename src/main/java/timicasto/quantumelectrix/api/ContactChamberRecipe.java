package timicasto.quantumelectrix.api;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ContactChamberRecipe extends MachineRecipes {

    public static float timeModifier = 1;
    public final ItemStack input;
    public final FluidStack output;
    public final ItemStack slag;
    public static ArrayList<ContactChamberRecipe> recipes = new ArrayList<ContactChamberRecipe>();

    /**
     * Recipe对象结构
     *
     * @param input 配方的输入物品
     * @param output 配方的输出流体
     * @param slag 剩下的渣子
     * @param processTime 完成一次处理所需要的时间
     */
    public ContactChamberRecipe(ItemStack input, FluidStack output, ItemStack slag, int processTime) {
        this.output = output;
        this.input = input;
        this.slag = slag;
        this.processTimeTotal = (int)Math.floor(processTime * timeModifier);
        this.energyTotal = 0;
        this.inputs = Lists.newArrayList(this.input);
        this.fluidOutputs = Lists.newArrayList(this.output);
        this.outputs = Utils.createListFromItemStack(this.slag);
    }
    
    
    
    @Override
    public List<ItemStack> getItemInputs() {
        return null;
    }

    @Override
    public List<FluidStack> getFluidInputs() {
        return null;
    }

    @Override
    public NonNullList<ItemStack> getItemOutputs() {
        return null;
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return null;
    }

    @Override
    public int getTotalProcessTime() {
        return 0;
    }

    @Override
    public int getTotalProcessEnergy() {
        return 0;
    }

    @Override
    public int getMultipleProcessTicks() {
        return 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("input", input.writeToNBT(new NBTTagCompound()));
        compound.setTag("output", output.tag);
        compound.setTag("slag", slag.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    public static ContactChamberRecipe readFromNBT(NBTTagCompound compound) {
        ItemStack in = new ItemStack(compound.getCompoundTag("in"));

        for (ContactChamberRecipe recipe : recipes) {
            if (recipe.input.equals(in)) {
                return recipe;
            }
        }
        return null;
    }

    public NonNullList<ItemStack> getOutput(ItemStack input) {
        NonNullList<ItemStack> outputs = NonNullList.create();
        outputs.add(slag);
        return outputs;
    }
    
    public boolean matches(ItemStack input) {
        if (this.input != null && this.input.getItem() == input.getItem()) {
            return true;
        }
        return false;
    }

    public boolean isInputValid(ItemStack stack) {
        return this.input != null && this.input.getItem() == stack.getItem();
    }

    public static ContactChamberRecipe createRecipe(ItemStack input, FluidStack output, @Nonnull ItemStack slag, int time) {
        ContactChamberRecipe recipe = new ContactChamberRecipe(input, output, slag, time);
        if (recipe.input != null) {
            recipes.add(recipe);
        }
        return recipe;
    }

    public static ContactChamberRecipe search(ItemStack input) {
        for (ContactChamberRecipe recipe : recipes) {
            if (recipe != null && recipe.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    public static boolean isRecipeInputValid(ItemStack stack) {
        for (ContactChamberRecipe recipe : recipes) {
            if (recipe != null && recipe.isInputValid(stack)) {
                return true;
            }
        }
        return false;
    }
    
    
}
