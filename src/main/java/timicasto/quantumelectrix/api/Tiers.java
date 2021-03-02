package timicasto.quantumelectrix.api;

import net.minecraft.util.IStringSerializable;

public enum Tiers implements IStringSerializable {
    max2A(2, Colors.ICE_BLUE),
    max10A(10, Colors.SAKURA);


    public int mC;
    public Colors color;
    Tiers(int maxCurrent, Colors colors) {
        mC = maxCurrent;
        color = colors;
    }

    public int getMaxCurrent() {
        return mC;
    }

    public Colors getColor() {
        return color;
    }

    @Override
    public String getName() {
        return "tier";
    }
}
