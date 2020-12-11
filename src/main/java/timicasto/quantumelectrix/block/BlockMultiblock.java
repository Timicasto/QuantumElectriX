package timicasto.quantumelectrix.block;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.api.IDirectionalTile;
import timicasto.quantumelectrix.api.OtherInterfaces;
import timicasto.quantumelectrix.block.types.BlockTypesMultiBlock;
import timicasto.quantumelectrix.creative.TabLoader;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class BlockMultiblock<E extends Enum<E> & BlockMultiblock.IBlockEnum> extends Block implements OtherInterfaces.IMeta {

    protected static IProperty[] temp;
    protected static IUnlistedProperty[] tempUnlisted;
    public final String name;
    public final PropertyEnum<E> property;
    public final IProperty[] additional;
    public final IUnlistedProperty[] additionalUnlisted;
    public final E[] enumValue;
    boolean[] isHidden;
    boolean[] hasFlavour;
    protected Set<BlockRenderLayer> renderLayerSet = Sets.newHashSet(BlockRenderLayer.SOLID);
    protected Set<BlockRenderLayer>[] renderLayers;
    protected Map<Integer, Integer> lightOpacities = Maps.newHashMap();
    protected Map<Integer, Float> hardness = Maps.newHashMap();
    protected Map<Integer, Integer> resistance = Maps.newHashMap();
    protected boolean[] toolHarvest;
    protected boolean[] notNormalBlock;
    private boolean opaqueCube = false;
    private Logger logger = LogManager.getLogger();

    public BlockMultiblock(String name, Material material, PropertyEnum<E> property, Class<? extends ItemBlock> itemBlock, Object... additional) {
        super(setTempProperties(material, property, additional));
        this.name = name;
        this.property = property;
        this.enumValue = property.getValueClass().getEnumConstants();
        this.isHidden = new boolean[this.enumValue.length];
        this.hasFlavour = new boolean[this.enumValue.length];
        this.renderLayers = new Set[this.enumValue.length];
        this.toolHarvest = new boolean[this.enumValue.length];

        ArrayList<IProperty> properties = new ArrayList<IProperty>();
        ArrayList<IUnlistedProperty> unlistedProperties = new ArrayList<IUnlistedProperty>();
        for (Object obj : additional) {
            if (obj instanceof IProperty) {
                properties.add((IProperty)obj);
            }
            if (obj instanceof IProperty[]) {
                for (IProperty i : ((IProperty[])obj)) {
                    properties.add(i);
                }
            }
            if (obj instanceof IUnlistedProperty) {
                unlistedProperties.add((IUnlistedProperty)obj);
            }
            if (obj instanceof IUnlistedProperty[]) {
                for (IUnlistedProperty i : ((IUnlistedProperty[])obj)) {
                    unlistedProperties.add(i);
                }
            }
        }
        this.additional = properties.toArray(new IProperty[properties.size()]);
        this.additionalUnlisted = unlistedProperties.toArray(new IUnlistedProperty[unlistedProperties.size()]);
        this.setDefaultState(getInitDefaultState());
        String registryName = regName();
        this.setUnlocalizedName(registryName);
        this.setRegistryName(registryName);
        this.setCreativeTab(TabLoader.elecTab);
        lightOpacity = 255;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Enum[] getMetaEnum() {
        return enumValue;
    }

    @Override
    public IBlockState getInvState(int meta) {
        IBlockState state = this.blockState.getBaseState().withProperty(this.property, enumValue[meta]);
        return state;
    }

    @Override
    public PropertyEnum<E> getProperty() {
        return property;
    }

    @Override
    public boolean customStateMapper() {
        return false;
    }

    @Override
    public String getCustomStateMap(int meta, boolean itemBlock) {
        return null;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public StateMapperBase getCustomMapper() {
        return null;
    }

    @Override
    public boolean appendProperties() {
        return true;
    }

    public String getUnlocalizedName(ItemStack stack) {
        String name = getStateFromMeta(stack.getItemDamage()).getValue(property).toString().toLowerCase(Locale.US);
        return super.getUnlocalizedName() + "." + name;
    }

    protected static Material setTempProperties(Material material, PropertyEnum<?> property, Object... additional) {
        ArrayList<IProperty> properties = new ArrayList<IProperty>();
        ArrayList<IUnlistedProperty> unlistedProperties = new ArrayList<IUnlistedProperty>();
        for (Object obj : additional) {
            if (obj instanceof IProperty) {
                properties.add((IProperty)obj);
            }
            if (obj instanceof IProperty[]) {
                for (IProperty i : ((IProperty[])obj)) {
                    properties.add(i);
                }
            }
            if (obj instanceof IUnlistedProperty) {
                unlistedProperties.add((IUnlistedProperty)obj);
            }
            if (obj instanceof IUnlistedProperty[]) {
                for (IUnlistedProperty i : ((IUnlistedProperty[])obj)) {
                    unlistedProperties.add(i);
                }
            }
        }
        temp = properties.toArray(new IProperty[properties.size()]);
        tempUnlisted = unlistedProperties.toArray(new IUnlistedProperty[unlistedProperties.size()]);
        return material;
    }

    protected static Object[] combineProperties(Object[] currentProperties, Object... added) {
        Object[] array = new Object[currentProperties.length + added.length];
        for (int i = 0 ; i < currentProperties.length ; i++) {
            array[i] = currentProperties[1];
        }
        for (int i = 0 ; i < added.length ; i++) {
            array[currentProperties.length + i] = added[i];
        }
        return array;
    }

    public BlockMultiblock setMetaHidden(int... meta) {
        for (int i : meta) {
            if (i >= 0 && i < this.isHidden.length) {
                this.isHidden[i] = true;
            }
        }
        return this;
    }

    public BlockMultiblock setMetaUnhidden(int... meta) {
        for (int i : meta) {
            if (i >= 0 && i < this.isHidden.length) {
                this.isHidden[i] = false;
            }
        }
        return this;
    }

    public boolean isHidden(int meta) {
        return this.isHidden[Math.max(0, Math.min(meta, this.isHidden.length - 1))];
    }

    public BlockMultiblock setHasFlavor(int... meta) {
        if (meta == null || meta.length < 1) {
            for (int i = 0 ; i < hasFlavour.length ; i++) {
                this.hasFlavour[i] = true;
            }
        } else {
            for (int i : meta) {
                if (i >= 0 && i < this.hasFlavour.length) {
                    this.hasFlavour[i] = false;
                }
            }
        }
        return this;
    }

    public boolean hasFlavor(ItemStack stack) {
        return this.hasFlavour[Math.max(0, Math.min(stack.getItemDamage(), this.hasFlavour.length - 1))];
    }

    public BlockMultiblock<E> setLayer(BlockRenderLayer... layer) {
        this.renderLayerSet = Sets.newHashSet(layer);
        return this;
    }

    public BlockMultiblock<E> setMetaLayer(int meta, BlockRenderLayer... layers) {
        this.renderLayers[Math.max(0, Math.min(meta, this.renderLayers.length - 1))] = Sets.newHashSet(layers);
        return this;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        if (cachedState != null) {
            int meta = this.getMetaFromState(cachedState);
            if (meta >= 0 && meta < renderLayers.length && renderLayers[meta] != null) {
                return renderLayers[meta].contains(layer);
            }
        }
        return renderLayerSet.contains(layer);
    }

    public BlockMultiblock<E> setLightOpacity(int meta, int opacity) {
        lightOpacities.put(meta, opacity);
        return this;
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        int meta = getMetaFromState(state);
        if (lightOpacities.containsKey(meta)) {
            return lightOpacities.get(meta);
        }
        return super.getLightOpacity(state, world, pos);
    }

    public BlockMultiblock<E> setHardness(int meta, float hardness) {
        this.hardness.put(meta, hardness);
        return this;
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        int meta = getMetaFromState(blockState);
        if (this.hardness.containsKey(meta)) {
            return this.hardness.get(meta);
        }
        return super.getBlockHardness(blockState, worldIn, pos);
    }

    public BlockMultiblock<E> setExplosionResistance(int meta, int resistance) {
        this.resistance.put(meta, resistance);
        return this;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        int meta = getMetaFromState(world.getBlockState(pos));
        if (this.resistance.containsKey(meta)) {
            return this.resistance.get(meta);
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

    public BlockMultiblock<E> notNormalBlock(int meta) {
        if (notNormalBlock == null) {
            notNormalBlock = new boolean[this.enumValue.length];
        }
        notNormalBlock[meta] = true;
        return this;
    }

    public BlockMultiblock<E> allNotNormalBlock() {
        if (notNormalBlock == null) {
            notNormalBlock = new boolean[this.enumValue.length];
        }
        for (int i = 0 ; i < notNormalBlock.length ; i++) {
            notNormalBlock[i] = true;
        }
        return this;
    }

    protected boolean normalCheck(IBlockState state) {
        if (notNormalBlock == null) {
            return true;
        }
        int meta = getMetaFromState(state);
        return (meta < 0 || meta >= notNormalBlock.length || !notNormalBlock[meta]);
    }

    @Override
    public boolean isFullBlock(IBlockState state) {
        return normalCheck(state);
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return normalCheck(state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return normalCheck(state);
    }

    @Override
    public boolean causesSuffocation(IBlockState state) {
        if (notNormalBlock == null) {
            return true;
        }
        int m = 0;
        for (boolean i : notNormalBlock) {
            if (i) {
                m++;
            }
        }
        return m < notNormalBlock.length / 2;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        return normalCheck(state);
    }

    protected static IBlockState cachedState;

    @Override
    public boolean hasTileEntity(IBlockState state) {
        cachedState = state;
        return super.hasTileEntity(state);
    }

    protected BlockStateContainer createBlockStateWithoutTemp() {
        return new BlockStateContainer(this, this.property);
    }

    @SuppressWarnings("unchecked")
    protected IBlockState getInitDefaultState() {
        return this.blockState.getBaseState().withProperty((IProperty) this.property, (Comparable)enumValue[0]);
    }

    protected <V extends Comparable<V>> IBlockState applyProperty(IBlockState state, IProperty<V> property, Object var) {
        return state.withProperty(property, (V)var);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityLivingBase placer, ItemStack stack) {

    }

    public boolean canBlockPlace(World world, BlockPos pos, IBlockState newState, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        IProperty[] properties = new IProperty[7];
        PropertyBool boolProperty = PropertyBool.create("bool0");
        PropertyEnum enumProperty = PropertyEnum.create("type", BlockTypesMultiBlock.class);
        PropertyDirection facingProperty = BlockHorizontal.FACING;
        PropertyBool boolProperty1 = PropertyBool.create("bool0");
        PropertyBool boolProperty2 = PropertyBool.create("bool0");
        PropertyBool boolProperty3 = PropertyBool.create("bool0");
        PropertyBool boolSlave = PropertyBool.create("multiblockslave");
        properties[0] = enumProperty;
        properties[1] = boolProperty;
        properties[2] = facingProperty;
        properties[3] = boolProperty1;
        properties[4] = boolProperty2;
        properties[5] = boolProperty3;
        properties[6] = boolSlave;
        return new BlockStateContainer(this, properties);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state == null || !this.equals(state.getBlock())) {
            return 0;
        }
        return state.getValue(this.property).getMeta();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        for (int i = 0 ; i < this.additional.length ; i++) {
            if (this.additional[i] != null && !this.additional[i].getAllowedValues().isEmpty()) {
                state = applyProperty(state, this.additional[i], this.additional[i].getAllowedValues().toArray()[0]);
            }
        }
        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState state = this.getDefaultState().withProperty(this.property, fromMeta(meta));
        for (int i = 0 ; i < this.additional.length ; i++) {
            state = applyProperty(state, this.additional[i], this.additional[i].getAllowedValues().toArray()[0]);
        }
        return state;
    }

    protected E fromMeta(int meta) {
        if (meta < 0 || meta >= enumValue.length) {
            meta = 0;
        }
        return enumValue[meta];
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (E type : this.enumValue) {
            if (type.creativeList() && !this.isHidden[type.getMeta()]) {
                items.add(new ItemStack(this, 1, type.getMeta()));
            }
        }
    }

    @Override
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
        if (worldIn.isRemote && id == 255) {
            worldIn.notifyBlockUpdate(pos, state, state, 3);
            return true;
        }
        return super.eventReceived(state, worldIn, pos, id, param);
    }

    public BlockMultiblock<E> setMetaHammerHarvest(int meta) {
        toolHarvest[meta] = true;
        return this;
    }

    public BlockMultiblock<E> setHammerHarvest() {
        for(int i = 0; i < notNormalBlock.length; i++) {
            toolHarvest[i] = true;
        }
        return this;
    }

    public boolean allowHammerHarvest(IBlockState blockState) {
        int meta = getMetaFromState(blockState);
        if(meta>=0&&meta<toolHarvest.length) {
            return toolHarvest[meta];
        }
        return false;
    }

    public boolean isOpaqueCube() {
        return opaqueCube;
    }
    public BlockMultiblock<E> setOpaque(boolean isOpaque) {
        opaqueCube = isOpaque;
        fullBlock = isOpaque;
        return this;
    }

    @Override
    public boolean isToolEffective(String type, IBlockState state) {
        if (allowHammerHarvest(state) && type.equals("TOOL_BOX")) {
            return true;
        }
        return super.isToolEffective(type, state);
    }

    public String regName() {
        return name;
    }

    public static interface IBlockEnum extends IStringSerializable {
        int getMeta();
        boolean creativeList();
    }
}
