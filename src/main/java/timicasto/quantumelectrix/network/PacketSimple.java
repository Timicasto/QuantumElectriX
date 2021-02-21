package timicasto.quantumelectrix.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.api.Utils;
import timicasto.quantumelectrix.network.wrapper.BlockVec3;
import timicasto.quantumelectrix.network.wrapper.Footprint;
import timicasto.quantumelectrix.network.wrapper.Vector3B;

import java.io.IOException;
import java.util.*;

public class PacketSimple extends PacketBase implements Packet<INetHandler> {
    public enum EnumPacket {
        // CommonSide
        S_RESPAWN_PLAYER(Side.SERVER, String.class),
        S_TELEPORT_ENTITY(Side.SERVER, Integer.class),
        S_IGNITE_ROCKET(Side.SERVER),
        S_OPEN_SCHEMATIC_PAGE(Side.SERVER, Integer.class, Integer.class, Integer.class, Integer.class),
        S_OPEN_FUEL_GUI(Side.SERVER, String.class),
        S_UPDATE_SHIP_YAW(Side.SERVER, Float.class),
        S_UPDATE_SHIP_PITCH(Side.SERVER, Float.class),
        S_SET_ENTITY_FIRE(Side.SERVER, Integer.class),
        S_BIND_SPACE_STATION_ID(Side.SERVER, Integer.class),
        S_UNLOCK_NEW_SCHEMATIC(Side.SERVER),
        S_UPDATE_DISABLEABLE_BUTTON(Side.SERVER, BlockPos.class, Integer.class),
        S_ON_FAILED_CHEST_UNLOCK(Side.SERVER, Integer.class),
        S_RENAME_SPACE_STATION(Side.SERVER, String.class, Integer.class),
        S_OPEN_EXTENDED_INVENTORY(Side.SERVER),
        S_ON_ADVANCED_GUI_CLICKED_INT(Side.SERVER, Integer.class, BlockPos.class, Integer.class),
        S_ON_ADVANCED_GUI_CLICKED_STRING(Side.SERVER, Integer.class, BlockPos.class, String.class),
        S_UPDATE_SHIP_MOTION_Y(Side.SERVER, Integer.class, Boolean.class),
        S_START_NEW_SPACE_RACE(Side.SERVER, Integer.class, String.class, FlagData.class, Vector3B.class, String[].class),
        S_REQUEST_FLAG_DATA(Side.SERVER, String.class),
        S_INVITE_RACE_PLAYER(Side.SERVER, String.class, Integer.class),
        S_REMOVE_RACE_PLAYER(Side.SERVER, String.class, Integer.class),
        S_ADD_RACE_PLAYER(Side.SERVER, String.class, Integer.class),
        S_COMPLETE_CBODY_HANDSHAKE(Side.SERVER, String.class),
        S_REQUEST_GEAR_DATA1(Side.SERVER, UUID.class),
        S_REQUEST_GEAR_DATA2(Side.SERVER, UUID.class),
        S_REQUEST_OVERWORLD_IMAGE(Side.SERVER),
        S_REQUEST_MAP_IMAGE(Side.SERVER, Integer.class, Integer.class, Integer.class),
        S_REQUEST_PLAYERSKIN(Side.SERVER, String.class),
        S_BUILDFLAGS_UPDATE(Side.SERVER, Integer.class),
        S_CONTROL_ENTITY(Side.SERVER, Integer.class),
        S_NOCLIP_PLAYER(Side.SERVER, Boolean.class),
        S_REQUEST_DATA(Side.SERVER, Integer.class, BlockPos.class),
        S_UPDATE_CHECKLIST(Side.SERVER, NBTTagCompound.class),
        S_REQUEST_MACHINE_DATA(Side.SERVER, BlockPos.class),
        S_REQUEST_CONTAINER_SLOT_REFRESH(Side.SERVER, Integer.class),
        // ClientSide
        C_AIR_REMAINING(Side.CLIENT, Integer.class, Integer.class, String.class),
        C_UPDATE_DIMENSION_LIST(Side.CLIENT, String.class, String.class, Boolean.class),
        C_SPAWN_SPARK_PARTICLES(Side.CLIENT, BlockPos.class),
        C_UPDATE_GEAR_SLOT(Side.CLIENT, String.class, Integer.class, Integer.class, Integer.class),
        C_CLOSE_GUI(Side.CLIENT),
        C_RESET_THIRD_PERSON(Side.CLIENT),
        C_UPDATE_SPACESTATION_LIST(Side.CLIENT, Integer[].class),
        C_UPDATE_SPACESTATION_DATA(Side.CLIENT, Integer.class, NBTTagCompound.class),
        C_UPDATE_SPACESTATION_CLIENT_ID(Side.CLIENT, String.class),
        C_UPDATE_PLANETS_LIST(Side.CLIENT, Integer[].class),
        C_UPDATE_CONFIGS(Side.CLIENT, Integer.class, Double.class, Integer.class, Integer.class, Integer.class, String.class, Float.class, Float.class, Float.class, Float.class, Integer.class, String[].class),
        C_UPDATE_STATS(Side.CLIENT, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, String.class, Integer.class, Integer.class), //Note: Integer, PANELTYPES_LENGTH * <String, Integer>, Integer - see StatsCapability.getMiscNetworkedStats()
        C_ADD_NEW_SCHEMATIC(Side.CLIENT, Integer.class),
        C_UPDATE_SCHEMATIC_LIST(Side.CLIENT, Integer[].class),
        C_PLAY_SOUND_BOSS_DEATH(Side.CLIENT, Float.class),
        C_PLAY_SOUND_EXPLODE(Side.CLIENT),
        C_PLAY_SOUND_BOSS_LAUGH(Side.CLIENT),
        C_PLAY_SOUND_BOW(Side.CLIENT),
        C_UPDATE_OXYGEN_VALIDITY(Side.CLIENT, Boolean.class),
        C_OPEN_PARACHEST_GUI(Side.CLIENT, Integer.class, Integer.class, Integer.class),
        C_UPDATE_WIRE_BOUNDS(Side.CLIENT, BlockPos.class),
        C_OPEN_SPACE_RACE_GUI(Side.CLIENT),
        C_UPDATE_SPACE_RACE_DATA(Side.CLIENT, Integer.class, String.class, FlagData.class, Vector3B.class, String[].class),
        C_OPEN_JOIN_RACE_GUI(Side.CLIENT, Integer.class),
        C_UPDATE_FOOTPRINT_LIST(Side.CLIENT, Long.class, Footprint[].class),
        C_UPDATE_DUNGEON_DIRECTION(Side.CLIENT, Float.class),
        C_FOOTPRINTS_REMOVED(Side.CLIENT, Long.class, BlockVec3.class),
        C_UPDATE_STATION_SPIN(Side.CLIENT, Float.class, Boolean.class),
        C_UPDATE_STATION_DATA(Side.CLIENT, Double.class, Double.class),
        C_UPDATE_STATION_BOX(Side.CLIENT, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class),
        C_UPDATE_THERMAL_LEVEL(Side.CLIENT, Integer.class, Boolean.class),
        C_DISPLAY_ROCKET_CONTROLS(Side.CLIENT),
        C_GET_CELESTIAL_BODY_LIST(Side.CLIENT),
        C_UPDATE_ENERGYUNITS(Side.CLIENT, Integer.class),
        C_RESPAWN_PLAYER(Side.CLIENT, String.class, Integer.class, String.class, Integer.class),
        C_UPDATE_TELEMETRY(Side.CLIENT, BlockPos.class, String.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, String.class),
        C_SEND_PLAYERSKIN(Side.CLIENT, String.class, String.class, String.class, String.class),
        C_SEND_OVERWORLD_IMAGE(Side.CLIENT, Integer.class, Integer.class, byte[].class),
        C_RECOLOR_PIPE(Side.CLIENT, BlockPos.class),
        C_RECOLOR_ALL_GLASS(Side.CLIENT, Integer.class, Integer.class, Integer.class),  //Number of integers to match number of different blocks of PLAIN glass individually instanced and registered in GCBlocks
        C_UPDATE_MACHINE_DATA(Side.CLIENT, BlockPos.class, Integer.class, Integer.class, Integer.class, Integer.class),
        C_SPAWN_HANGING_SCHEMATIC(Side.CLIENT, BlockPos.class, Integer.class, Integer.class, Integer.class),
        C_LEAK_DATA(Side.CLIENT, BlockPos.class, Integer[].class);

