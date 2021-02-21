package timicasto.quantumelectrix.api;

public class ResourceTools {

    public static float[] getColorArray(float pos) {
        if (pos < 85) {
            return new float[] {pos * 3, 255 - pos * 3, 0};
        }
        if (pos < 170) {
            return new float[] {255 - (pos -= 85) * 3, 0, pos * 3};
        }
        return new float[] {0, (pos -= 170) * 3, 255 - pos * 3};
    }
}
