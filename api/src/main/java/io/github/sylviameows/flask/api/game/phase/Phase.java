package io.github.sylviameows.flask.api.game.phase;

import io.github.sylviameows.flask.api.events.FlaskListener;
import io.github.sylviameows.flask.api.game.Lobby;
import org.bukkit.entity.Player;

sealed public interface Phase extends FlaskListener permits ListenerPhase, ModularPhase {
    void onEnabled(Lobby<?> parent);

    void onDisabled();

    default void onPlayerJoin(Player player) {}

    default void onPlayerLeave(Player player) {}

    Phase next();
}
