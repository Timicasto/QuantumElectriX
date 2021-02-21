package timicasto.quantumelectrix.api;

import net.minecraft.client.resources.I18n;

public enum Colors {
    ICE_BLUE("ice_blue", new int[]{102, 244, 255}),
    SAKURA("sakura", new int[]{254, 223, 225}),
    SOLARIZED_LIGHT("solarized_light", new int[]{253, 246, 227}),
    AQUAMARINE("aquamarine", new int[]{28, 238, 210}),
    LTCAT_BLUE("ltcat_blue", new int[]{17, 111, 206});

    public String name;
    public int[] color;

    Colors(String s, int[] c) {
        name = s;
        color = c;
    }

    public static Colors getFromName(String name) {
        for (Colors c : values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public String getLocalizedName(String unlocalizedName) {
        return I18n.format("color." + unlocalizedName);
    }
}
