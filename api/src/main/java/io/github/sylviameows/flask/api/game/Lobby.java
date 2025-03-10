package io.github.sylviameows.flask.api.game;

import io.github.sylviameows.flask.api.FlaskAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Lobby<G extends Game<?>> {
    protected final G parent;

    public List<Player> players;
    private @NotNull Phase phase;

    private World world;

    public Lobby(G parent) {
        this(parent, new ArrayList<>());
    }

    public Lobby(G parent, List<Player> players) {
        this.parent = parent;
        this.players = players;

        FlaskAPI api = parent.getPlugin().getFlaskAPI();
        players.forEach(player -> api.getPlayerManager().get(player).setLobby(this));

        this.phase = parent.initialPhase();
        api.getPlugin().getLogger().info("registering");
        api.getDispatcher().registerEvent(this, this.phase);
        this.phase.onEnabled(this);
    }

    // todo: call function in phase
    public void addPlayer(Player player) {
        FlaskAPI api = parent.getPlugin().getFlaskAPI();
        api.getPlayerManager().get(player).setLobby(this);
        players.add(player);
        phase.onPlayerJoin(player);
    }

    public void removePlayer(Player player) {
        FlaskAPI api = parent.getPlugin().getFlaskAPI();
        api.getPlayerManager().get(player).setLobby(null);
        players.remove(player);
        phase.onPlayerLeave(player);
    }

    public void closeLobby() {
        phase.onDisabled();

        FlaskAPI.instance().getPlugin().getLogger().info("unregistering");
        FlaskAPI.instance().getDispatcher().unregisterEvent(this, phase);

        // todo: replace with a requeue feature?
        players.forEach(player -> {
            parent.getQueue().removePlayer(player);
            player.teleportAsync(FlaskAPI.instance().getSpawnLocation());
            player.setGameMode(GameMode.ADVENTURE);
            player.setHealth(20.0);
            player.setSaturation(10f);
            player.setFoodLevel(20);
        });

        Bukkit.getScheduler().runTaskLater(FlaskAPI.instance().getPlugin(), () -> {
            Bukkit.unloadWorld(world, false);
        }, 200L);
    }

    public void closeLobby(Consumer<Player> consumer) {
        players.forEach(consumer);
        closeLobby();
    }


    public Phase getPhase() {
        return phase;
    }
    public void updatePhase(@NotNull Phase phase) {
        this.phase.onDisabled();
        FlaskAPI.instance().getPlugin().getLogger().info("unregistering");
        FlaskAPI.instance().getDispatcher().unregisterEvent(this, phase);

        this.phase = phase;

        this.phase.onEnabled(this);
        FlaskAPI.instance().getPlugin().getLogger().info("registering");
        FlaskAPI.instance().getDispatcher().registerEvent(this, phase);
    }

    public void nextPhase() {
        Phase nextPhase = phase.next();
        if (nextPhase == null) {
            parent.getPlugin().getComponentLogger().error("Next phase is not defined, aborting next phase action...");
            return;
        }
        updatePhase(nextPhase);
    }

    public G getParent() {
        return parent;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
