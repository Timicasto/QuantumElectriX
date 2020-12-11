package timicasto.quantumelectrix.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.tile.TileEntityBurningChamber;

public class ContainerSulfurSmeltFurnace extends Container {
    private final TileEntityBurningChamber tile;
    private int heat, fuel;

    public ContainerSulfurSmeltFurnace(InventoryPlayer player, TileEntity tileEntity) {
        super();
        this.tile = (TileEntityBurningChamber) tileEntity;
        IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        this.addSlotToContainer(new SlotItemHandler(inventory, 1, 26, 11){
            @Override
            public int getSlotStackLimit() {
                return 18;
            }

            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == RegistryHandler.PYRITES_ITEM;
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 26, 59){
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem() == Items.COAL;
            }
        });
        for (int i = 0 ; i < 9 ; i++) {
            this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 142));
        }

        for (int i = 0 ; i < 3 ; i++) {
            for (int k = 0 ; k < 9 ; k++) {
                this.addSlotToContainer(new Slot(player, k + i * 9 + 9, 8 + k * 18, 84 + i * 18));
            }
        }

    }
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
