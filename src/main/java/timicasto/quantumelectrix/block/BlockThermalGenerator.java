package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import timicasto.quantumelectrix.GuiHandler;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.creative.TabLoader;
import timicasto.quantumelectrix.tile.TileEntityThermalGenerator;

public class BlockThermalGenerator extends Block {
    public BlockThermalGenerator() {
        super(Material.IRON);
        setRegistryName("thermal_generator");
        setUnlocalizedName("thermal_generator");
        setCreativeTab(TabLoader.elecTab);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(QuantumElectriX.instance, GuiHandler.GUI_THERMAL_GENERATOR, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityThermalGenerator();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityThermalGenerator tile = (TileEntityThermalGenerator) worldIn.getTileEntity(pos);
        worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.handler.getStackInSlot(0)));
        worldIn.spawnEntity(new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), tile.handler.getStackInSlot(1)));
        super.breakBlock(worldIn, pos, state);
    }
}


















