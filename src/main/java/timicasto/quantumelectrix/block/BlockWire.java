package timicasto.quantumelectrix.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.IExtendedBlockState;
import timicasto.quantumelectrix.api.TransmittionType;
import timicasto.quantumelectrix.api.Utils;
import timicasto.quantumelectrix.block.statements.BlockStateProperty;
import timicasto.quantumelectrix.block.statements.PropertyConnection;
import timicasto.quantumelectrix.block.statements.TransmitterType;
import timicasto.quantumelectrix.block.types.AxisTypeWire;
import timicasto.quantumelectrix.creative.TabLoader;
import timicasto.quantumelectrix.tile.TileEntityAbsWire;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class BlockWire extends Block implements ITileEntityProvider {
    public static AxisAlignedBB smallDef;
    public static AxisAlignedBB largeDef;

    public BlockWire() {
        super(Material.PISTON);
        setCreativeTab(TabLoader.elecTab);
        setHardness(1F);
        setResistance(1F);
    }

    private static AxisAlignedBB getDefaultForTile(TileEntityAbsWire tile) {
        if (tile == null || tile.getTransmitterType().getSize() == TransmitterType.Size.SMALL) {
            return AxisTypeWire.getBoundingBox(1)[6];
        }
        return AxisTypeWire.getBoundingBox(2)[6];
    }

    private static void setDefaultForTile(TileEntityAbsWire tile, AxisAlignedBB boundBox) {
        if (tile == null) {
            return;
        }
        if (boundBox == null) {
            throw new IllegalStateException("box shouldnt be null");
        }
        if (tile.getTransmitterType().getSize() == TransmitterType.Size.SMALL) {
            smallDef = boundBox;
        } else {
            largeDef = boundBox;
        }
    }

    private static TileEntityAbsWire getTile(IBlockAccess access, BlockPos pos) {
        TileEntity tile = access.getTileEntity(pos);
        TileEntityAbsWire wire = null;
        if (tile instanceof TileEntityAbsWire) {
            wire = (TileEntityAbsWire)tile;
        }
        return wire;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileEntityAbsWire tile = getTile(worldIn, pos);
        if (tile != null) {
            state = state.withProperty(BlockStateProperty.tier, tile.getTier());
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        TransmitterType type = TransmitterType.get(meta);
        return getDefaultState().withProperty(BlockStateProperty.type, type);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        TransmitterType type = state.getValue(BlockStateProperty.type);
        return type.ordinal();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateProperty(this);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityAbsWire tile = getTile(world, pos);
        if (tile != null) {
            return tile.getExtendedState(state);
        }
        TileEntityAbsWire.ConnectionType[] types = new TileEntityAbsWire.ConnectionType[]{TileEntityAbsWire.ConnectionType.NORMAL, TileEntityAbsWire.ConnectionType.NORMAL, TileEntityAbsWire.ConnectionType.NORMAL, TileEntityAbsWire.ConnectionType.NORMAL, TileEntityAbsWire.ConnectionType.NORMAL, TileEntityAbsWire.ConnectionType.NORMAL};
        PropertyConnection property = new PropertyConnection((byte)0, (byte)0, types, true);
        return ((IExtendedBlockState)state).withProperty(OBJModel.OBJProperty.INSTANCE, new OBJModel.OBJState(Collections.emptyList(), true)).withProperty(PropertyConnection.INSTANCE, property);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntityAbsWire tile = getTile(source, pos);
        if (tile != null && tile.getTransmitterType().getSize() == TransmitterType.Size.SMALL) {
            return AxisTypeWire.getBoundingBox(1)[6];
        }
        return AxisTypeWire.getBoundingBox(2)[6];
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState) {
        TileEntityAbsWire tile = getTile(worldIn, pos);
        if (tile != null) {
            List<AxisAlignedBB> bbs = tile.getCollecsionBoxes(entityBox.offset(-pos.getX(), -pos.getY(), -pos.getZ()));
            for (AxisAlignedBB box : bbs) {
                collidingBoxes.add(box.offset(pos));
            }
        }
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return getDefaultForTile(getTile(worldIn, pos)).offset(pos);
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntityAbsWire tile = getTile(worldIn, pos);
        if (tile == null) {
            return null;
        }
        List<AxisAlignedBB> boxes = tile.getCollisionBoxes();
        Utils.AdvancedRayTraceResult result = Utils.collisionTrace(pos, start, end, boxes);
        if (result != null && result.valid()) {
            setDefaultForTile(tile, result.bounds);
        }
        return result != null ? result.hit : null;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        ItemStack stack = playerIn.getHeldItem(hand);
        if (stack.isEmpty()) {
            return false;
        }
        return true;
    }

   /* public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        if (!target.typeOfHit.equals(RayTraceResult.Type.BLOCK)) {
            return super.addHitEffects(state, worldObj, target, manager);
        }

    }*/
}
