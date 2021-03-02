package timicasto.quantumelectrix.api.ints;

public interface IActiveState {
    boolean getActive();

    void setActive(boolean active);

    default boolean activeRecently() {
        return getActive();
    }

    boolean renderUpdate();
    boolean lightUpdate();
}
