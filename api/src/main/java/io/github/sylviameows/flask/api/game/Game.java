package io.github.sylviameows.flask.api.game;

import io.github.sylviameows.flask.api.FlaskPlugin;
import io.github.sylviameows.flask.api.game.map.MapManager;
import io.github.sylviameows.flask.api.map.FlaskMap;
import io.github.sylviameows.flask.api.map.GameMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

public abstract class Game<T extends GameMap> {
    private final FlaskPlugin plugin;
    private final Settings<T> settings;
    private final Queue<?> queue;

    private NamespacedKey key;

    protected Game(FlaskPlugin plugin, Settings<T> settings) {
        this.plugin = plugin;
        this.settings = settings;

        this.queue = new Queue<>(this);
    }

    @ApiStatus.Experimental
    protected Game(FlaskPlugin plugin) {
        this.plugin = plugin;
        this.settings = Settings.from(this);

        this.queue = new Queue<>(this);
    }

    public boolean register(String key) {
        if (this.key != null) return false;
        this.key = new NamespacedKey(plugin, key);
        plugin.getFlaskAPI().getGameRegistry().add(this);
        return true;
    }

    abstract public Lobby<?> createLobby(List<Player> players);
    abstract public Phase initialPhase();

    abstract public MapManager<T> getMapManager();

    public Settings<T> getSettings() {
        return settings;
    }
    public Queue<?> getQueue() {
        return queue; 
    }
    public NamespacedKey getKey() {
        return key;
    }
    public FlaskPlugin getPlugin() {
        return plugin;
    }
}
