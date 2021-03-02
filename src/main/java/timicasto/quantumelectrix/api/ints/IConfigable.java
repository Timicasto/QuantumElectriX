package timicasto.quantumelectrix.api.ints;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;

public interface IConfigable {
    EnumActionResult onActiveWithShift(EntityPlayer player, EnumFacing facing);
    EnumActionResult onActive(EntityPlayer player, EnumFacing facing);
}
