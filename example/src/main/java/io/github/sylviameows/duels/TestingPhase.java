package io.github.sylviameows.duels;

import io.github.sylviameows.flask.api.annotations.FlaskEvent;
import io.github.sylviameows.flask.api.game.Lobby;
import io.github.sylviameows.flask.api.game.Phase;
import org.bukkit.event.player.PlayerDropItemEvent;

public class TestingPhase implements Phase {
    @FlaskEvent
    public void dropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @Override
    public void onEnabled(Lobby<?> parent) {

    }

    @Override
    public void onDisabled() {

    }

    @Override
    public Phase next() {
        return null;
    }

}
