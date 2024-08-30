package io.github.sylviameows.duels;

import io.github.sylviameows.duels.basic.ExampleGame;
import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.FlaskPlugin;
import org.bukkit.Bukkit;

public final class Duels extends FlaskPlugin {
    private FlaskAPI flask;

    @Override
    public void onEnable() {
        // Plugin startup logic
        var plugin = Bukkit.getPluginManager().getPlugin("core");
        if (plugin instanceof FlaskAPI api) {
            getLogger().info("core");
            flask = api;
        } else {
            plugin = Bukkit.getPluginManager().getPlugin("flask");
            if (plugin instanceof FlaskAPI api) {
                getLogger().info("flask");
                flask = api;
            }
        }

        new ExampleGame(this).register("sword");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public FlaskAPI getFlaskAPI() {
        return flask;
    }
}
