package timicasto.quantumelectrix.network;

import timicasto.quantumelectrix.network.wrapper.Vector3B;

public class FlagData {
    public static FlagData DEFAULT = new FlagData(48, 32);
    private int height;
    private int width;
    private byte[][][] color;

    public FlagData(int width, int height) {
        this.height = height;
        this.width = width;
        this.color = new byte[width][height][3];

        for (int i = 0 ; i < width ; i++) {
            for (int j = 0 ; j < height; j++) {
                this.color[i][j][0] = 127;
                this.color[i][j][1] = 127;
                this.color[i][j][1] = 127;
            }
        }
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Vector3B getColorAt(int posX, int posY) {
        if (posX >= this.width || posY >= this.height) {
            return new Vector3B(0, 0, 0);
        } return new Vector3B((this.color[posX][posY][0] + 128) / 256D, (this.color[posX][posY][1] + 128) / 256D, (this.color[posX][posY][2] + 128) / 256D);
    }

    public void setColorAt(int posX, int posY, Vector3B vec) {
        this.color[posX][posY][0] = (byte) (vec.intX() - 128);
        this.color[posX][posY][1] = (byte) (vec.intY() - 128);
        this.color[posX][posY][2] = (byte) (vec.intZ() - 128);
    }
}
