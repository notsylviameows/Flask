package io.github.sylviameows.flask.players;

import io.github.sylviameows.flask.api.FlaskPlayer;
import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.game.Lobby;
import io.github.sylviameows.flask.editor.EditorSession;
import io.github.sylviameows.flask.managers.PlayerManagerImpl;
import org.bukkit.entity.Player;

public class FlaskPlayerImpl implements FlaskPlayer {
    private Game game = null;
    private Lobby<?> lobby = null;
    private EditorSession session;

    public FlaskPlayerImpl(Player player) {
        if (!PlayerManagerImpl.instance().has(player)) {
            PlayerManagerImpl.instance().add(player.getUniqueId().toString(), this);
        }
    }

    public void setGame(Game game) {
        this.game = game;
    }
    public void setLobby(Lobby lobby) {
        this.lobby = lobby;
    }
    public void setSession(EditorSession session) {
        this.session = session;
    }

    public Game getGame() {
        return game;
    }
    public Lobby<?> getLobby() {
        return lobby;
    }
    public EditorSession getSession() {
        return session;
    }

    public void reset() {
        this.game = null;
        this.lobby = null;
    }

    public boolean isOccupied() {
        return game != null || session != null;
    }
    public boolean isInQueue() {
        return game != null && lobby == null;
    }
}
