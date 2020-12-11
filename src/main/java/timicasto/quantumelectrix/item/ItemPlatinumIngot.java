package timicasto.quantumelectrix.item;

import net.minecraft.item.Item;
import timicasto.quantumelectrix.creative.TabLoader;

public class ItemPlatinumIngot extends Item {
    public ItemPlatinumIngot() {
        super();
        setRegistryName("platinum_ingot");
        setUnlocalizedName("platinum_ingot");
        setCreativeTab(TabLoader.elecTab);
    }
}
