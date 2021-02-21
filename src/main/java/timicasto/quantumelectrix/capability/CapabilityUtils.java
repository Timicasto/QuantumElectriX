package timicasto.quantumelectrix.capability;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public final class CapabilityUtils {
    public static boolean hasCapability(ICapabilityProvider provider, Capability<?> capability, EnumFacing facing) {
        if (provider == null || capability == null) {
            return false;
        }
        return provider.hasCapability(capability, facing);
    }

    public static <T> T getCapability(ICapabilityProvider provider, Capability<T> capability, EnumFacing facing) {
        if (provider == null || capability == null) {
            return null;
        }
        return provider.getCapability(capability, facing);
    }


}
