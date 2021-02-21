package timicasto.quantumelectrix.api;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.world.World;

import java.util.List;

public class CompareElement {
    public static boolean isBlockAt(World world, BlockPos pos, Block target, int meta) {
        return blockstateMatch(world.getBlockState(pos), target, meta);
    }

    public static boolean blockstateMatch(IBlockState state, Block target, int meta) {
        if (state.getBlock().equals(target)) {
            return meta < 0 || meta == OreDictionary.WILDCARD_VALUE || state.getBlock().getMetaFromState(state) == meta;
        }
        return false;
    }

    public static boolean isOreBlockAt(World world, BlockPos pos, String oreName)
    {
        IBlockState state = world.getBlockState(pos);
        ItemStack stack = new ItemStack(state.getBlock(),1,state.getBlock().getMetaFromState(state));
        return compareToOreName(stack, oreName);
    }

    public static boolean compareToOreName(ItemStack stack, String oreName)
    {
        if(!isExistingOreName(oreName))
            return false;
        ItemStack comp = copyStackWithAmount(stack, 1);
        List<ItemStack> s = OreDictionary.getOres(oreName);
        for(ItemStack st:s)
            if(OreDictionary.itemMatches(st, comp, false))
                return true;
        return false;
    }

    public static boolean isExistingOreName(String name)
    {
        if(!OreDictionary.doesOreNameExist(name))
            return false;
        else
            return !OreDictionary.getOres(name).isEmpty();
    }

    public static ItemStack copyStackWithAmount(ItemStack stack, int amount)
    {
        if(stack.isEmpty())
            return ItemStack.EMPTY;
        ItemStack s2 = stack.copy();
        s2.setCount(amount);
        return s2;
    }
}
