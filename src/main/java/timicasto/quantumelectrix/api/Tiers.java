package timicasto.quantumelectrix.api;

public enum Tiers {
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
}
