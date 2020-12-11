package timicasto.quantumelectrix.api;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OtherInterfaces {
        public interface IMeta {
                String getName();

                IProperty getProperty();

                Enum[] getMetaEnum();

                IBlockState getInvState(int meta);

                boolean customStateMapper();

                String getCustomStateMap(int meta, boolean itemBlock);

                @SideOnly(Side.CLIENT)
                StateMapperBase getCustomMapper();

                boolean appendProperties();
        }

        public static final PropertyDirection FACING_HORIZONTAL = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
        public static final PropertyBool DYNAMIC_RENDERER = PropertyBool.create("dynamicrender");
        public static final PropertyBool MULTI_BLOCK_SLAVE = PropertyBool.create("multiblockslave");
        public static final IUnlistedProperty<HashMap> OBJ_TEXTURE_REMAP = new IUnlistedProperty<HashMap>()
        {
                @Override
                public String getName()
                {
                        return "remapped_obj";
                }
                @Override
                public boolean isValid(HashMap value)
                {
                        return true;
                }
                @Override
                public Class<HashMap> getType() {
                        return HashMap.class;
                }
                @Override
                public String valueToString(HashMap value)
                {
                        return value.toString();
                }
        };

        public interface ITileWithGui {
                default boolean canInteractWith(EntityPlayer player) {
                        return canInteractWith();
                }
                boolean canInteractWith();
                int getGui();
                TileEntity getContainerTile();
                default void onGuiOpen(EntityPlayer player, boolean isClient) {}
        }

        public interface IAdvancedSelectionBounds extends IDirectionalTile.IBlockBounds {
                List<AxisAlignedBB> getAdvancedSelectionBound();
                boolean isOverrided(AxisAlignedBB box, EntityPlayer player, RayTraceResult mop, ArrayList<AxisAlignedBB> arrayList);
        }

        public interface IAdvancedCollisionBounds extends IDirectionalTile.IBlockBounds {
                List<AxisAlignedBB> getCollisionBound();
        }
}













