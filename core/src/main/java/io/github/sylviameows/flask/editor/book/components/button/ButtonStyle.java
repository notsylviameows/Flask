package io.github.sylviameows.flask.editor.book.components.button;

import io.github.sylviameows.flask.api.Palette;
import net.kyori.adventure.text.format.TextColor;

public class ButtonStyle {
    public final TextColor color;
    public final String leftBorder;
    public final String rightBorder;

    public ButtonStyle(TextColor color, String border) {
        this.color = color;

        if (border.length() == 1) {
            this.leftBorder = border;
            this.rightBorder = border;
        } else if (!border.isEmpty() && border.length() % 2 == 0) {
            this.leftBorder = border.substring(0, border.length() / 2);
            this.rightBorder = border.substring(border.length() / 2);
        } else {
            this.leftBorder = "";
            this.rightBorder = "";
        }
    }

    public static ButtonStyle CONSTRUCTIVE = new ButtonStyle(Palette.GREEN_DARK, "[]");
    public static ButtonStyle MODIFYING = new ButtonStyle(Palette.AQUA_DARK, "[]");
    public static ButtonStyle DESTRUCTIVE = new ButtonStyle(Palette.RED_DARK, "{}");

}
