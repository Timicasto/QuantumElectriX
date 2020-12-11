package timicasto.quantumelectrix.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemLeadBattery extends ItemEnergy {
    public ItemLeadBattery(String name, int capacity, int transfer, int currentPower) {
        super(currentPower, capacity, transfer, name);
        setMaxStackSize(1);
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        setEnergy(new ItemStack(this), 1600);
    }
}
