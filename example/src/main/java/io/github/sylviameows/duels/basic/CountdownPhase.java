package io.github.sylviameows.duels.basic;

import io.github.sylviameows.duels.basic.modules.MovementModule;
import io.github.sylviameows.flask.api.game.module.CountdownModule;
import io.github.sylviameows.flask.api.game.module.DamageModule;
import io.github.sylviameows.flask.api.game.phase.ModularPhase;
import io.github.sylviameows.flask.api.game.phase.Phase;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

public class CountdownPhase extends ModularPhase {
    private final Player playerA;
    private final Player playerB;

    public CountdownPhase(Player playerA, Player playerB) {
        this.playerA = playerA;
        this.playerB = playerB;

        addModule(new MovementModule());
        addModule(CountdownModule.seconds(5)
                .at(3, () -> sendMessage(3))
                .at(2, () -> sendMessage(2))
                .at(1, () -> sendMessage(1))
                .end(() -> getLobby().nextPhase())
                .tick(() -> {
                    playerA.sendMessage("test");
                })
        );
        addModule(DamageModule.invulnerable());
    }

    private void sendMessage(int time) {
        playerA.sendMessage(Component.text(time));
        playerB.sendMessage(Component.text(time));
    }

    @Override
    public Phase next() {
        return new ExamplePlayingPhase(playerA, playerB);
    }
}
