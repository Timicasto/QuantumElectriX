package timicasto.quantumelectrix.block.types;

import net.minecraft.util.math.AxisAlignedBB;

public class AxisTypeWire {
    public static final int NonRubber2A = 1;
    public static final int NonRubber10A = 2;

    public static int getMeta(String type) {
        if (type.equals("NonRubber2A")) {
            return NonRubber2A;
        } else if (type.equals("NonRubber10A")) {
            return NonRubber10A;
        }
        return 0;
    }

    public static AxisAlignedBB[] getBoundingBox(int meta) {
        switch (meta) {
            case NonRubber2A:
                AxisAlignedBB[] axis2 = new AxisAlignedBB[7];
                axis2[0] = new AxisAlignedBB(0.4375, 0, 0.4375, 0.5625, 0.125, 0.5625);
                axis2[1] = new AxisAlignedBB(0.4375, 0, 0.4375, 0.5625, 1, 0.5625);
                axis2[2] = new AxisAlignedBB(0.4375, 0, 0, 0.5625, 1, 0.4375);
                axis2[3] = new AxisAlignedBB(0.4375, 0, 0.5625, 0.5625, 0.125, 1);
                axis2[4] = new AxisAlignedBB(0, 0, 0.4375, 0.4375, 0.125, 0.5625);
                axis2[5] = new AxisAlignedBB(0.5625, 0, 0.4375, 1, 0.125, 0.5625);
                axis2[6] = new AxisAlignedBB(0.4375, 0, 0.4375, 0.5625, 0.125, 0.5625);
                return axis2;
            case NonRubber10A:
                AxisAlignedBB[] axis10 = new AxisAlignedBB[7];
                axis10[0] = new AxisAlignedBB(0.375, 0, 0.375, 0.625, 0.25, 0.625);
                axis10[1] = new AxisAlignedBB(0.375, 0, 0.375, 0.625, 1, 0.625);
                axis10[2] = new AxisAlignedBB(0.375, 0, 0, 0.625, 1, 0.375);
                axis10[3] = new AxisAlignedBB(0.375, 0, 0.625, 0.625, 0.25, 1);
                axis10[4] = new AxisAlignedBB(0, 0, 0.375, 0.375, 0.25, 0.625);
                axis10[5] = new AxisAlignedBB(0.625, 0, 0.375, 1, 0.25, 0.625);
                axis10[6] = new AxisAlignedBB(0.375, 0, 0.375, 0.625, 0.25, 0.625);
                return axis10;
        }
        return new AxisAlignedBB[] {};
    }

    public static AxisAlignedBB getDefaultBox(int meta) {
        switch (meta) {
            case NonRubber2A:
                return getBoundingBox(NonRubber2A)[6];
            case NonRubber10A:
                return getBoundingBox(NonRubber10A)[6];
        }
        return new AxisAlignedBB(0,0,0,0,0,0);
    }
}
