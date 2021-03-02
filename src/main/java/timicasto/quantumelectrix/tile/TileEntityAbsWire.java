package timicasto.quantumelectrix.tile;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJModel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.api.*;
import timicasto.quantumelectrix.api.ints.*;
import timicasto.quantumelectrix.block.statements.PropertyConnection;
import timicasto.quantumelectrix.block.statements.TransmitterType;
import timicasto.quantumelectrix.block.types.AxisTypeWire;
import timicasto.quantumelectrix.capability.CapabilitiesRegistryHandler;
import timicasto.quantumelectrix.capability.CapabilityUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class TileEntityAbsWire extends TileEntity implements ITickable, IBlockableConnect, ITransmitter, INetworkTile, IConfigable, ITileWithTiers {
    public int delay;
    public byte acceptorConnections = 0x00;
    public byte transmitterConnections = 0x00;
    public boolean sendDesc = false;
    private boolean powered = false;
    private boolean reactive = false;
    public boolean update = true;
    public boolean rsSet = false;
    public ConnectionType[] connectionTypes = {ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL, ConnectionType.NORMAL};
    public TileEntity[] cachedAcceptors = new TileEntity[6];

    public static boolean connectionMapContain(byte connections, EnumFacing facing) {
        byte test = (byte) (1 << facing.ordinal());
        return (connections & test) > 0;
    }

    public static byte setConnectionByte(byte connections, boolean tar, EnumFacing facing) {
        return (byte)((connections & ~(byte) (1 << facing.ordinal())) | (byte)((tar ? 1 : 0) << facing.ordinal()));
    }

    public static ConnectionType getConnectionType(EnumFacing facing, byte allConnections, byte transmitterConnections, ConnectionType[] types) {
        if (!connectionMapContain(allConnections, facing)) {
            return ConnectionType.NONE;
        } else if (connectionMapContain(transmitterConnections, facing)) {
            return ConnectionType.NORMAL;
        }
        return types[facing.ordinal()];
    }

    @Override
    public void update() {
        if (world.isRemote) {
            if (delay == 5) {
                delay = 6;
                refreshConnections();
            } else if (delay < 5) {
                delay++;
            }
        } else {
            if (update) {
                refreshConnections();
                update = false;
            }
            if (sendDesc) {
                QuantumElectriX.packetHandler.sendUpdatePacket(this);
                sendDesc = false;
            }
        }
    }

    @Override
    public Tiers getTier() {
        return Tiers.max2A;
    }

    public void setTier(Tiers tier) {

    }

    public boolean handlesRedstone() {
        return true;
    }

    public boolean renderCenter() {
        return false;
    }

    public byte getPossibleTransmitterConnections() {
        byte connections = 0x00;
        if (handlesRedstone() && reactive && powered) {
            return connections;
        }
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (canConnect(facing)) {
                TileEntity tile = world.getTileEntity(getPos().offset(facing));
                if (CapabilityUtils.hasCapability(tile, CapabilitiesRegistryHandler.GRID_TRANSMITTER, facing.getOpposite()) && TransmittionType.checkTransmissionType((TileEntity) CapabilityUtils.getCapability(tile, CapabilitiesRegistryHandler.GRID_TRANSMITTER, facing.getOpposite()), getTransmitterType().getTransmission()) && isValidTransmitter(tile)) {
                    connections |= 1 << facing.ordinal();
                }
            }
        }
        return connections;
    }

    public boolean getPossibleAcceptorConnections(EnumFacing facing) {
        if (handlesRedstone() && reactive && powered) {
            return false;
        }
        if (canMutualConnect(facing)) {
            TileEntity tile = world.getTileEntity(getPos().offset(facing));
            if (isValidAcceptor(tile, facing)) {
                if (cachedAcceptors[facing.ordinal()] != tile) {
                    cachedAcceptors[facing.ordinal()] = tile;
                    markDirtyAcceptors(facing);
                }
                return true;
            }
        }
        if (cachedAcceptors[facing.ordinal()] != null) {
            cachedAcceptors[facing.ordinal()] = null;
            markDirtyAcceptors(facing);
        }
        return false;
    }

    public boolean getPossibleTransmitterConnections(EnumFacing facing) {
        if (handlesRedstone() && reactive && powered) {
            return false;
        }
        if (canMutualConnect(facing)) {
            TileEntity tile = world.getTileEntity(getPos().offset(facing));
            return CapabilityUtils.hasCapability(tile, CapabilitiesRegistryHandler.GRID_TRANSMITTER, facing.getOpposite()) && TransmittionType.checkTransmissionType((TileEntity) CapabilityUtils.getCapability(tile, CapabilitiesRegistryHandler.GRID_TRANSMITTER, facing.getOpposite()), getTransmitterType().getTransmission()) && isValidTransmitter(tile);
        }
        return false;
    }

    public byte getPossibleAcceptorConnections() {
        byte connections = 0x00;
        if (handlesRedstone() && reactive && powered) {
            return connections;
        }
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (canMutualConnect(facing)) {
                Coord4D coord = new Coord4D(getPos(), getWorld()).offset(facing);
                if (!getWorld().isRemote && !coord.exists(getWorld())) {
                    update = true;
                    continue;
                }
                TileEntity tile = coord.getTileEntity(getWorld());
                if (isValidAcceptor(tile, facing)) {
                    if (cachedAcceptors[facing.ordinal()] != tile) {
                        cachedAcceptors[facing.ordinal()] = tile;
                        markDirtyAcceptors(facing);
                    }
                    connections |= 1 << facing.ordinal();
                    continue;
                }
            }
            if (cachedAcceptors[facing.ordinal()] != null) {
                cachedAcceptors[facing.ordinal()] = null;
                markDirtyAcceptors(facing);
            }
        }
        return connections;
    }

    public byte getAllCurrentConnections() {
        return (byte) (transmitterConnections |acceptorConnections);
    }

    public boolean isValidTransmitter(TileEntity tile) {
        return true;
    }

    public List<AxisAlignedBB> getCollisionBoxes() {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            int ordinal = facing.ordinal();
            byte connections = getAllCurrentConnections();
            if (connectionMapContain(connections, facing)) {
                list.add(getTransmitterType().getSize() == TransmitterType.Size.SMALL ? AxisTypeWire.getBoundingBox(1)[ordinal] : AxisTypeWire.getBoundingBox(2)[ordinal]);
            }
        }
        list.add(getTransmitterType().getSize() == TransmitterType.Size.SMALL ? AxisTypeWire.getBoundingBox(1)[6] : AxisTypeWire.getBoundingBox(2)[6]);
        return list;
    }

    public abstract TransmitterType getTransmitterType();

    public List<AxisAlignedBB> getCollecsionBoxes(AxisAlignedBB entityBox) {
        List<AxisAlignedBB> list = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            int ordinal = facing.ordinal();
            byte connections = getAllCurrentConnections();
            if (connectionMapContain(connections, facing)) {
                AxisAlignedBB box = getTransmitterType().getSize() == TransmitterType.Size.SMALL ? AxisTypeWire.getBoundingBox(1)[ordinal] : AxisTypeWire.getBoundingBox(2)[ordinal];
                if (box.intersects(entityBox)) {
                    list.add(box);
                }
            }
        }
        AxisAlignedBB box = getTransmitterType().getSize() == TransmitterType.Size.SMALL ? AxisTypeWire.getBoundingBox(1)[6] : AxisTypeWire.getBoundingBox(2)[6];
        if (box.intersects(entityBox)) {
            list.add(box);
        }
        return list;
    }

    public abstract boolean isValidAcceptor(TileEntity tile, EnumFacing facing);

    @Override
    public boolean canMutualConnect(EnumFacing facing) {
        if (!canConnect(facing)) {
            return false;
        }
        final BlockPos pos = getPos().offset(facing);
        final TileEntity tile = world.getTileEntity(pos);
        if (!CapabilityUtils.hasCapability(tile, CapabilitiesRegistryHandler.BLOCKABLE_CONNECT, facing.getOpposite())) {
            return true;
        }
        return CapabilityUtils.getCapability(tile, CapabilitiesRegistryHandler.BLOCKABLE_CONNECT, facing.getOpposite()).canConnect(facing.getOpposite());
    }

    @Override
    public boolean canConnect(EnumFacing facing) {
        if (connectionTypes[facing.ordinal()] == ConnectionType.NONE) {
            return false;
        }
        if (handlesRedstone()) {
            if (!rsSet) {
                if (reactive) {
                    powered = Utils.isPowered(getWorld(), new Coord4D(getPos(), getWorld()));
                } else {
                    powered = false;
                }
                rsSet = true;
            }
            if (reactive && powered) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void handlePacketData(ByteBuf buf) throws Exception {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            transmitterConnections = buf.readByte();
            acceptorConnections = buf.readByte();
            for (int i = 0 ; i < 6 ; i++) {
                connectionTypes[i] = ConnectionType.values()[buf.readInt()];
            }
            markDirty();
            Utils.updateBlock(world, pos);
        }
    }

    @Override
    public TileNetworkList getNetworkedData(TileNetworkList data) {
        data.add(transmitterConnections);
        data.add(acceptorConnections);
        for (int i = 0 ; i < 6 ; i++) {
            data.add(connectionTypes[i].ordinal());
        }
        return data;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        reactive = compound.getBoolean("reactive");
        for (int i = 0 ; i < 6 ; i++) {
            connectionTypes[i] = ConnectionType.values()[compound.getInteger("connection" + i)];
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("reactive", reactive);
        for (int i = 0 ; i < 6 ; i++) {
            compound.setInteger("connection" + i, connectionTypes[i].ordinal());
        }
        return compound;
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound compound = super.getUpdateTag();
        compound.setInteger("tier", getTier().ordinal());
        return compound;
    }

    protected void onRefresh() {}

    public void refreshConnections() {
        if (handlesRedstone()) {
            boolean previousPowered = powered;
            if (reactive) {
                powered = Utils.isPowered(getWorld(), new Coord4D(getPos(), getWorld()));
            } else {
                powered = false;
            }
            if (previousPowered != powered) {
                markDirtyTransmitters();
            }
            rsSet = true;
        }
        if (!getWorld().isRemote) {
            byte possibleTransmitters = getPossibleTransmitterConnections();
            byte possibleAcceptors = getPossibleAcceptorConnections();
            byte newEnabledTransmitters = 0;

            if ((possibleTransmitters | possibleAcceptors) != getAllCurrentConnections()) {
                sendDesc = true;
                if (possibleTransmitters != transmitterConnections) {
                    newEnabledTransmitters = (byte) (possibleTransmitters ^ transmitterConnections);
                    newEnabledTransmitters &= ~transmitterConnections;
                }
            }
            transmitterConnections = possibleTransmitters;
            acceptorConnections = possibleAcceptors;
            if (newEnabledTransmitters != 0) {
                recheckConnections(newEnabledTransmitters);
            }
        }
    }

    public void refreshConnections(EnumFacing facing) {
        if (!getWorld().isRemote) {
            boolean isPossibleTransmitter = getPossibleTransmitterConnections(facing);
            boolean isPossibleAcceptor = getPossibleAcceptorConnections(facing);
            boolean wasTransmitterChanged = false;

            if ((isPossibleTransmitter || isPossibleAcceptor) != connectionMapContain(getAllCurrentConnections(), facing)) {
                sendDesc = true;
                if (isPossibleTransmitter != connectionMapContain(transmitterConnections, facing)) {
                    wasTransmitterChanged = isPossibleTransmitter;
                }
            }
            transmitterConnections = setConnectionByte(transmitterConnections, isPossibleTransmitter, facing);
            acceptorConnections = setConnectionByte(acceptorConnections, isPossibleAcceptor, facing);
            if (wasTransmitterChanged) {
                recheckConnection(facing);
            }

        }
    }

    protected void recheckConnections(byte newEnabledTransmitters) {
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (connectionMapContain(newEnabledTransmitters, facing)) {
                TileEntity tile = world.getTileEntity(getPos().offset(facing));
                if (tile instanceof TileEntityAbsWire) {
                    ((TileEntityAbsWire)tile).refreshConnections();
                }
            }
        }
    }

    protected void recheckConnection(EnumFacing facing) {}

    protected void onModeChange(EnumFacing facing) {
        markDirtyAcceptors(facing);
        if (getPossibleTransmitterConnections() != transmitterConnections) {
            markDirtyTransmitters();
        }
        markDirty();
    }

    protected void markDirtyTransmitters() {
        notifyTileChange();
    }

    protected void markDirtyAcceptors(EnumFacing facing) {

    }

    public abstract void onWorldJoin();
    public abstract void onWorldSeparate();

    @Override
    public void invalidate() {
        onWorldSeparate();
        super.invalidate();
    }

    @Override
    public void validate() {
        onWorldJoin();
        super.validate();
    }

    @Override
    public void onChunkUnload() {
        onWorldSeparate();
        super.onChunkUnload();
    }

    public void onAdded() {
        onWorldJoin();
        refreshConnections();
    }

    @Override
    public void onLoad() {
        onWorldJoin();
        if (getPossibleTransmitterConnections() != transmitterConnections) {
            refreshConnections();
        }
        super.onLoad();
        Class test = TileEntityAbsWire.ConnectionType.class;
    }

    public void onNeighborTileChange(EnumFacing facing) {
        refreshConnections(facing);
    }

    public void onNeighborBlockChange(EnumFacing facing) {
        refreshConnections();
    }

    public ConnectionType getConnectionType(EnumFacing facing) {
        return getConnectionType(facing, getAllCurrentConnections(), transmitterConnections, connectionTypes);
    }

    public List<EnumFacing> getConnections(ConnectionType type) {
        List<EnumFacing> facings = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            if (getConnectionType(facing) == type) {
                facings.add(facing);
            }
        }
        return facings;
    }

    @Override
    public EnumActionResult onActiveWithShift(EntityPlayer player, EnumFacing facing) {
        if (!getWorld().isRemote) {
            RayTraceResult result = reTrace(getWorld(), getPos(), player);
            if (result == null) {
                return EnumActionResult.PASS;
            } else {
                EnumFacing facing1 = faceActive(result.subHit + 1);
                if (facing1 == null) {
                    if (connectionTypes[facing.ordinal()] != ConnectionType.NONE && onConfig(player, 6, facing) == EnumActionResult.SUCCESS) {
                        return EnumActionResult.SUCCESS;
                    }
                    facing1 = facing;
                }
                if (facing1 != null) {
                    connectionTypes[facing1.ordinal()] = connectionTypes[facing1.ordinal()].next();
                    sendDesc = true;
                    onModeChange(EnumFacing.values()[facing1.ordinal()]);
                    refreshConnections();
                    notifyTileChange();
                    player.sendMessage(new TextComponentString(I18n.format("conf.tileChange.switchMode") + "  " + I18n.format(connectionTypes[facing1.ordinal()].translationKey())));
                    return EnumActionResult.SUCCESS;
                } else {
                    return EnumActionResult.PASS;
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }

    private RayTraceResult reTrace(World world, BlockPos pos, EntityPlayer player) {
        Pair<Vec3d, Vec3d> vectors = Utils.getRayTraceVectors(player);
        Utils.AdvancedRayTraceResult result = Utils.collisionTrace(getPos(), vectors.getLeft(), vectors.getRight(), getCollisionBoxes());
        return result == null ? null : result.hit;
    }

    protected EnumFacing faceActive(int index) {
        List<EnumFacing> list = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            byte connections = getAllCurrentConnections();
            if (connectionMapContain(connections, facing)) {
                list.add(facing);
            }
        }
        if (index < list.size()) {
            return list.get(index);
        }
        return null;
    }

    protected EnumActionResult onConfig(EntityPlayer player, int part, EnumFacing facing) {
        return EnumActionResult.PASS;
    }

    public Colors getRenderColor() {
        return null;
    }

    @Override
    public EnumActionResult onActive(EntityPlayer player, EnumFacing facing) {
        if (!getWorld().isRemote && handlesRedstone()) {
            reactive ^= true;
            refreshConnections();
            notifyTileChange();
            player.sendMessage(new TextComponentString(Colors.ICE_BLUE.name + "RedStone sensor mode changed" + Colors.LTCAT_BLUE.name + (reactive ? "on." : "off.")));
        }
        return EnumActionResult.SUCCESS;
    }

    public List<String> getVisibleGroups() {

        List<String> visible = new ArrayList<>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            visible.add(facing.getName() + getConnectionType(facing).getName().toUpperCase());
        }
        return visible;
    }

    public IBlockState getExtendedState(IBlockState state) {
        PropertyConnection connectionProperty = new PropertyConnection(getAllCurrentConnections(), transmitterConnections, connectionTypes, renderCenter());
        return ((IExtendedBlockState)state).withProperty(OBJModel.OBJProperty.INSTANCE, new OBJModel.OBJState(getVisibleGroups(), true)).withProperty(PropertyConnection.INSTANCE, connectionProperty);
    }

    public void notifyTileChange() {
        Utils.notifyTileNeighborsChanged(getWorld(), new Coord4D(getPos(), getWorld()));
    }

    @Override
    public boolean canRenderBreaking() {
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilitiesRegistryHandler.CONFIGABLE || capability == CapabilitiesRegistryHandler.NETWORK_TILE || capability == CapabilitiesRegistryHandler.BLOCKABLE_CONNECT;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilitiesRegistryHandler.CONFIGABLE || capability == CapabilitiesRegistryHandler.NETWORK_TILE || capability == CapabilitiesRegistryHandler.BLOCKABLE_CONNECT) {
            return (T)this;
        }
        return super.getCapability(capability, facing);
    }

    public enum ConnectionType implements IStringSerializable
    {
        NORMAL,
        NONE,
        IN,
        OUT;

        public ConnectionType next() {
            if (ordinal() == values().length - 1) {
                return NORMAL;
            }
            return values()[ordinal() + 1];
        }

        @Override
        public String getName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public String translationKey() {
            return "quantumelectrix.transmitter.connectiontype." + getName();
        }
    }
}
