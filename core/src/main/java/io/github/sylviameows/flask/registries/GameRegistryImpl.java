package io.github.sylviameows.flask.registries;

import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.registry.GameRegistry;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to manage and load games from multiple plugin sources.
 */
public class GameRegistryImpl extends RegistryImpl<Game<?>> implements GameRegistry {
    private static final GameRegistryImpl instance = new GameRegistryImpl();

    public static GameRegistryImpl instance() {
        return instance;
    }

    @Override
    public Game<?> add(Game<?> game) {
        if (game.getIdentifier() == null) {
            return null;
        }
        return map().put(game.getIdentifier(), game);
    }

    @Override
    public Game<?> add(NamespacedKey key, Game<?> entry) {
        return super.add(key, entry);
    }

    @Override
    public Game<?> add(Plugin plugin, String key, Game<?> entry) {
        return super.add(plugin, key, entry);
    }

    public Game<?> remove(Game<?> game) {
        if (game.getIdentifier() == null) {
            return null;
        }
        return map().remove(game.getIdentifier());
    }

    public List<NamespacedKey> keys() {
        return new ArrayList<>(map().keySet());
    }

    public Game<?> findByName(String name) {
        for (NamespacedKey key : map().keySet()) {
            if (key.value().equals(name)) {
                return get(key);
            }
        }
        return null;
    }
}
