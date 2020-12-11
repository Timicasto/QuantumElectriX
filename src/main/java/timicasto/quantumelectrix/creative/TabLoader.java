package timicasto.quantumelectrix.creative;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TabLoader {
    public static CreativeTabs elecTab;
    public TabLoader(FMLPreInitializationEvent event) {
        elecTab = new CreativeTab();
    }
}
