package io.github.sylviameows.duels;

import io.github.sylviameows.flask.api.FlaskPlugin;
import io.github.sylviameows.flask.api.annotations.GameProperties;
import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.game.Lobby;
import io.github.sylviameows.flask.api.game.Phase;
import io.github.sylviameows.flask.api.game.map.MapManager;
import io.github.sylviameows.flask.api.map.FlaskMap;
import org.bukkit.entity.Player;

import java.util.List;

@GameProperties(
        name="testing",
        min=1,
        max=2
)
public class TestingGame extends Game<FlaskMap> {
    protected TestingGame(FlaskPlugin plugin) {
        super(plugin);
    }

    @Override
    public Lobby<?> createLobby(List<Player> players) {
        return new Lobby<TestingGame>(this);
    }

    @Override
    public Phase initialPhase() {
        return new TestingPhase();
    }

    @Override
    public MapManager<FlaskMap> getMapManager() {
        return null;
    }
}
