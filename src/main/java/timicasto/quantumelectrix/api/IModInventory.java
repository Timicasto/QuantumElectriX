package timicasto.quantumelectrix.api;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IModInventory {
    NonNullList<ItemStack> getInventory();
    boolean isStackValid(int slot, ItemStack stack);
    int getSlotLimit(int slot);
    void renderUpdate(int slot);
    default NonNullList<ItemStack> getDropItems() {
        return getInventory();
    }
}
