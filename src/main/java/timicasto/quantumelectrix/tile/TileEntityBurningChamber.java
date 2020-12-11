package timicasto.quantumelectrix.tile;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.fluid.FluidLoader;

import java.util.Random;

public class TileEntityBurningChamber extends TileEntity implements ITickable {
    private int fuel;
    private int heat;
    private final ItemStackHandler inventory = new ItemStackHandler(2);
    private final FluidTank chemistrySO2 = new FluidTank(16000);
    private final Logger logger = LogManager.getLogger();
    private boolean canUse = false;
    private boolean isWorking;
    private final Random random = new Random();

    public static final TileEntityBurningChamber instance = new TileEntityBurningChamber();

    public FluidTank getSO2() {
        return instance.chemistrySO2;
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (heat == 725) {
                inventory.extractItem(1, 1, false);
                chemistrySO2.fill(new FluidStack(FluidLoader.chemistrySO2, random.nextInt(800) + 400), true);
                heat = 0;
            }
            if (fuel > 0 && isWorking) {
                ++heat;
                --fuel;
            }
            if (fuel == 0) {
                if (inventory.getStackInSlot(0).getItem() == Items.COAL) {
                    fuel = fuel + 1600;
                    inventory.extractItem(0, 1, false);
                }
                if (inventory.getStackInSlot(0).isEmpty()) {
                    isWorking = false;
                }
            }
            logger.info("current heat : " + heat);
            logger.info("isWorking : " + isWorking);
            logger.info("canUse : " + isCanUse());
            logger.info("current fuel : " + fuel);
            logger.info("current SO2 : " + chemistrySO2.getFluidAmount());
        }
    }

    public void makeWork() {
        isWorking = true;
        logger.info("made work.");
    }

    public void formBlock() {
        canUse = true;
    }

    public boolean isStructureValid(int x, int y, int z) {
        if (world.getBlockState(new BlockPos(x, y, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y, z)).getBlock() == Blocks.BRICK_BLOCK) {
            if (world.getBlockState(new BlockPos(x + 1, y + 1, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 1, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 1, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 1, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 1, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 1, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 1, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 1, z + 1)).getBlock() == Blocks.BRICK_BLOCK) {
                if (world.getBlockState(new BlockPos(x + 2, y + 2, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 2, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 2, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 2, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 2, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 2, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 2, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 2, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 2, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 2, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 2, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 2, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 2, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 2, z + 2)).getBlock() == Blocks.BRICK_BLOCK) {
                    if (world.getBlockState(new BlockPos(x + 2, y + 3, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 3, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 3, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 3, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 3, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 3, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 3, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 3, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 3, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 3, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 3, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 3, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 3, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 3, z + 2)).getBlock() == Blocks.BRICK_BLOCK) {
                        if (world.getBlockState(new BlockPos(x + 2, y + 4, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 4, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 4, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 4, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 4, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 4, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 4, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 4, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 4, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 4, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 4, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 4, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 4, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 4, z + 2)).getBlock() == Blocks.BRICK_BLOCK) {
                            if (world.getBlockState(new BlockPos(x + 2, y + 5, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 5, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 2, y + 5, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 5, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x + 1, y + 5, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 5, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x, y + 5, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 5, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 1, y + 5, z + 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 5, z - 2)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 5, z - 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 5, z)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 5, z + 1)).getBlock() == Blocks.BRICK_BLOCK && world.getBlockState(new BlockPos(x - 2, y + 5, z + 2)).getBlock() == Blocks.BRICK_BLOCK) {
                                if (world.getBlockState(new BlockPos(x + 1, y + 5, z - 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x + 1, y + 5, z)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x + 1, y + 5, z + 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x, y + 5, z - 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x, y + 5, z + 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x - 1, y + 5, z - 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x - 1, y + 5, z - 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x - 1, y + 5, z)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x - 1, y + 5, z + 1)).getBlock() == RegistryHandler.AIR_WAY && world.getBlockState(new BlockPos(x, y + 5, z)).getBlock() == Blocks.TRAPDOOR) {
                                    logger.info("Valid Structure!");
                                    canUse = true;
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public boolean isCanUse() {
        return canUse;
    }


    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(compound);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) this.inventory;
        }
        return super.getCapability(capability, facing);
    }
}
