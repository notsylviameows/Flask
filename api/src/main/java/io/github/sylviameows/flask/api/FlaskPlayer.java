package io.github.sylviameows.flask.api;

import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.game.Lobby;
import org.jetbrains.annotations.ApiStatus;

public interface FlaskPlayer {
    void setGame(Game game);
    void setLobby(Lobby<?> lobby);

    Game getGame();
    Lobby<?> getLobby();

    void reset();

    boolean isOccupied();
    boolean isInQueue();
}
