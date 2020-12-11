package timicasto.quantumelectrix.api;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface IDirectionalTile {

    EnumFacing getFacing();
    void setFacing(EnumFacing facing);
    int getFacingLimitation();
    default EnumFacing getFacingForPlacement(EntityLivingBase placer, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        EnumFacing f = EnumFacing.DOWN;
        int limit = getFacingLimitation();
        if(limit==0)
            f = side;
        else if(limit==1)
            f = EnumFacing.getDirectionFromEntityLiving(pos, placer);
        else if(limit==2)
            f = EnumFacing.fromAngle(placer.rotationYaw);
        else if(limit==3)
            f = (side!=EnumFacing.DOWN&&(side==EnumFacing.UP||hitY<=.5))?EnumFacing.UP : EnumFacing.DOWN;
        else if(limit==4)
        {
            f = EnumFacing.fromAngle(placer.rotationYaw);
            if(f==EnumFacing.SOUTH || f==EnumFacing.WEST)
                f = f.getOpposite();
        } else if(limit == 5)
        {
            if(side.getAxis() != EnumFacing.Axis.Y)
                f = side.getOpposite();
            else
            {
                float xFromMid = hitX - .5f;
                float zFromMid = hitZ - .5f;
                float max = Math.max(Math.abs(xFromMid), Math.abs(zFromMid));
                if(max == Math.abs(xFromMid))
                    f = xFromMid < 0 ? EnumFacing.WEST : EnumFacing.EAST;
                else
                    f = zFromMid < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
            }
        }
        else if(limit == 6)
            f = side.getAxis()!= EnumFacing.Axis.Y? side.getOpposite(): EnumFacing.getDirectionFromEntityLiving(pos, placer);

        return mirrorFacingOnPlacement(placer)?f.getOpposite():f;
    }
    boolean mirrorFacingOnPlacement(EntityLivingBase placer);
    boolean canHammerRotate(EnumFacing side, float hitX, float hitY, float hitZ, EntityLivingBase entity);
    boolean canRotate(EnumFacing axis);
    default void afterRotation(EnumFacing oldDir, EnumFacing newDir){}

    public interface IBlockBounds
    {
        float[] getBlockBounds();
    }

    public interface ITileDrop {
        ItemStack getTileDrop(EntityPlayer player, IBlockState state);

        void readOnPlacement(@Nullable EntityLivingBase placer, ItemStack stack);

        default boolean preventInventoryDrop() { return false; }
    }

    public interface IMirrorAble extends IUsesBoolean
    {
        boolean getIsMirrored();
    }

    PropertyBool[] BOOLEANS = {
            PropertyBool.create("bool0"),
            PropertyBool.create("bool1"),
            PropertyBool.create("bool2")
    };

    static boolean compareNBT(ItemStack i1, ItemStack i2) {
        if ((i1.isEmpty()) != (i2.isEmpty())) {
            return false;
        }
        boolean emp1 = (i1.getTagCompound()==null||i1.getTagCompound().hasNoTags());
        boolean emp2 = (i2.getTagCompound()==null||i2.getTagCompound().hasNoTags());
        if(emp1!=emp2)
            return false;
        if(!emp1 && !i1.getTagCompound().equals(i2.getTagCompound()))
            return false;
        return i1.areCapsCompatible(i2);
    }

    public static final PropertyBool[] SIDECONNECTION = {
            PropertyBool.create("sideconnection_down"),
            PropertyBool.create("sideconnection_up"),
            PropertyBool.create("sideconnection_north"),
            PropertyBool.create("sideconnection_south"),
            PropertyBool.create("sideconnection_west"),
            PropertyBool.create("sideconnection_east")
    };

    public interface IUsesBoolean
    {
        PropertyBool getBoolProperty(Class<? extends IUsesBoolean> inf);
    }

    public interface IHammerInteraction
    {
        boolean hammerUseSide(EnumFacing side, EntityPlayer player, float hitX, float hitY, float hitZ);
    }

    public interface IProcessTile
    {
        int[] getCurrentProcessesStep();
        int[] getCurrentProcessesMax();
    }

    public static boolean stacksMatchItemStack(List<ItemStack> in, NonNullList<ItemStack> stacks) {
        ArrayList<ItemStack> q = new ArrayList<ItemStack>(stacks.size());
        for (ItemStack s : stacks) {
            if (!s.isEmpty()) {
                q.add(s.copy());
            }
        }

        for (ItemStack stack : in) {
            if (stack != null) {
                int amount = stack.getCount();
                Iterator<ItemStack> iterator = q.iterator();
                while (iterator.hasNext()) {
                    ItemStack q1 = iterator.next();
                    if (!q1.isEmpty()) {
                        if (q1.getCount() > amount) {
                            q1.shrink(amount);
                            amount = 0;
                        } else {
                            amount -= q1.getCount();
                            q1.setCount(0);
                        }
                    }
                    if (q1.getCount() <= 0) {
                        iterator.remove();
                    }
                    if (amount <= 0) {
                        break;
                    }
                }
                if (amount > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean matchStackWithoutCount(ItemStack input1, ItemStack input2) {
        if (input1.getItem() == input2.getItem()) {
            return true;
        }
        return false;
    }

    static NBTTagList writeInventory(Collection<ItemStack> stacks) {
        NBTTagList list = new NBTTagList();
        byte slot = 0;
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                NBTTagCompound tag = new NBTTagCompound();
                tag.setByte("Slot", slot);
                stack.writeToNBT(tag);
                list.appendTag(tag);
            }
            slot++;
        }
        return list;
    }

    static ItemStack copy(ItemStack stack, int count) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack1 = stack.copy();
        stack1.setCount(count);
        return stack1;
    }

    static NonNullList<ItemStack> createList(ItemStack stack) {
        NonNullList<ItemStack> list = NonNullList.withSize(1, ItemStack.EMPTY);
        list.set(0, stack);
        return list;
    }

    interface IDamageable {
        int getMaxDamage(ItemStack param1ItemStack);

        int getItemDamage(ItemStack param1ItemStack);
    }
}

















