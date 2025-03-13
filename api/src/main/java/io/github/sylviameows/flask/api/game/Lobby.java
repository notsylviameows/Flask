package io.github.sylviameows.flask.api.game;

import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.game.phase.Phase;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Lobby<G extends Game<?>> {
    private final G parent;

    private List<Player> players;
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
        this.phase.onEnabled(this);
    }

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
        closeLobby(player -> {});
    }

    public void closeLobby(Consumer<Player> consumer) {
        phase.onDisabled();

        players.forEach(player -> {
            resetPlayer(player);
            consumer.accept(player);
        });

        Bukkit.getScheduler().runTaskLater(FlaskAPI.instance().getPlugin(), () -> {
            Bukkit.unloadWorld(world, false);
        }, 200L /* 10 SECONDS */);
    }

    // todo: add a requeue feature?
    public void resetPlayer(Player player) {
        parent.getQueue().removePlayer(player);
        player.teleportAsync(FlaskAPI.instance().getSpawnLocation());
        player.setGameMode(GameMode.ADVENTURE);

        player.heal(Integer.MAX_VALUE);
        player.setSaturation(10f);
        player.setFoodLevel(20);
    }

    public void setPhase(@NotNull Phase phase) {
        this.phase = phase;
    }

    public @NotNull Phase getPhase() {
        return phase;
    }

    public void updatePhase(@NotNull Phase newPhase) {
        this.phase.onDisabled();
        this.phase = newPhase;
        this.phase.onEnabled(this);
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

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
