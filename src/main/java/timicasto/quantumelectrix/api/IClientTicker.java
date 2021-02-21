package timicasto.quantumelectrix.api;

public interface IClientTicker {
    void clientTick();

    boolean needTicks();
}
