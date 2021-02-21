package timicasto.quantumelectrix;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.api.MultiblockHandler;
import timicasto.quantumelectrix.block.*;
import timicasto.quantumelectrix.block.multiblocks.BlockInstanceMultiBlock;
import timicasto.quantumelectrix.block.multiblocks.BlockMultiBlocks;
import timicasto.quantumelectrix.block.multiblocks.MultiBlockContactChamber;
import timicasto.quantumelectrix.block.types.BlockTypesMultiBlock;
import timicasto.quantumelectrix.fluid.FluidBlockBase;
import timicasto.quantumelectrix.fluid.FluidLoader;
import timicasto.quantumelectrix.item.*;
import timicasto.quantumelectrix.tile.TileEntityBatteryAdapter;
import timicasto.quantumelectrix.tile.TileEntityBurningChamber;
import timicasto.quantumelectrix.tile.TileEntityContactChamber;
import timicasto.quantumelectrix.tile.TileEntityLargeAssemblyTable;

@Mod.EventBusSubscriber
public class RegistryHandler {
    private static Logger logger = LogManager.getLogger();
    public static Block THERMAL_GENERATOR = new BlockThermalGenerator();
    public static Item THERMAL_GENERATOR_ITEM = new ItemBlock(THERMAL_GENERATOR);
    public static Block AIR_WAY = new BlockAirWay();
    public static Item AIR_WAY_ITEM = new ItemBlock(AIR_WAY);
    public static Block BURNING_CHAMBER = new BlockBurningChamber();
    public static Item BURNING_CHAMBER_ITEM = new ItemBlock(BURNING_CHAMBER);
    public static Block COIL = new BlockCoil();
    public static Item COIL_ITEM = new ItemBlock(COIL);
    public static Block PERMANENT_MAGNET = new BlockPermanentMagnet();
    public static Item PERMANENT_MAGNET_ITEM = new ItemBlock(PERMANENT_MAGNET);
    public static Block TURBINE_BLADE = new BlockTurbineBlade();
    public static Item TURBINE_BLADE_ITEM = new ItemBlock(TURBINE_BLADE);
    public static Block TURBINE_HOUSE = new BlockTurbineHouse();
    public static Item TURBINE_HOUSE_ITEM = new ItemBlock(TURBINE_HOUSE);
    public static Item BEARING = new ItemBearing();
    public static Block AZURITE = new BlockAzurite();
    public static Item AZURITE_ITEM = new ItemBlock(AZURITE);
    public static Block BORNITE = new BlockBornite();
    public static Item BORNITE_ITEM = new ItemBlock(BORNITE);
    public static Block CHALCOCITE = new BlockChalcocite();
    public static Item CHALCOCITE_ITEM = new ItemBlock(CHALCOCITE);
    public static Item COPPER_INGOT = new ItemIngotCopper();
    public static Block SPHALERITE = new BlockSphalerite();
    public static Item SPHALERITE_ITEM_BLOCK = new ItemBlock(SPHALERITE);
    public static Block LARGE_ASSEMBLY_TABLE = new BlockLargeAssemblyTable();
    public static Item LARGE_ASSEMBLY_TABLE_ITEM_BLOCK = new ItemBlock(LARGE_ASSEMBLY_TABLE);
    public static Block PLATINUM_ORE = new BlockPlatinumOre();
    public static Item PLATINUM_ORE_ITEM_BLOCK = new ItemBlock(PLATINUM_ORE);
    public static Item PLATINUM_INGOT = new ItemPlatinumIngot();
    public static Block PYRITES = new BlockPyrites();
    public static Item PYRITES_ITEM = new ItemBlock(PYRITES);
    public static Block SO2 = new FluidBlockBase(FluidLoader.chemistrySO2);
    public static BlockMultiblock<BlockTypesMultiBlock> BLOCK_MULTI_BLOCK = new BlockInstanceMultiBlock();
    public static Item TOOL_BOX = new ItemToolBox();
    public static Item VOLTA_BATTERY = new ItemVoltaBattery(237800, 5);
    public static Block BATTERY_ADAPTER = new BlockBatteryAdapter();
    public static Item BATTERY_ADAPTER_ITEM = new ItemBlock(BATTERY_ADAPTER);

    @SubscribeEvent
    public static void registerBlock(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(THERMAL_GENERATOR);
        GameRegistry.registerTileEntity(TileEntityLargeAssemblyTable.class, "large_assembly_table");
        GameRegistry.registerTileEntity(TileEntityBurningChamber.class, "burning_chamber");
        GameRegistry.registerTileEntity(TileEntityContactChamber.class, "contact_chamber");
        event.getRegistry().registerAll(
                AIR_WAY,
                BURNING_CHAMBER,
                COIL,
                PERMANENT_MAGNET,
                TURBINE_BLADE,
                TURBINE_HOUSE,
                AZURITE,
                BORNITE,
                CHALCOCITE,
                SPHALERITE,
                LARGE_ASSEMBLY_TABLE,
                PLATINUM_ORE,
                PYRITES,
                SO2.setRegistryName("so2").setUnlocalizedName("so2"),
                BLOCK_MULTI_BLOCK,
                BATTERY_ADAPTER
        );
        GameRegistry.registerTileEntity(TileEntityBatteryAdapter.class, "battery_adapter");
    }

    @SubscribeEvent
    public static void registerItem(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(THERMAL_GENERATOR_ITEM.setRegistryName("thermal_generator"));
        event.getRegistry().registerAll(
                AIR_WAY_ITEM.setRegistryName("air_way"),
                BURNING_CHAMBER_ITEM.setRegistryName("burning_chamber"),
                COIL_ITEM.setRegistryName("coil"),
                PERMANENT_MAGNET_ITEM.setRegistryName("permanent_magnet"),
                TURBINE_BLADE_ITEM.setRegistryName("turbine_blade"),
                TURBINE_HOUSE_ITEM.setRegistryName("turbine_house"),
                BEARING,
                AZURITE_ITEM.setRegistryName("azurite"),
                BORNITE_ITEM.setRegistryName("bornite"),
                CHALCOCITE_ITEM.setRegistryName("chalcocite"),
                COPPER_INGOT,
                SPHALERITE_ITEM_BLOCK.setRegistryName("sphalerite"),
                LARGE_ASSEMBLY_TABLE_ITEM_BLOCK.setRegistryName("large_assembly_table"),
                PLATINUM_ORE_ITEM_BLOCK.setRegistryName("platinum_ore"),
                PLATINUM_INGOT,
                PYRITES_ITEM.setRegistryName("pyrites"),
                TOOL_BOX,
                VOLTA_BATTERY,
                BATTERY_ADAPTER_ITEM.setRegistryName("battery_adapter")
        );
    }

    public static void init() {
        MultiblockHandler.registerMultiBlock(MultiBlockContactChamber.instance);
    }
}
