package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
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
import timicasto.quantumelectrix.tile.TileEntityBatteryAdapter;

public class BlockBatteryAdapter extends Block {
    public BlockBatteryAdapter() {
        super(Material.IRON);
        setRegistryName("battery_adapter");
        setUnlocalizedName("battery_adapter");
        setCreativeTab(TabLoader.elecTab);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
    
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityBatteryAdapter();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            playerIn.openGui(QuantumElectriX.instance, GuiHandler.GUI_BATTERY_ADAPTER, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }
}
