package timicasto.quantumelectrix.api.ints;

import net.minecraft.util.EnumFacing;

public interface IBlockableConnect {
    boolean canMutualConnect(EnumFacing facing);
    boolean canConnect(EnumFacing facing);
}
