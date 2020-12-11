package timicasto.quantumelectrix.block.multiblocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.Properties;
import timicasto.quantumelectrix.api.IDirectionalTile;
import timicasto.quantumelectrix.api.OtherInterfaces;
import timicasto.quantumelectrix.block.types.BlockTypesMultiBlock;
import timicasto.quantumelectrix.tile.TileEntityContactChamber;
import timicasto.quantumelectrix.tile.multiblock.TileEntityMultiBlockPart;
import timicasto.quantumelectrix.tile.multiblock.TileEntityMultiblock;

public class BlockInstanceMultiBlock extends BlockMultiBlocks<BlockTypesMultiBlock> {
    public BlockInstanceMultiBlock() {
        super("multiblock_instance", Material.IRON, PropertyEnum.create("type", BlockTypesMultiBlock.class), ItemBlock.class, OtherInterfaces.DYNAMIC_RENDERER, IDirectionalTile.BOOLEANS[0], Properties.AnimationProperty, OtherInterfaces.OBJ_TEXTURE_REMAP);
        setHardness(3.0F);
        setResistance(15.0F);
        setMetaLayer(BlockTypesMultiBlock.CONTACT_CHAMBER.getMeta(), BlockRenderLayer.SOLID, BlockRenderLayer.TRANSLUCENT);
        this.allNotNormalBlock();
        lightOpacity = 0;
        setUnlocalizedName("multiblock_instance");
    }

    @Override
    public boolean customStateMapper() {
        return true;
    }

    @Override
    public String getCustomStateMap(int meta, boolean itemBlock) {
        if (BlockTypesMultiBlock.values()[meta].needCustomState()) {
            return BlockTypesMultiBlock.values()[meta].getCustomState();
        }
        return null;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        switch (BlockTypesMultiBlock.values()[getMetaFromState(state)]) {
            case CONTACT_CHAMBER:
                return new TileEntityContactChamber();
        }
        return null;
    }

    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMultiBlockPart) {
            TileEntityMultiBlockPart tileEntity = (TileEntityMultiBlockPart)tile;
            if (tileEntity instanceof TileEntityMultiblock && ((TileEntityMultiblock)tileEntity).isRedstonePos()) {
                return true;
            }
            if (tile instanceof TileEntityContactChamber) {
                return tileEntity.pos < 3 || (tileEntity.pos == 7 && side == EnumFacing.UP);
            }
        }
        return super.isSideSolid(base_state, world, pos, side);
    }

    @Override
    public boolean allowHammerHarvest(IBlockState blockState) {
        return true;
    }
}
