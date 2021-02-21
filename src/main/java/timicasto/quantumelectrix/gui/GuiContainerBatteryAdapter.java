package timicasto.quantumelectrix.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.energy.CapabilityEnergy;
import timicasto.quantumelectrix.QuantumElectriX;
import timicasto.quantumelectrix.container.ContainerBatteryAdapter;
import timicasto.quantumelectrix.tile.TileEntityBatteryAdapter;

import java.util.Objects;

public class GuiContainerBatteryAdapter extends GuiContainer {
    private static final ResourceLocation BG = new ResourceLocation(QuantumElectriX.MODID, "textures/gui/battery_adapter_bg.png");
    private final InventoryPlayer player;
    private final TileEntityBatteryAdapter tile;

    public GuiContainerBatteryAdapter(InventoryPlayer player, TileEntityBatteryAdapter tile) {
        super(new ContainerBatteryAdapter(player, tile));
        this.player = player;
        this.tile = tile;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        String title = "电池盒";
        this.fontRenderer.drawString(title, (this.xSize / 2 - this.fontRenderer.getStringWidth(title) / 2) + 3, 8, 4210752);
        int energyStored = Objects.requireNonNull(tile.getCapability(CapabilityEnergy.ENERGY, null)).getEnergyStored();
        int maxEnergy = Objects.requireNonNull(tile.getCapability(CapabilityEnergy.ENERGY, null)).getMaxEnergyStored();
        this.drawVerticalLine(40, 180, 220, 0xFFFFFF00);
        this.drawVerticalLine(240, 180, 220, 0xFFFFFF00);
        this.drawHorizontalLine(40, 240, 180, 0xFFFFFF00);
        this.drawHorizontalLine(40, 240, 220, 0xFFFFFF00);
        this.fontRenderer.drawString(Integer.toString(tile.getEnergyStored()), 115, 72, 4210752);
        
        if (maxEnergy != 0) {
            double filledPercent = energyStored / maxEnergy;

        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1, 1, 1);
        this.mc.getTextureManager().bindTexture(BG);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
    }
}
