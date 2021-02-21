package timicasto.quantumelectrix.api;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import timicasto.quantumbase.utils.MetalSmelterRecipe;
import timicasto.quantumelectrix.RegistryHandler;

public class CraftHandler {

    public CraftHandler() {
        MetalSmelterRecipe.instance().createRecipe(new ItemStack(RegistryHandler.AZURITE_ITEM), new ItemStack(RegistryHandler.AZURITE_ITEM), 9600, 220, true, new ItemStack(RegistryHandler.COPPER_INGOT), 4800);
        MetalSmelterRecipe.instance().createRecipe(new ItemStack(RegistryHandler.BORNITE_ITEM), new ItemStack(RegistryHandler.BORNITE_ITEM), 7200, 400, true, new ItemStack(RegistryHandler.COPPER_INGOT), 3600);
        MetalSmelterRecipe.instance().createRecipe(new ItemStack(RegistryHandler.CHALCOCITE_ITEM), new ItemStack(RegistryHandler.CHALCOCITE_ITEM), 8000, 1115, true, new ItemStack(RegistryHandler.COPPER_INGOT), 4000);
        MetalSmelterRecipe.instance().createRecipe(new ItemStack(RegistryHandler.PLATINUM_ORE_ITEM_BLOCK), new ItemStack(RegistryHandler.PLATINUM_INGOT), 6000, 1768, false, ItemStack.EMPTY, 3000);
    }



}
