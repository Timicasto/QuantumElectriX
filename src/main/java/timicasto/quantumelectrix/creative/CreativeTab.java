package timicasto.quantumelectrix.creative;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import timicasto.quantumelectrix.RegistryHandler;

public class CreativeTab extends CreativeTabs {
    public CreativeTab() {
        super("quantumelectrix");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(RegistryHandler.THERMAL_GENERATOR_ITEM);
    }
}
