package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.GuiHandler;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.creative.TabLoader;
import timicasto.quantumelectrix.fluid.FluidLoader;
import timicasto.quantumelectrix.tile.TileEntityBurningChamber;
import timicasto.quantumelectrix.tile.TileEntityThermalGenerator;

public class BlockBurningChamber extends Block {
    private Logger logger = LogManager.getLogger();
    public BlockBurningChamber() {
        super(Material.IRON);
        setRegistryName("burning_chamber");
        setUnlocalizedName("burning_chamber");
        setCreativeTab(TabLoader.elecTab);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = worldIn.getTileEntity(pos);
        assert tile != null;
        if (((TileEntityBurningChamber)tile).isStructureValid(pos.getX(), pos.getY(), pos.getZ())) {
            ((TileEntityBurningChamber) tile).formBlock();
        }
        logger.info("Click Infomation : " + "TileEntity : " + tile + " Returned " + ((TileEntityBurningChamber)tile).isStructureValid(pos.getX(), pos.getY(), pos.getZ()));
        ItemStack heldItem = playerIn.getHeldItem(hand);
        if (heldItem.getItem() == Items.FLINT_AND_STEEL && !playerIn.isSneaking()) {
            if (tile instanceof TileEntityBurningChamber) {
                ((TileEntityBurningChamber) tile).makeWork();
            }
        }
        if (playerIn.isSneaking() && ((TileEntityBurningChamber)tile).isStructureValid(pos.getX(), pos.getY(), pos.getZ())) {
            playerIn.openGui(QuantumElectriX.instance, GuiHandler.GUI_SULFUR_SMELT_FURNACE, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }
        if (tile instanceof TileEntityBurningChamber) {
            if (heldItem.getItem() == Items.BUCKET && ((TileEntityBurningChamber)tile).getSO2().getFluidAmount() >= 1000) {
                playerIn.setHeldItem(hand, FluidUtil.getFilledBucket(new FluidStack(FluidLoader.chemistrySO2, 1000)));
                ((TileEntityBurningChamber)tile).getSO2().drain(new FluidStack(FluidLoader.chemistrySO2, 1000), true);
            }
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityBurningChamber();
    }
}
