package timicasto.quantumelectrix.block.statements;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.api.Colors;
import timicasto.quantumelectrix.api.Tiers;
import timicasto.quantumelectrix.block.BlockWire;

import javax.annotation.Nonnull;

public class BlockStateProperty extends ExtendedBlockState {
    public static final PropertyEnum<TransmitterType> type = PropertyEnum.create("transmission", TransmitterType.class);
    public static final PropertyEnum<Tiers> tier = PropertyEnum.create("tier", Tiers.class);

    public BlockStateProperty(Block blockIn) {
        super(blockIn, new IProperty[]{type, tier}, new IUnlistedProperty[]{OBJModel.OBJProperty.INSTANCE, ColorProperty.INSTANCE, PropertyConnection.INSTANCE});
    }

    public static class TransmitterMap /*extends StateMapperBase */{
       /* @Override
        protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
            BlockWire block = (BlockWire)state.getBlock();
            TransmitterType type = state.getValue(type);
            StringBuilder builder = new StringBuilder();
            String orName = null;
            if (type.tiers) {
                Tiers tier = state.getValue(tier);
                orName = type.getName() + "." + tier.getName();
            }
            if (builder.length() == 0) {
                builder.append("normal");
            }
            ResourceLocation location = new ResourceLocation(QuantumElectriX.MODID, orName != null ? orName : type.getName());
            return new ModelResourceLocation(location, builder.toString());
        }*/
    }

    public static class ColorProperty implements IUnlistedProperty<ColorProperty> {
        public static ColorProperty INSTANCE = new ColorProperty();
        public Colors colors;
        public ColorProperty() {}
        public ColorProperty(Colors color) {
            colors = color;
        }

        @Override
        public String getName() {
            return "color";
        }

        @Override
        public boolean isValid(ColorProperty value) {
            return true;
        }

        @Override
        public Class<ColorProperty> getType() {
            return ColorProperty.class;
        }

        @Override
        public String valueToString(ColorProperty value) {
            return colors.name;
        }
    }
}
