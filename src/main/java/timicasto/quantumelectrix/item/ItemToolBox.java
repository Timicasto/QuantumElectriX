package timicasto.quantumelectrix.item;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import timicasto.quantumelectrix.api.IDirectionalTile;
import timicasto.quantumelectrix.api.IMultiblock;
import timicasto.quantumelectrix.api.MultiblockHandler;
import timicasto.quantumelectrix.api.NBTHelper;
import timicasto.quantumelectrix.creative.TabLoader;

public class ItemToolBox extends Item implements IDirectionalTile.IDamageable {
    static int maxDamage;
    public ItemToolBox() {
        super();
        setRegistryName("tool_box");
        setUnlocalizedName("tool_box");
        setCreativeTab(TabLoader.elecTab);
        maxDamage = 16;
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        EnumActionResult ret = formStructure(player, world, pos, side, hand);
        if (world.isRemote && ret != EnumActionResult.PASS) {
            Minecraft.getMinecraft().getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, side, hand, hitX, hitY, hitZ));
        }
        System.out.println("onItemUseFirst Ran");
        return ret;
    }

    private EnumActionResult formStructure(EntityPlayer player, World world, BlockPos pos, EnumFacing side, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getMetadata() == 0) {
            String[] permittedStructures = null;
            String[] interdictedStructures = null;
            if (NBTHelper.hasKey(stack, "multiblockPermission")) {
                NBTTagList list = stack.getTagCompound().getTagList("multiblockPermission", 8);
                permittedStructures = new String[list.tagCount()];
                for (int i = 0 ; i < permittedStructures.length ; i++) {
                    permittedStructures[i] = list.getStringTagAt(i);
                }
            }
            if (NBTHelper.hasKey(stack, "multiblockInterdiction")) {
                NBTTagList list = stack.getTagCompound().getTagList("multiblockInterdiction", 8);
                interdictedStructures = new String[list.tagCount()];
                for (int i = 0 ; i < interdictedStructures.length ; i++) {
                    interdictedStructures[i] = list.getStringTagAt(i);
                }
            }
            for (IMultiblock structure : MultiblockHandler.getMultiblocks()) {
                if (structure.isBlockTrigger(world.getBlockState(pos))) {
                    boolean notNull = permittedStructures == null;
                    if (permittedStructures != null) {
                        for (String s : permittedStructures) {
                            if (structure.getUniqueName().equalsIgnoreCase(s)) {
                                notNull = true;
                                continue;
                            }
                        }
                    }
                    if (!notNull) {
                        break;
                    }
                    if (interdictedStructures != null) {
                        for (String s : interdictedStructures) {
                            if (structure.getUniqueName().equalsIgnoreCase(s)) {
                                notNull = false;
                                continue;
                            }
                        }
                    }
                    if (!notNull) {
                        System.out.println("Structure is null!");
                        break;
                    }
                    if (MultiblockHandler.postMultiblockFormationEvent(player, structure, pos, stack).isCanceled()) {
                        System.out.println("event canceled");
                        continue;
                    }
                    System.out.println("event passed");
                    if (structure.createStructure(world, pos, side, player)) {
                        System.out.println("formed");
                        return EnumActionResult.SUCCESS;
                    }
                }
            }
        }
        System.out.println("failed");
        return EnumActionResult.PASS;
    }

    @Override
    public int getItemDamage(ItemStack param1ItemStack) {
        return 0;
    }
}
