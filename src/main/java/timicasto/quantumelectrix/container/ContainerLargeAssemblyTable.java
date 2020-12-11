package timicasto.quantumelectrix.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import timicasto.quantumelectrix.tile.TileEntityLargeAssemblyTable;

public class ContainerLargeAssemblyTable extends Container {
    private final TileEntityLargeAssemblyTable tile;


    public ContainerLargeAssemblyTable(InventoryPlayer player, TileEntityLargeAssemblyTable tile) {
        this.tile = tile;
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        for (int i = 0 ; i < 3 ; i++) {
            for (int k = 0; k < 4; k++) {
                this.addSlotToContainer(new SlotItemHandler(inventory, k + 4 * i, 8 + 18 * k, 8 + i* 18));
            }
        }

        for (int i = 0 ; i < 3 ; i++) {
            for (int k = 0; k < 4; k++) {
                this.addSlotToContainer(new SlotItemHandler(inventory, 12 + k + 4 * i, 8 + 18 * k + 72, 8 + i* 18));
            }
        }

        for (int i = 0 ; i < 3 ; i++) {
            for (int k = 0 ; k < 4 ; k++) {
                this.addSlotToContainer(new SlotItemHandler(inventory, 24 + k + 4 * i, 8 + 18 * k + 72, 64 + i * 18));
            }
        }

        for (int i = 0 ; i < 3 ; i++) {
            for (int k = 0; k < 3; k++) {
                this.addSlotToContainer(new SlotItemHandler(inventory, 36 + k + 4 * i, 8 + 18 * k, 64 + i* 18));
            }
        }

        for (int i = 0 ; i < 5 ; i++) {
            this.addSlotToContainer(new SlotItemHandler(inventory, 45 + i, 156, 8 + i * 18));
        }

    }


    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }
}
