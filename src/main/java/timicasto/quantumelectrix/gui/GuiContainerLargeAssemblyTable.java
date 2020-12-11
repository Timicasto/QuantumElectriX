package timicasto.quantumelectrix.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.container.ContainerLargeAssemblyTable;
import timicasto.quantumelectrix.tile.TileEntityLargeAssemblyTable;

public class GuiContainerLargeAssemblyTable extends GuiContainer {
    private static final ResourceLocation BG = new ResourceLocation(QuantumElectriX.MODID + ":textures/gui/large_assembly_table_bg.png");
    private final InventoryPlayer player;
    private final TileEntityLargeAssemblyTable tile;

    public GuiContainerLargeAssemblyTable(InventoryPlayer player, TileEntityLargeAssemblyTable tile) {
        super(new ContainerLargeAssemblyTable(player, tile));

        this.player = player;
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BG);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
