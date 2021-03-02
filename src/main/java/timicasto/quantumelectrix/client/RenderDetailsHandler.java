package timicasto.quantumelectrix.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import timicasto.quantumelectrix.api.Colors;
import timicasto.quantumelectrix.api.ints.ITileWithTiers;

public class RenderDetailsHandler {

    // Start constant values
    public static final double centerOff = 0.20000000298023224D;
    public static final double sideOff = 0.10000000149011612D;
    // End constant values

    /**
     * Generates block hit particles for block, depends on the tier color
     *
     * @param world     current world
     * @param facing    position of target block
     * @param pos       the side that be hit
     * @param manager   vanilla particle manager(s)
     * @return          true if handled
     *
     * @see ParticleManager#addBlockHitEffects(BlockPos, EnumFacing) for base source
     */
    public static boolean addBlockHitEffect(World world, EnumFacing facing, BlockPos pos, ParticleManager manager) {
        TileEntity tile = world.getTileEntity(pos);
        Colors color = null;
        IBlockState blockState = world.getBlockState(pos);
        blockState = blockState.getBlock().getActualState(blockState, world, pos);
        if (tile instanceof ITileWithTiers) {
            color = ((ITileWithTiers)tile).getTier().getColor();
        }
        if (blockState.getRenderType() != EnumBlockRenderType.INVISIBLE) {
            int blockX = pos.getX();
            int blockY = pos.getY();
            int blockZ = pos.getZ();
            AxisAlignedBB bb = blockState.getBoundingBox(world, pos);
            double xParticle = (double)blockX + world.rand.nextDouble() * (bb.maxX - bb.minX - centerOff) + sideOff + bb.minX;
            double yParticle = (double)blockX + world.rand.nextDouble() * (bb.maxY - bb.minY - centerOff) + sideOff + bb.minY;
            double zParticle = (double)blockZ + world.rand.nextDouble() * (bb.maxZ - bb.minZ - centerOff) + sideOff + bb.minZ;
            switch (facing) {
                case DOWN:
                    yParticle = (double)blockY + bb.minY - sideOff;
                    break;
                case UP:
                    yParticle = (double)blockY + bb.maxY + sideOff;
                    break;
                case NORTH:
                    zParticle = (double)blockZ + bb.minZ - sideOff;
                    break;
                case SOUTH:
                    zParticle = (double)blockZ + bb.maxZ + sideOff;
                    break;
                case WEST:

            }
        }return true;
    }

}
