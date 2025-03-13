package io.github.sylviameows.flask.api.game;

import io.github.sylviameows.flask.api.events.FlaskListener;
import org.bukkit.entity.Player;

public interface Phase extends FlaskListener {
    /* TODO */

    void onEnabled(Lobby<?> parent);

    void onDisabled();

    default void onPlayerJoin(Player player) {}

    default void onPlayerLeave(Player player) {}

    Phase next();
}
