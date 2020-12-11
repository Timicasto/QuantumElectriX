package timicasto.quantumelectrix.item;

import net.minecraft.item.Item;
import timicasto.quantumelectrix.creative.TabLoader;

public class ItemBearing extends Item {
    public ItemBearing() {
        super();
        setRegistryName("bearing");
        setUnlocalizedName("bearing");
        setCreativeTab(TabLoader.elecTab);
    }
}
