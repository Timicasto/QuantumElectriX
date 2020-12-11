package timicasto.quantumelectrix.block.multiblocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import timicasto.quantumelectrix.RegistryHandler;
import timicasto.quantumelectrix.api.CompareElement;
import timicasto.quantumelectrix.api.IMultiblock;
import timicasto.quantumelectrix.block.types.BlockTypesMultiBlock;
import timicasto.quantumelectrix.tile.TileEntityContactChamber;

public class MultiBlockContactChamber implements IMultiblock{
    public static MultiBlockContactChamber instance = new MultiBlockContactChamber();

    static ItemStack[][][] structure = new ItemStack[5][5][9];

    static {
        for (int w = 0 ; w < 5 ; w++) {
            for (int l = 0 ; l < 5 ; l++) {
                for (int h = 0 ; h < 9; h++) {
                    if (h == 0) {
                        structure[l][w][h] = new ItemStack(Blocks.CONCRETE, 1, 7);
                    }
                    if (h > 0 && h < 7) {
                        if (w == 0 || w == 4) {
                            structure[l][w][h] = new ItemStack(Blocks.CONCRETE, 1, 7);
                        } else if (l == 0 || l == 4) {
                            structure[l][w][h] = new ItemStack(Blocks.CONCRETE, 1, 7);
                        }
                        if (h == 2 || h == 5) {
                            if (w != 0 && w != 4 && l != 0 && l != 4) {
                                structure[l][w][h] = new ItemStack(Blocks.IRON_BLOCK);
                            }
                        }
                    }
                    if (h == 7) {
                        if (l != 0 && l != 4 && w != 0 && w != 4) {
                            structure[l][w][h] = new ItemStack(Blocks.CONCRETE, 1, 7);
                        }
                    }
                    if (h == 8) {
                        if (l == 2 && w == 2) {
                            structure[l][w][h] = new ItemStack(RegistryHandler.AIR_WAY);
                        }
                    }
                    if (structure[l][w][h] == null) {
                        structure[l][w][h] = ItemStack.EMPTY;
                    }
                }
            }
        }
    }

