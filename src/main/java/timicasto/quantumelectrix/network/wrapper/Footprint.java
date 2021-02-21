package timicasto.quantumelectrix.network.wrapper;

public class Footprint {
    public static final short MAX_AGE = 6400;
    public final int dim;
    public final float rot;
    public final Vector3B pos;
    public short age;
    public final String igniter;
    public int lightVal;

    public Footprint(int dim, Vector3B pos, float rot, String uuid, int lightVal) {
        this(dim, pos, rot, (short)0, uuid, lightVal);
    }

    public Footprint(int dim, Vector3B pos, float rot, short age, String uuid, int lightVal) {
        this.dim = dim;
        this.pos = pos;
        this.rot = rot;
        this.age = age;
        this.igniter = uuid;
        this.lightVal = lightVal;
    }
}
