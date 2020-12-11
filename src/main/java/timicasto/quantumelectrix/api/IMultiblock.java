package timicasto.quantumelectrix.api;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IMultiblock {
    String getUniqueName();
    boolean isBlockTrigger(IBlockState state);
    boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player);
    ItemStack[][][] getStructureManual();

    default IBlockState getBlockstateFromStack(int index, ItemStack stack)
    {
        if(!stack.isEmpty() && stack.getItem() instanceof ItemBlock)
            return ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
        return null;
    }
    @SideOnly(Side.CLIENT)
    boolean overwriteBlockRender(ItemStack stack, int iterator);
    float getManualScale();
    @SideOnly(Side.CLIENT)
    boolean canRenderFormedStructure();
    @SideOnly(Side.CLIENT)
    void renderFormedStructure();
}
