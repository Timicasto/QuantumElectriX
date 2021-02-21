package timicasto.quantumelectrix.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import timicasto.quantumelectrix.api.ResourceTools;
import timicasto.quantumelectrix.creative.TabLoader;
import timicasto.quantumelectrix.energy.CustomEnergyStorage;

import javax.annotation.Nullable;
import java.text.NumberFormat;
import java.util.List;

public class ItemVoltaBattery extends Item {
    private final int maxStorage;
    private final int maxTransfer;
    private final int energy = 237800;
    public ItemVoltaBattery(int maxStorage, int maxTransfer) {
        super();
        setRegistryName("volta_battery");
        setUnlocalizedName("volta_battery");
        setCreativeTab(TabLoader.elecTab);
        this.maxStorage = maxStorage;
        this.maxTransfer = maxTransfer;
        setHasSubtypes(false);
        setMaxStackSize(1);
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null) {
                NumberFormat format = NumberFormat.getInstance();
                tooltip.add(String.format("%s/%s %s", format.format(storage.getEnergyStored()), format.format(storage.getMaxEnergyStored()), I18n.format("electrix.battery.volta")));
            }
        }
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return false;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null) {
                double maxStorage = storage.getMaxEnergyStored();
                double energyDifference = maxStorage - storage.getEnergyStored();
                return energyDifference / maxStorage;
            }
        }
        return super.getDurabilityForDisplay(stack);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && player.world != null) {
            float[] color = ResourceTools.getColorArray(player.world.getTotalWorldTime() % 256);
            return MathHelper.rgb(color[0] / 255, color[1] / 255, color[2] / 255);
        }
        return super.getRGBDurabilityForDisplay(stack);
    }

    public void setEnergy(ItemStack stack, int energy) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage instanceof CustomEnergyStorage) {
                ((CustomEnergyStorage) storage).setEnergyStored(energy);
            }
        }
    }

    public int extractEnergy(ItemStack stack, int maxTransfer, boolean simulate) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage instanceof CustomEnergyStorage) {
                ((CustomEnergyStorage)storage).extractEnergy(maxTransfer, simulate);
            }
        }
        return 0;
    }

    public int getEnergyStored(ItemStack stack) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null) {
                return storage.getEnergyStored();
            }
        }
        return 0;
    }

    public int getMaxStorage(ItemStack stack) {
        if (stack.hasCapability(CapabilityEnergy.ENERGY, null)) {
            IEnergyStorage storage = stack.getCapability(CapabilityEnergy.ENERGY, null);
            if (storage != null) {
                return storage.getMaxEnergyStored();
            }
        }
        return 0;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
        return new EnergyCapabilityProvider(stack, this);
    }

    private static class EnergyCapabilityProvider implements ICapabilityProvider {
        public final CustomEnergyStorage storage;

        public EnergyCapabilityProvider(final ItemStack stack, ItemVoltaBattery item) {
            this.storage = new CustomEnergyStorage(item.maxStorage, item.maxTransfer, item.maxTransfer, 237800) {
                @Override
                public int getEnergyStored() {
                    if (stack.hasTagCompound()) {
                        return stack.getTagCompound().getInteger("Energy");
                    } else {
                        return 0;
                    }
                }

                @Override
                public void setEnergyStored(int energy) {
                    if (!stack.hasTagCompound()) {
                        stack.setTagCompound(new NBTTagCompound());
                    }
                    stack.getTagCompound().setInteger("Energy", energy);
                }
            };
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return this.getCapability(capability, facing) != null;
        }

        @Nullable
        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if (capability == CapabilityEnergy.ENERGY) {
                return CapabilityEnergy.ENERGY.cast(this.storage);
            }
            return null;
        }
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
        setEnergy(stack, 237800);
    }
}














