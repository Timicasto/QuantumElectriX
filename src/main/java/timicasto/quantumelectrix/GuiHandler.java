package timicasto.quantumelectrix;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import timicasto.quantumelectrix.container.ContainerBatteryAdapter;
import timicasto.quantumelectrix.container.ContainerLargeAssemblyTable;
import timicasto.quantumelectrix.container.ContainerSulfurSmeltFurnace;
import timicasto.quantumelectrix.container.ContainerThermalGenerator;
import timicasto.quantumelectrix.gui.GuiContainerBatteryAdapter;
import timicasto.quantumelectrix.gui.GuiContainerLargeAssemblyTable;
import timicasto.quantumelectrix.gui.GuiContainerSulfurSmeltFurnace;
import timicasto.quantumelectrix.gui.GuiContainerThermalGenerator;
import timicasto.quantumelectrix.tile.TileEntityBatteryAdapter;
import timicasto.quantumelectrix.tile.TileEntityBurningChamber;
import timicasto.quantumelectrix.tile.TileEntityLargeAssemblyTable;
import timicasto.quantumelectrix.tile.TileEntityThermalGenerator;

public class GuiHandler implements IGuiHandler {
    // ID管理
    public static final int GUI_THERMAL_GENERATOR = 1;
    public static final int GUI_LARGE_ASSEMBLY_TABLE = 2;
    public static final int GUI_SULFUR_SMELT_FURNACE = 3;
    public static final int GUI_CONTACT_CHAMBER = 4;
    public static final int GUI_BATTERY_ADAPTER = 5;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_THERMAL_GENERATOR:
                return new ContainerThermalGenerator(player.inventory, (TileEntityThermalGenerator)world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_LARGE_ASSEMBLY_TABLE:
                return new ContainerLargeAssemblyTable(player.inventory, (TileEntityLargeAssemblyTable)world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_SULFUR_SMELT_FURNACE:
                return new ContainerSulfurSmeltFurnace(player.inventory, (TileEntityBurningChamber)world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_BATTERY_ADAPTER:
                return new ContainerBatteryAdapter(player.inventory, (TileEntityBatteryAdapter)world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case GUI_THERMAL_GENERATOR:
                return new GuiContainerThermalGenerator(player.inventory, (TileEntityThermalGenerator)world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_LARGE_ASSEMBLY_TABLE:
                return new GuiContainerLargeAssemblyTable(player.inventory, (TileEntityLargeAssemblyTable)world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_SULFUR_SMELT_FURNACE:
                return new GuiContainerSulfurSmeltFurnace(player.inventory, (TileEntityBurningChamber)world.getTileEntity(new BlockPos(x, y, z)));
            case GUI_BATTERY_ADAPTER:
                return new GuiContainerBatteryAdapter(player.inventory, (TileEntityBatteryAdapter)world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }
}
