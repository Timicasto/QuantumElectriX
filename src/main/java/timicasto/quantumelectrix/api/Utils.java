package timicasto.quantumelectrix.api;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import timicasto.quantumelectrix.RegistryHandler;

public class Utils {
    public static void dropStack(World world, BlockPos pos, ItemStack stack, EnumFacing facing) {
        if (!stack.isEmpty()) {
            EntityItem entity = new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy());
            entity.motionY = 0.025000000372529D;
            if (facing != null) {
                entity.motionX = (0.075 * facing.getFrontOffsetX());
                entity.motionZ = (0.075 * facing.getFrontOffsetZ());
            }
            world.spawnEntity(entity);
        }
    }

    public static NonNullList<ItemStack> createListFromItemStack(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.create();
        if (stack != null) {
            list.add(0, stack);
        }
        return list;
    }

    public static boolean isActiveTool(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        return stack.getItem() == RegistryHandler.TOOL_BOX;
    }
}
