package io.github.sylviameows.flask.editor.book.components.button;

import io.github.sylviameows.flask.api.Palette;
import io.github.sylviameows.flask.editor.book.components.BookComponent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public class BookButton implements BookComponent {
    private Component label;

    public BookButton(String label, ButtonStyle style, ClickEvent event) {
        this.label = Component.text(style.left() + label + style.right())
                .color(style.color())
                .clickEvent(event);
    }

    public BookButton(String label, String description, ButtonStyle style, ClickEvent event) {
        this.label = Component.text(style.left() + label + style.right())
                .color(style.color())
                .clickEvent(event)
                .hoverEvent(HoverEvent.showText(Component.text(description)));
    }

    public static BookButton callback(String label, String description, ButtonStyle style, ClickCallback<Audience> callback) {
        return new BookButton(label, description, style, ClickEvent.callback(callback));
    }

    public static BookButton callback(String label, ButtonStyle style, ClickCallback<Audience> callback) {
        return callback(label, "", style, callback);
    }

    public BookButton disable() {
        label = label.clickEvent(null);
        label = label.color(Palette.GRAY);
        return this;
    }

    @Override
    public Component label() {
        return label;
    }
}
