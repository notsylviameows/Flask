package io.github.sylviameows.duels.basic;

import io.github.sylviameows.flask.api.Palette;
import io.github.sylviameows.flask.api.annotations.FlaskEvent;
import io.github.sylviameows.flask.api.game.Lobby;
import io.github.sylviameows.flask.api.game.Phase;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.time.Duration;

public class ExampleEndingPhase implements Phase {
    private Player winner;

    private Lobby<?> parent;

    public void setWinner(Player player) {
        this.winner = player;
    }

    @Override
    public void onEnabled(Lobby<?> lobby) {
        this.parent = lobby;

        lobby.getPlayers().forEach(this::ending);

        // close lobby after 5.
        Bukkit.getScheduler().runTaskLater(lobby.getParent().getPlugin(), () -> {
            lobby.closeLobby(player -> {
                player.setGameMode(GameMode.ADVENTURE);
            });
        }, 100L);
    }

    private void ending(Player player) {
        player.getInventory().clear();

        Component prefix = Component.text("SYSTEM › ").color(parent.getParent().getSettings().getColor());
        player.sendMessage(prefix
                .append(Component.text(winner.getName()).color(Palette.WHITE))
                .append(Component.text(" won the duel!").color(Palette.GRAY))
        );

        player.playSound(Sound.sound(Key.key("entity.arrow.hit_player"), Sound.Source.MASTER, 1f, 1.6f));

        Component title;
        if (player == winner) {
            title = Component.text("YOU WON")
                    .color(Palette.LIME);
        } else {
            title = Component.text("YOU LOST")
                    .color(Palette.RED_LIGHT);
        }

        player.showTitle(Title.title(
                title.decorate(TextDecoration.BOLD),
                Component.empty(),
                Title.Times.times(
                        Duration.ZERO,
                        Duration.ofSeconds(2),
                        Duration.ofSeconds(1)
                )
        ));
    }

    @Override
    public void onDisabled() {
        this.parent.getPlayers().forEach(player -> {
            player.setGameMode(GameMode.ADVENTURE);

            var lobby = Bukkit.getWorld("world");
            player.teleport(new Location(lobby, 0.0, -60.0, 0.0));
        });

        var world = this.parent.getWorld();
        if (world != null) {
            Bukkit.unloadWorld(world.getName(), false);
        }
    }

    @FlaskEvent
    public void damage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            event.setCancelled(true);
        }
    }

    @Override
    public Phase next() {
        return null;
    }
}
