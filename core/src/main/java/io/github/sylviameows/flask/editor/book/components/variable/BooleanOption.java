package io.github.sylviameows.flask.editor.book.components.variable;

import io.github.sylviameows.flask.api.Palette;
import io.github.sylviameows.flask.api.map.FlaskMap;
import io.github.sylviameows.flask.api.map.GameMap;
import io.github.sylviameows.flask.editor.book.EditorBook;
import io.github.sylviameows.flask.editor.book.components.button.BookButton;
import io.github.sylviameows.flask.editor.book.components.button.ButtonStyle;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;

public class BooleanOption extends BookOption {
    public BooleanOption(Field field, GameMap map) {
        super(field, map);
    }

    @Override
    public int lines() {
        return 3;
    }

    private Boolean get() throws IllegalAccessException {
        try {
            field.setAccessible(true);
            return field.getBoolean(map);
        } catch (NullPointerException e) {
            return null;
        }
    }

    protected Component value() {
        try {
            var value = get();

            if (value == null) {
                value = false;
                field.setBoolean(map, false);
            }

            var component = Component.text(value ? "True" : "False").color(Palette.DARK_GRAY);
            if (isOptional()) {
                var d = (boolean) getDefault();
                if (value == d) return component.append(Component.text("(default)").color(Palette.GRAY));
            }

            return component;
        } catch (Exception e) {
            return Component.text("* value fetch error *").color(Palette.RED_LIGHT);
        }
    }

    @Override
    protected Component buttons() {
        return BookButton.callback("toggle", "Toggles this option.", ButtonStyle.MODIFYING, audience -> {
            audience.sendMessage(Component.text("Toggled boolean value"));
            try {
                field.setAccessible(true);
                var value = field.getBoolean(map);
                field.setBoolean(map, !value);
            } catch (IllegalAccessException ignored) {
            }

            // get player editor session to reopen book TODO

            var filtered = audience.get(Identity.UUID);
            var player = Bukkit.getPlayer(filtered.get());
            if (player == null) return;

            // todo: reopen book
        }).label();
    }
}
