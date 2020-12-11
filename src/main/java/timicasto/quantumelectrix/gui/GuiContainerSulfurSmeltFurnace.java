package timicasto.quantumelectrix.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.FluidStack;
import timicasto.quantumbase.QuantumBase;
import timicasto.quantumelectrix.container.ContainerSulfurSmeltFurnace;
import timicasto.quantumelectrix.tile.TileEntityBurningChamber;

public class GuiContainerSulfurSmeltFurnace extends GuiContainer {
    private static final ResourceLocation BG = new ResourceLocation(QuantumBase.MODID + "textures/gui/petroleum_processor");
    private static final ResourceLocation BAR = new ResourceLocation(QuantumBase.MODID + "textures/gui/fluid_bar.png");
    private final InventoryPlayer player;
    private final TileEntityBurningChamber tile;

    public GuiContainerSulfurSmeltFurnace(InventoryPlayer player, TileEntityBurningChamber tile) {
        super(new ContainerSulfurSmeltFurnace(player, tile));
        this.player = player;
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BG);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String tileName = "硫铁矿熔炼炉";
        this.fontRenderer.drawString(tileName, (this.xSize / 2 - this.fontRenderer.getStringWidth(tileName) / 2) + 3, 8, 4210752);
        FluidStack petroleumTank = tile.getSO2().getFluid();
        if (petroleumTank != null && petroleumTank.amount > 0) {
            int fluidColor = petroleumTank.getFluid().getColor(petroleumTank);
            float red = (fluidColor >> 16 & 0xFF) / 255F;
            float green = (fluidColor >> 8 & 0xFF) / 255F;
            float blue = (fluidColor & 0xFF) / 255F;

            float peraFilled = ((float) petroleumTank.amount) / ((float) tile.getSO2().getCapacity());
            peraFilled = MathHelper.clamp(peraFilled, 0F, 1F);
            int pxFilled = MathHelper.ceil(peraFilled * 61F);
            GlStateManager.color(red, green, blue, 1.0F);
            ResourceLocation rl = petroleumTank.getFluid().getStill(petroleumTank);
            TextureAtlasSprite tas = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(rl.toString());
            if (tas == null) {
                tas = Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
            }
            this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            drawTexturedModalRect(15, 10 + 61 - pxFilled, tas, 20, pxFilled);
        }
        GlStateManager.color(1.0F,1.0F,1.0F,1.0F);
        this.mc.getTextureManager().bindTexture(BAR);
        this.drawTexturedModalRect(15, 10, 176, 0, 20, 61);
    }
}
