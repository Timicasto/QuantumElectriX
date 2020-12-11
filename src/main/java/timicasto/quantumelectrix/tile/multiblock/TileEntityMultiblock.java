package timicasto.quantumelectrix.tile.multiblock;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;
import timicasto.quantumelectrix.api.IDirectionalTile;
import timicasto.quantumelectrix.api.IModInventory;
import timicasto.quantumelectrix.api.IMultiblock;
import timicasto.quantumelectrix.api.IMultiblockRecipe;
import timicasto.quantumelectrix.energy.CustomEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class TileEntityMultiblock<T extends TileEntityMultiblock<T, Q>, Q extends IMultiblockRecipe> extends TileEntityMultiBlockPart<T> implements IDirectionalTile.IHammerInteraction, IDirectionalTile.IMirrorAble, IDirectionalTile.IProcessTile, IModInventory {
    public final CustomEnergyStorage energyStorage;
    protected final boolean hasRedStoneControl;
    protected final IMultiblock multiBlock;
    protected boolean redStoneControlInverted = false;
    public int controlComputers = 0;
    public boolean controlOn = true;

    public TileEntityMultiblock(IMultiblock multiBlock, int[] structureDimensions, int energyCapacity, boolean redStoneControl)
    {
        super(structureDimensions);
        this.energyStorage = new CustomEnergyStorage(energyCapacity);
        this.hasRedStoneControl = redStoneControl;
        this.multiBlock = multiBlock;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        energyStorage.readFromNBT(compound);
        redStoneControlInverted = compound.getBoolean("ControlInverted");
        NBTTagList process = compound.getTagList("ProcessQueue", 10);
        processQueue.clear();
        for (int i = 0 ; i < process.tagCount() ; i++) {
            NBTTagCompound nbt = process.getCompoundTagAt(i);
            IMultiblockRecipe multiblockRecipe = readRecipeFromNBT(nbt);
            if (multiblockRecipe != null) {
                int processTime = nbt.getInteger("ProcessTime");
                MultiblockProcess process1 = loadProcessFromNBT(nbt);
                if (process1 != null) {
                    process1.processTime = processTime;
                    processQueue.add(process1);
                }
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        energyStorage.writeToNBT(compound);
        compound.setBoolean("ControlInverted", redStoneControlInverted);
        NBTTagList process = new NBTTagList();
        for (MultiblockProcess process1 : this.processQueue) {
            process.appendTag(writeProcessToNBT(process1));
        }
        compound.setTag("ProcessQueue", process);
        return super.writeToNBT(compound);
    }

    public static NonNullList<ItemStack> readInventory(NBTTagList nbt, int size)
    {
        NonNullList<ItemStack> inv = NonNullList.withSize(size, ItemStack.EMPTY);
        int max = nbt.tagCount();
        for (int i = 0;i<max;i++)
        {
            NBTTagCompound itemTag = nbt.getCompoundTagAt(i);
            int slot = itemTag.getByte("Slot") & 255;
            if(slot>=0 && slot<size)
                inv.set(slot, new ItemStack(itemTag));
        }
        return inv;
    }

    public static NonNullList<ItemStack> loadItemStacksFromNBT(NBTBase tag) {
        NonNullList<ItemStack> itemStacks = NonNullList.create();
        if(tag instanceof NBTTagCompound)
        {
            ItemStack stack = new ItemStack((NBTTagCompound)tag);
            itemStacks.add(stack);
            return itemStacks;
        }
        else if(tag instanceof NBTTagList)
        {
            NBTTagList list = (NBTTagList)tag;
            return readInventory(list, list.tagCount());
        }
        return itemStacks;
    }

    protected abstract Q readRecipeFromNBT(NBTTagCompound compound);
    protected MultiblockProcess loadProcessFromNBT(NBTTagCompound compound) {
        IMultiblockRecipe recipe = readRecipeFromNBT(compound);
        if (recipe != null) {
            if (isInWorldProcessingMachine()) {
                return new MultiblockProcessInWorld(recipe, compound.getFloat("TransformationPoint"), loadItemStacksFromNBT(compound.getTag("InputItem")));
            } else {
                return new MultiblockProcessInMachine(recipe, compound.getIntArray("process_inputSlots")).setInputTanks(compound.getIntArray("process_inputTanks"));
            }
        }
        return null;
    }

    protected NBTTagCompound writeProcessToNBT(MultiblockProcess process) {
        NBTTagCompound compound = process.recipe.writeToNBT(new NBTTagCompound());
        compound.setInteger("ProcessTime", process.processTime);
        process.writeExtraDataToNBT(compound);
        return compound;
    }

    protected abstract int[] getEnergyPos();
    public boolean isEnergyPos() {
        for (int i : getEnergyPos()) {
            if (pos == i) {
                return true;
            }
        }
        return false;
    }

    public CustomEnergyStorage getEnergyStorage() {
        T master = this.master();
        if (master != null) {
            return master().energyStorage;
        }
        return energyStorage;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        if(!isDummy())
        {
            BlockPos nullPos = this.getBlockPosForPos(0);
            return new AxisAlignedBB(nullPos, nullPos.offset(facing,structureDimension[1]).offset(mirrored?facing.rotateYCCW():facing.rotateY(),structureDimension[2]).up(structureDimension[0]));
        }
        return super.getRenderBoundingBox();
    }

    public abstract int[] getRedstonePos();
    public boolean isRedstonePos() {
        if (!hasRedStoneControl || getRedstonePos() == null) {
            return false;
        }
        for (int i : getRedstonePos()) {
            if (pos == i) {
                return true;
            }
        }
        return false;
    }
    public boolean isControlPos() {
        if (!hasRedStoneControl || getRedstonePos() == null) {
            return false;
        }
        for (int i : getRedstonePos()) {
            if (pos == i) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hammerUseSide(EnumFacing side, EntityPlayer player, float hitX, float hitY, float hitZ) {
        if(this.isControlPos()) {
            TileEntityMultiblock<T, Q> master = master();
            master.redStoneControlInverted = !master.redStoneControlInverted;
            this.updateMaster(null, true);
            return true;
        }
        return false;
    }

    public boolean isDisabled() {
        if (controlComputers > 0 && controlOn) {
            return true;
        }
        int[] redStonePositions = getRedstonePos();
        if (redStonePositions == null || redStonePositions.length < 1) {
            return false;
        }
        for (int pos : redStonePositions) {
            T tile = getTileForPos(pos);
            if (tile != null) {
                boolean powered = world.isBlockIndirectlyGettingPowered(tile.getPos()) > 0;
                return redStoneControlInverted != powered;
            }
        }
        return false;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public T getTileForPos(int target) {
        BlockPos tar = getBlockPosForPos(target);
        TileEntity tile = world.getTileEntity(tar);
        if (this.getClass().isInstance(tile)) {
            return (T) tile;
        }
        return null;
    }

    @Override
    public ItemStack getOriginalBlock() {
        if (pos < 0) {
            return ItemStack.EMPTY;
        }
        ItemStack result = ItemStack.EMPTY;
        try {
            int blockPerH = structureDimension[1] * structureDimension[2];
            int h = (pos / blockPerH);
            int l = (pos % blockPerH / structureDimension[2]);
            int w = (pos % structureDimension[2]);
            result = this.multiBlock.getStructureManual()[w][h][l];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.copy();
    }

    @Override
    public boolean getIsMirrored() {
        return this.mirrored;
    }

    @Override
    public PropertyBool getBoolProperty(Class<? extends IUsesBoolean> inf) {
        return BOOLEANS[0];
    }
    
    public List<MultiblockProcess<Q>> processQueue = new ArrayList<MultiblockProcess<Q>>();
    public int ticked = 0;

    @Override
    public void update() {
        ticked = 0;
        if (world.isRemote || isDummy() || isDisabled()) {
            return;
        }
        int total = getMaxProcessTime();
        int i = 0;
        Iterator<MultiblockProcess<Q>> processIterator = processQueue.iterator();
        ticked = 0;
        while (processIterator.hasNext() && i++ < total) {
            MultiblockProcess<Q> process = processIterator.next();
            if (process.canProcess(this)) {
                process.doProcess(this);
                ticked++;
            }
            if (process.clearProcess) {
                processIterator.remove();
            }
        }
    }

    public abstract IFluidTank[] getTanks();
    public abstract Q findRecipeForInternal(ItemStack internal);
    public abstract int[] getOutputSlot();
    public abstract int[] getOutputTanks();
    public abstract boolean additionalCanProcessCheck(MultiblockProcess<Q> process);
    public abstract void doProcessOutput(ItemStack output);
    public abstract void doProcessFluidOutput(FluidStack output);
    public abstract void onProcessFinish(MultiblockProcess<Q> process);
    public abstract int getMaxProcessTime();
    public abstract int getProcessQueueMaxLength();
    public abstract float getMinProcessDistance(MultiblockProcess<Q> process);
    public abstract boolean isInWorldProcessingMachine();

    public boolean addProcessToQueue(MultiblockProcess<Q> process, boolean simulate) {
        return addProcessToQueue(process, simulate, false);
    }

    public boolean addProcessToQueue(MultiblockProcess<Q> process, boolean simulate, boolean toPrevious) {
        if (toPrevious && process instanceof MultiblockProcessInWorld) {
            for (MultiblockProcess<Q> i : processQueue) {
                if(i instanceof MultiblockProcessInWorld && process.recipe.equals(i.recipe))
                {
                    MultiblockProcessInWorld p = (MultiblockProcessInWorld) i;
                    boolean canStack = true;
                    for(ItemStack old : (List<ItemStack>)p.inputs) {
                        for(ItemStack in : (List<ItemStack>)((MultiblockProcessInWorld)process).inputs)
                            if(OreDictionary.itemMatches(old, in, true) && IDirectionalTile.compareNBT(old, in))
                                if(old.getCount() + in.getCount() >old.getMaxStackSize()) {
                                    canStack = false;
                                    break;
                                }
                        if(!canStack) {
                            break;
                        }
                    }
                    if(canStack) {
                        if(!simulate)
                            for(ItemStack old : (List<ItemStack>)p.inputs) {
                                for(ItemStack in : (List<ItemStack>)((MultiblockProcessInWorld)process).inputs)
                                    if(OreDictionary.itemMatches(old, in, true) && IDirectionalTile.compareNBT(old, in)) {
                                        old.grow(in.getCount());
                                        break;
                                    }
                            }
                        return true;
                    }
            }
        }
    }
    if (getProcessQueueMaxLength() < 0 || processQueue.size() < getProcessQueueMaxLength()) {
        float dist = 1;
        MultiblockProcess<Q> p = null;
        if(processQueue.size()>0) {
            p = processQueue.get(processQueue.size() - 1);
            if(p != null) {
                dist = p.processTime / (float) p.maxTime;
            }
        }
        if(p != null && dist < getMinProcessDistance(p))
            return false;

        if(!simulate) {
            processQueue.add(process);
        }
        return true;
    }
        return false;
    }

    @Override
    public int[] getCurrentProcessesStep() {
        T master = master();
        if (master != this && master != null) {
            return master.getCurrentProcessesStep();
        }
        int[] i = new int[processQueue.size()];
        for (int j = 0 ; j < i.length ; j++) {
            i[j] = processQueue.get(j).processTime;
        }
        return i;
    }

    @Override
    public int[] getCurrentProcessesMax() {
        T master = master();
        if (master != this && master != null) {
            return master.getCurrentProcessesMax();
        }
        int[] i = new int[processQueue.size()];
        for (int j = 0 ; j < i.length ; j++) {
            i[j] = processQueue.get(j).maxTime;
        }
        return i;
    }

    public boolean shouldRenderAsActive() {
        return (controlComputers <= 0 || controlOn) && energyStorage.getEnergyStored() > 0 && !isDisabled() && !processQueue.isEmpty();
    }

    public abstract static class MultiblockProcess<Q extends IMultiblockRecipe> {
        public Q recipe;
        public int processTime;
        public int maxTime;
        public int energyPerTick;
        public boolean clearProcess = false;

        public MultiblockProcess(Q recipe) {
            this.recipe = recipe;
            this.processTime = 0;
            this.maxTime = this.recipe.getTotalProcessTime();
            this.energyPerTick = this.recipe.getTotalProcessEnergy() / this.maxTime;
        }

        protected List<ItemStack> getRecipeItemOutputs(TileEntityMultiblock structure) {
            return recipe.getActualItemOutputs(structure);
        }

        protected List<FluidStack> getRecipeFluidOutputs(TileEntityMultiblock structure) {
            return recipe.getActualFluidOutputs(structure);
        }

        public boolean canProcess(TileEntityMultiblock structure) {
            if (structure.energyStorage.extractEnergy(energyPerTick, true) == energyPerTick) {
                List<ItemStack> outputs = recipe.getItemOutputs();
                if (outputs != null && !outputs.isEmpty()) {
                    int[] outputSlot = structure.getOutputSlot();
                    for (ItemStack output : outputs) {
                        if (!output.isEmpty()) {
                            boolean canOutput = false;
                            if (outputSlot == null) {
                                canOutput = true;
                            } else {
                                for (int i : outputSlot) {
                                    ItemStack result = structure.getInventory().get(i);
                                    if (result.isEmpty() || (ItemHandlerHelper.canItemStacksStack(result, output) && result.getCount() + output.getCount() <= structure.getSlotLimit(i))) {
                                        canOutput = true;
                                        break;
                                    }
                                }
                            }
                            if (!canOutput) {
                                return false;
                            }
                        }
                        List<FluidStack> fluidOutputs = recipe.getFluidOutputs();
                        if (fluidOutputs != null && !fluidOutputs.isEmpty()) {
                            IFluidTank[] tanks = structure.getTanks();
                            int[] outputTanks = structure.getOutputTanks();
                            for (FluidStack j : fluidOutputs)
                                if (j != null && j.amount > 0) {
                                    boolean canOutput = false;
                                    if (tanks == null || outputTanks == null)
                                        canOutput = true;
                                    else {
                                        for (int iOutputTank : outputTanks)
                                            if (iOutputTank >= 0 && iOutputTank < tanks.length && tanks[iOutputTank] != null && tanks[iOutputTank].fill(j, false) == j.amount) {
                                                canOutput = true;
                                                break;
                                            }
                                    }
                                    if (!canOutput)
                                        return false;
                                }
                        }
                        return structure.additionalCanProcessCheck(this);
                    }
                }
            }
            return false;
        }

        public void doProcess(TileEntityMultiblock structure) {
            int energyExtracted = energyPerTick;
            int added = 1;
            if (this.recipe.getMultipleProcessTicks() > 1) {
                int averageInsertion = structure.energyStorage.getAverageInsertion();
                averageInsertion = structure.energyStorage.extractEnergy(averageInsertion, true);
                if(averageInsertion>energyExtracted) {
                    int possibleTicks = Math.min(averageInsertion/energyPerTick, Math.min(this.recipe.getMultipleProcessTicks(), this.maxTime-this.processTime));
                    if(possibleTicks>1) {
                        added = possibleTicks;
                        energyExtracted *= added;
                    }
                }
            }
            structure.energyStorage.extractEnergy(energyExtracted, false);
            this.processTime += added;

            if(this.processTime>=this.maxTime) {
                this.processFinish(structure);
            }
        }

        protected void processFinish(TileEntityMultiblock structure) {
            List<ItemStack> outputs = getRecipeItemOutputs(structure);
            if(outputs!=null && !outputs.isEmpty()) {
                int[] outputSlots = structure.getOutputSlot();
                for(ItemStack output : outputs)
                    if(!output.isEmpty())
                        if(outputSlots==null || structure.getInventory()==null)
                            structure.doProcessOutput(output.copy());
                        else {
                            for(int iOutputSlot:outputSlots) {
                                ItemStack s = structure.getInventory().get(iOutputSlot);
                                if(s.isEmpty()) {
                                    structure.getInventory().set(iOutputSlot, output.copy());
                                    break;
                                }
                                else if(ItemHandlerHelper.canItemStacksStack(s, output) && s.getCount() + output.getCount() <= structure.getSlotLimit(iOutputSlot)) {
                                    structure.getInventory().get(iOutputSlot).grow(output.getCount());
                                    break;
                                }
                            }
                        }
            }
            List<FluidStack> fluidOutputs = getRecipeFluidOutputs(structure);
            if(fluidOutputs!=null && !fluidOutputs.isEmpty()) {
                IFluidTank[] tanks = structure.getTanks();
                int[] outputTanks = structure.getOutputTanks();
                for(FluidStack output : fluidOutputs)
                    if(output!=null && output.amount>0) {
                        if(tanks==null || outputTanks==null)
                            structure.doProcessFluidOutput(output);
                        else {
                            for(int iOutputTank : outputTanks)
                                if(iOutputTank >= 0&&iOutputTank < tanks.length&&tanks[iOutputTank]!=null&&tanks[iOutputTank].fill(output, false)==output.amount) {
                                    tanks[iOutputTank].fill(output, true);
                                    break;
                                }
                        }
                    }
            }

            structure.onProcessFinish(this);
            this.clearProcess = true;
        }

        protected abstract void writeExtraDataToNBT(NBTTagCompound nbt);
        }

        public static class MultiblockProcessInMachine<Q extends IMultiblockRecipe> extends MultiblockProcess<Q> {
            protected int[] inputSlots = new int[0];
            protected int[] inputTanks = new int[0];

            public MultiblockProcessInMachine(Q recipe, int... inputSlots) {
                super(recipe);
                this.inputSlots = inputSlots;
            }

            public MultiblockProcessInMachine setInputTanks(int... inputTanks) {
                this.inputTanks = inputTanks;
                return this;
            }

            public int[] getInputSlots() {
                return this.inputSlots;
            }

            public int[] getInputTanks() {
                return this.inputTanks;
            }

            protected List<ItemStack> getRecipeItemInputs(TileEntityMultiblock structure) {
                return recipe.getItemInputs();
            }

            protected List<FluidStack> getReipeFluidInputs(TileEntityMultiblock structure) {
                return recipe.getFluidInputs();
            }

            @Override
            public void doProcess(TileEntityMultiblock structure) {
                NonNullList<ItemStack> inventory = structure.getInventory();
                if (recipe.getItemInputs() != null && inventory != null) {
                    NonNullList<ItemStack> q = NonNullList.withSize(inputSlots.length, ItemStack.EMPTY);
                    for (int i = 0 ; i < inputSlots.length ; i++) {
                        if (inputSlots[i] >= 0 && inputSlots[i] < inventory.size()) {
                            q.set(i, structure.getInventory().get(inputSlots[i]));
                        }
                    }
                    if (!IDirectionalTile.stacksMatchItemStack(recipe.getItemInputs(), q)) {
                        this.clearProcess = true;
                        return;
                    }
                }

                super.doProcess(structure);
            }

            @Override
            protected void processFinish(TileEntityMultiblock structure) {
                super.processFinish(structure);
                NonNullList<ItemStack> inventory = structure.getInventory();
                List<ItemStack> inputs = this.getRecipeItemInputs(structure);
                if (inventory != null && this.inputSlots != null && inputs != null) {
                    Iterator<ItemStack> iterator = new ArrayList(inputs).iterator();
                    while (iterator.hasNext()) {
                        ItemStack stack = iterator.next();
                        int size = stack.getCount();
                        for (int slot : this.inputSlots) {
                            if (!inventory.get(slot).isEmpty() && IDirectionalTile.matchStackWithoutCount(stack, inventory.get(slot))) {
                                int min = Math.min(inventory.get(slot).getCount(), size);
                                inventory.get(slot).shrink(min);
                                if (inventory.get(slot).getCount() <= 0) {
                                    inventory.set(slot, ItemStack.EMPTY);
                                }
                                if ((size -= min) <= 0) {
                                    break;
                                }
                            }
                        }
                    }
                }
                IFluidTank[] tanks = structure.getTanks();
                List<FluidStack> fluidStacks = this.getReipeFluidInputs(structure);
                if (tanks != null && this.inputTanks != null && fluidStacks != null) {
                    Iterator<FluidStack> iterator = new ArrayList(fluidStacks).iterator();
                    while(iterator.hasNext()) {
                        FluidStack ingr = iterator.next();
                        int ingrSize = ingr.amount;
                        for(int tank : this.inputTanks) {
                            if (tanks[tank] != null) {
                                if (tanks[tank] instanceof IFluidHandler && ((IFluidHandler) tanks[tank]).drain(ingr, false) != null) {
                                    FluidStack taken = ((IFluidHandler) tanks[tank]).drain(ingr, true);
                                    if ((ingrSize -= taken.amount) <= 0) {
                                        break;
                                    }
                                } else if (tanks[tank].getFluid() != null && tanks[tank].getFluid().isFluidEqual(ingr)) {
                                    int taken = Math.min(tanks[tank].getFluidAmount(), ingrSize);
                                    tanks[tank].drain(taken, true);
                                    if ((ingrSize -= taken) <= 0) {
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            @Override
            protected void writeExtraDataToNBT(NBTTagCompound nbt) {
                if (inputSlots != null) {
                    nbt.setIntArray("inputSlots", inputSlots);
                }
                if (inputTanks != null) {
                    nbt.setIntArray("inputTanks", inputTanks);
                }
            }
        }

        public static class MultiblockProcessInWorld<Q extends IMultiblockRecipe> extends MultiblockProcess<Q> {
            public List<ItemStack> inputs;
            protected float transformPoint;
            MultiblockProcessInWorld(Q recipe, float transformPoint, NonNullList<ItemStack> inputs) {
                super(recipe);
                this.inputs = new ArrayList<>(inputs.size());
                for (ItemStack stack : inputs) {
                    this.inputs.add(stack);
                }
                this.transformPoint = transformPoint;
            }

            public List<ItemStack> getDisplayItem() {
                if (processTime / (float)maxTime > transformPoint) {
                    List<ItemStack> list = this.recipe.getItemOutputs();
                    if (!list.isEmpty()) {
                        return list;
                    }
                }
                return inputs;
            }

            @Override
            protected void writeExtraDataToNBT(NBTTagCompound nbt) {
                nbt.setTag("inputItem", IDirectionalTile.writeInventory(inputs));
                nbt.setFloat("transformationPoint", transformPoint);
            }

            @Override
            protected void processFinish(TileEntityMultiblock structure) {
                super.processFinish(structure);
                int size = -1;
                for (ItemStack stack : this.inputs) {
                    for (ItemStack s : recipe.getItemInputs()) {
                        if (IDirectionalTile.matchStackWithoutCount(s, stack)) {
                            size = s.getCount();
                            break;
                        }
                    }
                    if (size > 0 && stack.getCount() > size) {
                        stack.splitStack(size);
                        processTime = 0;
                        clearProcess = false;
                    }
                }
            }
        }

        public static class MultiblockInventoryHandlerDirectProcess implements IItemHandlerModifiable {
            TileEntityMultiblock structure;
            float transformPoint = 0.5F;
            boolean processing = false;

            public MultiblockInventoryHandlerDirectProcess(TileEntityMultiblock structure) {
                this.structure = structure;
            }

            public MultiblockInventoryHandlerDirectProcess setTransformPoint(float value) {
                this.transformPoint = value;
                return this;
            }

            public MultiblockInventoryHandlerDirectProcess setProcessing(boolean value) {
                this.processing = value;
                return this;
            }

            @Override
            public int getSlots() {
                return 1;
            }

            @Override
            public ItemStack getStackInSlot(int slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                stack = stack.copy();
                IMultiblockRecipe recipe = this.structure.findRecipeForInternal(stack);
                if (recipe == null) {
                    return stack;
                }
                ItemStack display = ItemStack.EMPTY;
                for (ItemStack stacks : recipe.getItemInputs()) {
                    if (stacks.getItem() == stack.getItem() && stacks.getCount() <= stack.getCount()) {
                        display = IDirectionalTile.copy(stack, stacks.getCount());
                        break;
                    }
                }
                if (structure.addProcessToQueue(new MultiblockProcessInWorld(recipe, transformPoint, IDirectionalTile.createList(display)), simulate, processing)) {
                    structure.markDirty();
                    structure.markContainingBlockForUpdate(null);
                    stack.shrink(display.getCount());
                    if (stack.getCount() <= 0) {
                        stack = ItemStack.EMPTY;
                    }
                }
                return stack;
            }

            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                return ItemStack.EMPTY;
            }

            @Override
            public int getSlotLimit(int slot) {
                return 64;
            }

            @Override
            public void setStackInSlot(int slot, ItemStack stack) {

            }
        }
}
