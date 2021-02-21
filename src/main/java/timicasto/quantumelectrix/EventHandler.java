package timicasto.quantumelectrix;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import timicasto.quantumelectrix.api.OtherInterfaces;
import timicasto.quantumelectrix.api.Utils;

import java.util.ArrayList;
import java.util.List;

public class EventHandler {
    public static boolean first = false;
    @SubscribeEvent()
    public void renderAdvBlockBounds(DrawBlockHighlightEvent event) {
        if (event.getSubID() == 0 && event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK) {
            float f = 0.002F;
            double pixelX = -TileEntityRendererDispatcher.staticPlayerX;
            double pixelY = -TileEntityRendererDispatcher.staticPlayerY;
            double pixelZ = -TileEntityRendererDispatcher.staticPlayerZ;

            TileEntity tile = event.getPlayer().world.getTileEntity(event.getTarget().getBlockPos());
            ItemStack stack = event.getPlayer().getHeldItem(EnumHand.MAIN_HAND);
            if (tile instanceof OtherInterfaces.IAdvancedSelectionBounds) {
                OtherInterfaces.IAdvancedSelectionBounds selectionBounds = (OtherInterfaces.IAdvancedSelectionBounds)tile;
                List<AxisAlignedBB> selections = selectionBounds.getAdvancedSelectionBound();
                if (selections != null && !selections.isEmpty()) {
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                    GlStateManager.glLineWidth(2.0F);
                    GlStateManager.disableTexture2D();
                    GlStateManager.depthMask(false);
                    ArrayList<AxisAlignedBB> addition = new ArrayList<>();
                    AxisAlignedBB override = null;
                    for (AxisAlignedBB boundBox : selections) {
                        if (boundBox != null) {
                            if (selectionBounds.isOverrided(boundBox, event.getPlayer(), event.getTarget(), addition)) {
                                override = boundBox;
                            }
                        }
                    }

                    if (override != null) {
                        RenderGlobal.drawSelectionBoundingBox(override.expand(f, f, f).offset(pixelX, pixelY, pixelZ), 0, 0, 0, 0.4F);
                    } else {
                        for (AxisAlignedBB box : addition.isEmpty() ? selections : addition) {
                            RenderGlobal.drawSelectionBoundingBox(box.expand(f, f, f).offset(pixelX, pixelY, pixelZ), 0, 0, 0, 0.4F);
                        }
                    }
                    GlStateManager.depthMask(true);
                    GlStateManager.enableTexture2D();
                    GlStateManager.disableBlend();
                    event.setCanceled(true);
                }
            }
        }
    }

    public int hammingWeight(int n) {
        String string = Integer.toString(00000000000000000000000000001011);
        char[] chars = string.toCharArray();
        int count = 0;
        for (int i = 0 ; i < chars.length ; i++) {
            if (chars[i] == '1') {
                count++;
            }
        }
        System.out.println(count);
        return count;
    }
}











