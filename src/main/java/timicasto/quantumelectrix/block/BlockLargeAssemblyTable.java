package timicasto.quantumelectrix.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import timicasto.quantumelectrix.GuiHandler;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.creative.TabLoader;
import timicasto.quantumelectrix.tile.TileEntityLargeAssemblyTable;

public class BlockLargeAssemblyTable extends BlockContainer {
    public BlockLargeAssemblyTable() {
        super(Material.WOOD);
        setRegistryName("large_assembly_table");
        setUnlocalizedName("large_assembly_table");
        setCreativeTab(TabLoader.elecTab);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityLargeAssemblyTable();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        playerIn.openGui(QuantumElectriX.instance, GuiHandler.GUI_LARGE_ASSEMBLY_TABLE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }
}
