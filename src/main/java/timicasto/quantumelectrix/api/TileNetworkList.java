package timicasto.quantumelectrix.api;

import net.minecraft.util.NonNullList;
import org.apache.commons.lang3.Validate;
import scala.actors.threadpool.Arrays;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileNetworkList extends NonNullList<Object> {
    public TileNetworkList() {
        super(new ArrayList<>(), null);
    }

    public TileNetworkList(@Nonnull List<Object> contents) {
        super(contents, null);
        Validate.noNullElements(contents);
    }

    public static TileNetworkList withContents(@Nonnull Object... contents) {
        return new TileNetworkList(Arrays.asList(contents));
    }
}
