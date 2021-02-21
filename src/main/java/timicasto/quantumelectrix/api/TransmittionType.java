package timicasto.quantumelectrix.api;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.translation.I18n;
import timicasto.quantumelectrix.api.ints.IWire;

public enum TransmittionType {
    ENERGY("EnergyNetwork", "Energy"),
    FLUID("FluidNetwork", "Fluids"),
    ITEM("InventoryNetwork", "Items"),
    HEAT("HeatNetwork", "Heat");

    private String name;
    private String transmission;

    TransmittionType(String name, String transmission) {
        this.name = name;
        this.transmission = transmission;
    }

    public static boolean checkTransmissionType(IWire tile, TransmittionType type) {
        return type.checkTransmissionType(tile);
    }

    public static boolean checkTransmissionType(TileEntity tile, TransmittionType type) {
        return checkTransmissionType(tile, type, null);
    }

    public static boolean checkTransmissionType(TileEntity tile, TransmittionType type, TileEntity tile1) {
        return type.checkTransmissionType(tile, tile1);
    }

    public boolean checkTransmissionType(IWire wire) {
        return wire.getTransmissionType() == this;
    }

    public boolean checkTransmissionType(TileEntity tile, TileEntity current) {
        return tile instanceof IWire && ((IWire)tile).getTransmissionType() == this;
    }

    public String getName() {
        return name;
    }

    public String localize() {
        return I18n.translateToLocal(getTranslationKey());
    }

    public String getTranslationKey() {
        return "transmission." + getTransmission();
    }

    public String getTransmission() {
        return transmission;
    }
}