        private Side targetSide;
        private Class<?>[] decodeAs;

        EnumPacket(Side targetSide, Class<?>... decodeAs)
        {
            this.targetSide = targetSide;
            this.decodeAs = decodeAs;
        }

        public Side getTargetSide()
        {
            return this.targetSide;
        }

        public Class<?>[] getDecodeClasses()
        {
            return this.decodeAs;
        }
    }

    private EnumPacket type;
    private List<Object> data;
    private static String spam;
    private static Map<EntityPlayerMP, GameType> savedSettings = new HashMap<>();

    public PacketSimple() {
        super();
    }

    public PacketSimple(EnumPacket type, int dim, Object[] data) {
        this(type, dim, data);
    }

    public PacketSimple(EnumPacket type, World world, Object[] data) {
        this(type, world.provider.getDimension(), data);
    }

    public PacketSimple(EnumPacket type, int dim, List<Object> data) {
        super(dim);
        if (type.getDecodeClasses().length != data.size()) {
            System.out.println("Simple Packet Frame Discovered the Data Length Different than Packet Type");
            new RuntimeException().printStackTrace();
        }
        this.type = type;
        this.data = data;
    }

    @Override
    public void encodeInto(ByteBuf buf) {
        super.encodeInto(buf);
        buf.writeInt(this.type.ordinal());
        try {
            Utils.encodeData_ObjectCollection(buf, this.data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void decodeInto(ByteBuf buf) {
        super.decodeInto(buf);
        this.type = EnumPacket.values()[buf.readInt()];

        try {
            if (this.type.getDecodeClasses().length > 0) {
                this.data = Utils.decodeData(this.type.getDecodeClasses(), buf);
            }
            if (buf.readableBytes() > 0 && buf.writerIndex() < 0xFFF00) {
                System.out.println("An error occurred during solving the length of packet type");
            }
        } catch (Exception e) {
            System.err.println("Error occurred during handling simple packet type " + this.type.toString() + " " + buf.toString());
            e.printStackTrace();
            throw e;
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleClientSide(EntityPlayer player) {
        EntityPlayerSP playerClient = null;
        if (player instanceof EntityPlayerSP) {
            playerClient = (EntityPlayerSP)player;
        }
    }
}
