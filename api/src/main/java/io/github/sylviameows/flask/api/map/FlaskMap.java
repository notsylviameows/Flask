package io.github.sylviameows.flask.api.map;

import io.github.sylviameows.flask.api.annotations.MapProperty;
import org.bukkit.Location;

public class FlaskMap {
    public final String id;

    public FlaskMap(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @MapProperty(name = "Lobby Spawn", description = "The location a player will spawn while waiting for the game to start.")
    Location waiting;

    @MapProperty(name = "Allow Spectators", description = "When enabled, players will be allowed to spectate on this map.")
    boolean spectators = true;
}
