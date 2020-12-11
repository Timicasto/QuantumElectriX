package timicasto.quantumelectrix.item;

import net.minecraft.item.Item;
import timicasto.quantumelectrix.creative.TabLoader;

public class ItemIngotCopper extends Item {
    public ItemIngotCopper() {
        super();
        setRegistryName("copper_ingot");
        setUnlocalizedName("copper_ingot");
        setCreativeTab(TabLoader.elecTab);
    }
}
