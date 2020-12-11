package timicasto.quantumelectrix.block.multiblocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import timicasto.quantumelectrix.api.OtherInterfaces;
import timicasto.quantumelectrix.block.BlockMultiblock;
import timicasto.quantumelectrix.tile.multiblock.TileEntityMultiBlockPart;

import java.util.ArrayList;
import java.util.List;

public class BlockMultiBlocks<E extends Enum<E> & BlockMultiblock.IBlockEnum> extends BlockMultiblock<E> {
    public BlockMultiBlocks(String name, Material material, PropertyEnum<E> property, Class<? extends ItemBlock> itemBlock, Object... additional) {
        super(name, material, property, itemBlock, combineProperties(additional, OtherInterfaces.FACING_HORIZONTAL, OtherInterfaces.MULTI_BLOCK_SLAVE));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        state = super.getActualState(state, worldIn, pos);
        return state;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityMultiBlockPart && worldIn.getGameRules().getBoolean("doTileDrops")) {
            TileEntityMultiBlockPart tileEntity = (TileEntityMultiBlockPart)tile;
            if (!((TileEntityMultiBlockPart<?>) tile).formed && ((TileEntityMultiBlockPart<?>) tile).pos == -1 && !((TileEntityMultiBlockPart<?>) tile).getOriginalBlock().isEmpty()) {
                worldIn.spawnEntity(new EntityItem(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, ((TileEntityMultiBlockPart<?>) tile).getOriginalBlock().copy()));
            }
        }
        if (tile instanceof TileEntityMultiBlockPart) {
            ((TileEntityMultiBlockPart)tile).disassemble();
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return new ArrayList<ItemStack>();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = getOriginalBlock(world, pos);
        if (!stack.isEmpty()) {
            return stack;
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    public ItemStack getOriginalBlock(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMultiBlockPart) {
            return ((TileEntityMultiBlockPart)tile).getOriginalBlock();
        }
        return ItemStack.EMPTY;
    }
}
