package io.github.sylviameows.flask.api;

import net.kyori.adventure.text.format.TextColor;

/**
 * Flask's custom palette, to create consistency.
 */
public final class Palette {
    public static final TextColor WHITE = TextColor.color(0xFFFFFF); // default
    public static final TextColor GRAY = TextColor.color(0xAAAAAA); // default
    public static final TextColor DARK_GRAY = TextColor.color(0x555555); // default
    public static final TextColor BLACK = TextColor.color(0x000000); // default

    public static final TextColor RED_DARK = TextColor.color(0xAA0000); // default
    public static final TextColor RED = TextColor.color(0xFF0000);
    public static final TextColor RED_LIGHT = TextColor.color(0xFF5555); // default

    public static final TextColor GOLD = TextColor.color(0xFFAA00); // default

    public static final TextColor YELLOW = TextColor.color(0xFFFF00); // default

    public static final TextColor LIME = TextColor.color(0xAAFF55);

    public static final TextColor GREEN_DARK = TextColor.color(0x00AA00); // default
    public static final TextColor GREEN = TextColor.color(0x55FF55); // default

    public static final TextColor MINT = TextColor.color(0x87ffdf);

    public static final TextColor AQUA_DARK = TextColor.color(0x00AAAA); // default
    public static final TextColor AQUA = TextColor.color(0x55FFFF); // default

    public static final TextColor BLUE_DARK = TextColor.color(0x0000AA); // default
    public static final TextColor BLUE = TextColor.color(0x5555FF); // default

    public static final TextColor PURPLE = TextColor.color(0xAA00AA); // default
    public static final TextColor PURPLE_LIGHT = TextColor.color(0xFF55FF); // default

    private Palette() {}
}
