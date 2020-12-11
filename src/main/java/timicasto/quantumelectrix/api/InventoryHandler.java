package timicasto.quantumelectrix.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventoryHandler implements IItemHandlerModifiable {
    int slot;
    IModInventory inventory;
    int slotOffset;
    boolean[] canIn;
    boolean[] canOut;

    public InventoryHandler(int slots, IModInventory inventory)
    {
        this(slots,inventory,0, new boolean[slots], new boolean[slots]);
        for(int i=0; i<slots; i++)
            this.canOut[i] = this.canIn[i] = true;
    }

    public InventoryHandler(int slots, IModInventory inventory, int slotOffset, boolean canIn, boolean canOut) {
        this(slots, inventory, slotOffset, new boolean[slots], new boolean[slots]);
        for (int i = 0 ; i < slots ; i++) {
            this.canIn[i] = canIn;
            this.canOut[i] = canOut;
        }
    }

    public InventoryHandler(int slots, IModInventory inventory, int slotOffset, boolean canIn[], boolean canOut[])
    {
        this.slot = slots;
        this.inventory = inventory;
        this.slotOffset = slotOffset;
        this.canIn = canIn;
        this.canOut = canOut;
    }

    public int getSlot() {
        return slot;
    }

    @Override
    public int getSlots() {
        return slot;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.inventory.getInventory().get(this.slotOffset + slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (!canIn[slot] || stack.isEmpty()) {
            return stack;
        }
        stack = stack.copy();

        if (!inventory.isStackValid(this.slotOffset + slot, stack)) {
            return stack;
        }
        int offset = this.slotOffset + slot;
        ItemStack stack1 = inventory.getInventory().get(offset);

        if (stack1.isEmpty()) {
            int accepted = Math.min(stack.getMaxStackSize(), inventory.getSlotLimit(offset));
            if (accepted < stack.getCount()) {
                if (!simulate) {
                    inventory.getInventory().set(offset, stack.splitStack(accepted));
                    return stack;
                } else {
                    stack.shrink(accepted);
                    return stack;
                }
            } else {
                if (!simulate) {
                    inventory.getInventory().set(offset, stack);
                }
                return ItemStack.EMPTY;
            }
        } else {
            if (!ItemHandlerHelper.canItemStacksStack(stack, stack1)) {
                return stack;
            }

            int accepted = Math.min(stack.getMaxStackSize(), inventory.getSlotLimit(offset) - stack1.getCount());
            if (accepted < stack.getCount()) {
                if (!simulate) {
                    ItemStack result = stack.splitStack(accepted);
                    result.grow(stack1.getCount());
                    inventory.getInventory().set(offset, result);
                    return stack;
                } else {
                    stack.shrink(accepted);
                    return stack;
                }
            } else {
                if (!simulate) {
                    ItemStack result = stack.copy();
                    result.grow(stack1.getCount());
                    inventory.getInventory().set(offset, stack1);
                }
                return ItemStack.EMPTY;
            }
        }
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!canOut[slot] || amount == 0) {
            return ItemStack.EMPTY;
        }
        int offset = this.slotOffset + slot;
        ItemStack stack = inventory.getInventory().get(offset);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        int extracted = Math.min(stack.getCount(), amount);
        ItemStack _return = stack.copy();
        _return.setCount(extracted);
        if (!simulate) {
            if (extracted < stack.getCount()) {
                stack.shrink(extracted);
            } else {
                stack = ItemStack.EMPTY;
            }
            inventory.getInventory().set(offset, stack);
        }
        return _return;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        inventory.getInventory().set(this.slotOffset + slot, stack);
    }
}









