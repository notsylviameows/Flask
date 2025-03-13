package io.github.sylviameows.duels.basic;

import io.github.sylviameows.flask.api.annotations.FlaskEvent;
import io.github.sylviameows.flask.api.game.phase.ListenerPhase;
import io.github.sylviameows.flask.api.game.phase.Phase;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

public class ExamplePlayingPhase extends ListenerPhase {
    private final Player playerA;
    private final Player playerB;

    private final @NotNull ExampleEndingPhase nextPhase;

    public ExamplePlayingPhase(Player playerA, Player playerB) {
        this.playerA = playerA;
        this.playerB = playerB;

        this.nextPhase = new ExampleEndingPhase();
    }

    @Override
    public void onPlayerLeave(Player player) {
        if (player == playerA) {
            nextPhase.setWinner(playerB);
        } else {
            nextPhase.setWinner(playerA);
        }
        getLobby().nextPhase();
    }

    @FlaskEvent
    public void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player == playerA) {
                nextPhase.setWinner(playerB);
                getLobby().nextPhase();
            } else if (player == playerB) {
                nextPhase.setWinner(playerA);
                getLobby().nextPhase();
            } else {
                return;
            }
            event.setCancelled(true);
            player.setHealth(20.0);
            player.setGameMode(GameMode.SPECTATOR);
        }
    }

    @Override
    public Phase next() {
        return nextPhase;
    }
}
