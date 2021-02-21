package timicasto.quantumelectrix.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import timicasto.quantumelectrix.tile.TileEntityBatteryAdapter;

public class ContainerBatteryAdapter extends Container {
    private final TileEntityBatteryAdapter tile;

    public ContainerBatteryAdapter(InventoryPlayer player, TileEntity tile) {
        super();
        this.tile = (TileEntityBatteryAdapter) tile;
        IItemHandler inventory = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.addSlotToContainer(new SlotItemHandler(inventory, 0, 26, 11){
            @Override
            public boolean isItemValid(ItemStack stack) {
                TileEntityBatteryAdapter tileEntity = (TileEntityBatteryAdapter) tile;
                return ((TileEntityBatteryAdapter) tile).canInsertBattery(stack.getItem());
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 1, 26, 11){
            @Override
            public boolean isItemValid(ItemStack stack) {
                TileEntityBatteryAdapter tileEntity = (TileEntityBatteryAdapter) tile;
                return ((TileEntityBatteryAdapter) tile).canInsertBattery(stack.getItem());
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 2, 56, 11){
            @Override
            public boolean isItemValid(ItemStack stack) {
                TileEntityBatteryAdapter tileEntity = (TileEntityBatteryAdapter) tile;
                return ((TileEntityBatteryAdapter) tile).canInsertBattery(stack.getItem());
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 3, 86, 11){
            @Override
            public boolean isItemValid(ItemStack stack) {
                TileEntityBatteryAdapter tileEntity = (TileEntityBatteryAdapter) tile;
                return ((TileEntityBatteryAdapter) tile).canInsertBattery(stack.getItem());
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 4, 26, 50){
            @Override
            public boolean isItemValid(ItemStack stack) {
                TileEntityBatteryAdapter tileEntity = (TileEntityBatteryAdapter) tile;
                return ((TileEntityBatteryAdapter) tile).canInsertBattery(stack.getItem());
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 5, 56, 50){
            @Override
            public boolean isItemValid(ItemStack stack) {
                TileEntityBatteryAdapter tileEntity = (TileEntityBatteryAdapter) tile;
                return ((TileEntityBatteryAdapter) tile).canInsertBattery(stack.getItem());
            }
        });
        this.addSlotToContainer(new SlotItemHandler(inventory, 6, 86, 50){
            @Override
            public boolean isItemValid(ItemStack stack) {
                return stack.getItem().equals(Item.getItemFromBlock(Blocks.STONE)) || stack.getItem().equals(Item.getItemFromBlock(Blocks.COBBLESTONE));
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

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return ItemStack.EMPTY;
    }
}