    @Override
    public ItemStack[][][] getStructureManual() {
        return structure;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean overwriteBlockRender(ItemStack stack, int iterator) {
        return false;
    }

    @Override
    public float getManualScale() {
        return 1;
    }

    @Override
    public IBlockState getBlockstateFromStack(int index, ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() == RegistryHandler.AIR_WAY_ITEM) {
                return RegistryHandler.AIR_WAY.getDefaultState();
            } else if (stack.getItem() instanceof ItemBlock) {
                return ((ItemBlock)stack.getItem()).getBlock().getStateFromMeta(stack.getItemDamage());
            }
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderFormedStructure() {
        return true;
    }

    static ItemStack render = ItemStack.EMPTY;
    @Override
    public void renderFormedStructure() {
        if (render.isEmpty()) {
            render = new ItemStack(RegistryHandler.BLOCK_MULTI_BLOCK, 1, BlockTypesMultiBlock.CONTACT_CHAMBER.getMeta());
        }
        GlStateManager.translate(2.5, 2.25, 2.25);
        GlStateManager.rotate(-45, 0, 1, 0);
        GlStateManager.rotate(-20, 1, 0, 0);
        GlStateManager.scale(6.5, 6.5, 6.5);
        GlStateManager.disableCull();
        Minecraft.getMinecraft().getRenderItem().renderItem(render, ItemCameraTransforms.TransformType.GUI);
        GlStateManager.enableCull();
    }

    @Override
    public String getUniqueName() {
        return "quantumelectrix:ContactChamber";
    }

    @Override
    public boolean isBlockTrigger(IBlockState state) {
        return state.getBlock() == RegistryHandler.AIR_WAY;
    }

    @Override
    public boolean createStructure(World world, BlockPos pos, EnumFacing side, EntityPlayer player) {
        System.out.println("ran create structure");
        if(side==EnumFacing.UP||side==EnumFacing.DOWN)
            side = EnumFacing.fromAngle(player.rotationYaw);
        BlockPos startPos = pos;
        side = side.getOpposite();

        if(CompareElement.isBlockAt(world, startPos.add(0,-1,0), Blocks.CONCRETE, OreDictionary.WILDCARD_VALUE)
                && CompareElement.isBlockAt(world, startPos.offset(side,2).add(0,-2,0), Blocks.CONCRETE, OreDictionary.WILDCARD_VALUE))
        {
            startPos = startPos.offset(side,2);
            side = side.getOpposite();
        }

        boolean mirrored = false;
        boolean b = structureCheck(world,startPos, side, mirrored);
        System.out.println("state:" + b);
        if(!b)
        {
            mirrored = true;
            b = structureCheck(world,startPos, side, mirrored);
        }
        System.out.println("postState:" + b);

        if(b)
        {
            for(int l=-2;l<=2;l++)
                for(int w=-2;w<=2;w++)
                    for(int h=0;h <= 8;h++)
                        if(!structure[l+2][w+2][h].isEmpty())
                        {
                            int ww = mirrored?-w:w;
                            BlockPos pos2 = startPos.offset(side, l+2).offset(side.rotateY(), ww).add(0, h-8, 0);

                            world.setBlockState(pos2, RegistryHandler.BLOCK_MULTI_BLOCK.getStateFromMeta(BlockTypesMultiBlock.CONTACT_CHAMBER.getMeta()));
                            TileEntity compare = world.getTileEntity(pos2);
                            if(compare instanceof TileEntityContactChamber) {
                                TileEntityContactChamber tile = (TileEntityContactChamber) compare;
                                tile.facing = side;
                                tile.formed=true;
                                tile.pos = (h - 8) * 25 + (l + 2) * 5 + ( w + 2 );
                                tile.offset = new int[]{(side==EnumFacing.WEST?-l+2: side==EnumFacing.EAST?l-2: side==EnumFacing.NORTH?ww: -ww),h-1,(side==EnumFacing.NORTH?-l+2: side==EnumFacing.SOUTH?l-2: side==EnumFacing.EAST?ww : -ww)};
                                tile.mirrored = mirrored;
                                tile.markDirty();
                                world.addBlockEvent(pos2, RegistryHandler.BLOCK_MULTI_BLOCK, 255, 0);
                            }
                        }
        }
        System.out.println("return state:"+ b);
        return b;
    }

    boolean structureCheck(World world, BlockPos startPos, EnumFacing dir, boolean mirror) {
        System.out.println("ran check");
        for (int l = -2; l <= 2; l++) {
            for (int w = -2; w <= 2; w++) {
                for (int h = 0 ; h <= 8; h++) {
                    System.out.println("checking " + (l+2) + "," + (w+2) + "," + (h));
                    System.out.println("Raw Value: " + structure[l+2][w+2][h]);
                    System.out.println("Real Value : " + world.getBlockState(startPos.offset(dir, l).offset(dir.rotateY(), (mirror ? -w : w)).add(0, h, 0)).getBlock());
                    if (!structure[l+2][w+2][h].isEmpty()) {
                        int w1 = mirror ? -w : w;
                        BlockPos pos = startPos.offset(dir, l+2).offset(dir.rotateY(), w1).add(0, h-8, 0);
                        System.out.println("checking pos : " + pos);
                        if (world.isAirBlock(pos)) {
                            System.out.println("APE on : " + (l+2) + "," + (w+2) + "," + (h));
                            return false;
                        }
                        if (OreDictionary.itemMatches(structure[l+2][w+2][h], new ItemStack(RegistryHandler.AIR_WAY), true)) {
                            if (!CompareElement.isBlockAt(world, pos, RegistryHandler.AIR_WAY, OreDictionary.WILDCARD_VALUE)) {
                                System.out.println("Air Way Error!on" + (l+2) + "," + (w+2) + "," + (h));
                                return false;
                            }
                        } else if (OreDictionary.itemMatches(structure[l+2][w+2][h], new ItemStack(Blocks.CONCRETE, 1, 7), true)) {
                                if (!CompareElement.isBlockAt(world, pos, Blocks.CONCRETE, 7)) {
                                    System.out.println("Concrete Error! on" + (l+2) + "," + (w+2) + "," + (h));
                                    return false;
                                }
                        } else if (OreDictionary.itemMatches(structure[l+2][w+2][h], new ItemStack(Blocks.IRON_BLOCK), true)) {
                                if (!CompareElement.isBlockAt(world, pos, Blocks.IRON_BLOCK, OreDictionary.WILDCARD_VALUE)) {
                                    System.out.println("Iron_Block Error!on" + (l+2) + "," + (w+2) + "," + (h));
                                    return false;
                                }
                        } else {
                            Block block = Block.getBlockFromItem(structure[l+2][w+2][h].getItem());
                            if (block != null) {
                                if (!CompareElement.isBlockAt(world, pos, block, structure[l+2][w+2][h].getItemDamage())) {
                                    System.out.println("Invalid Error!on" + (l+2) + "," + (w+2) + "," + (h));
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Valid structure!");
        return true;
    }




}

