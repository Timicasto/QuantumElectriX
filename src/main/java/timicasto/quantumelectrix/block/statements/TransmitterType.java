package timicasto.quantumelectrix.block.statements;

import net.minecraft.util.IStringSerializable;
import timicasto.quantumelectrix.api.TransmittionType;

import java.util.Locale;

public enum TransmitterType implements IStringSerializable {
    ENERGY_CABLE("EnergyCable", Size.SMALL, TransmittionType.ENERGY, false, true);

    private String unlocalizedName;
    private Size size;
    private TransmittionType type;
    private boolean transparencyRenderer;
    public boolean tiers;

    TransmitterType(String name, Size sobj, TransmittionType trType, boolean transparency, boolean b) {
        unlocalizedName = name;
        size = sobj;
        type = trType;
        transparencyRenderer = transparency;
        tiers = b;
    }

    public static TransmitterType get(int meta) {
        return TransmitterType.values()[meta];
    }

    @Override
    public String getName() {
        return name().toLowerCase(Locale.ROOT);
    }

    public String getTranslationKey() {
        return unlocalizedName;
    }

    public Size getSize() {
        return size;
    }

    public boolean hasTransparency() {
        return transparencyRenderer;
    }

    public boolean hasTiers() {
        return tiers;
    }

    public enum Size {
        SMALL(6),
        LARGE(8);

        public int centerSize;
        Size(int size) {
            centerSize = size;
        }
    }

    public TransmittionType getTransmission() {
        return type;
    }
}
