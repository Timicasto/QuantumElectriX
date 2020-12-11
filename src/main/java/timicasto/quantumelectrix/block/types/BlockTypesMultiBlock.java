package timicasto.quantumelectrix.block.types;

import net.minecraft.util.IStringSerializable;
import org.apache.logging.log4j.LogManager;
import timicasto.quantumelectrix.block.BlockMultiblock;

import java.util.Locale;

public enum BlockTypesMultiBlock implements IStringSerializable, BlockMultiblock.IBlockEnum {
    CONTACT_CHAMBER(false);

    private boolean customState;
    BlockTypesMultiBlock(boolean customState) {
        this.customState = customState;
    }

    @Override
    public String getName() {
        return this.toString().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public int getMeta() {
        return ordinal();
    }

    @Override
    public boolean creativeList() {
        return false;
    }

    public boolean needCustomState() {
        return this.customState;
    }

    public String getCustomState() {
        return getName().toLowerCase();
    }
}
