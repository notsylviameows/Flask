package io.github.sylviameows.flask.api.game.module;

import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.events.FlaskDispatcher;
import io.github.sylviameows.flask.api.events.FlaskListener;
import io.github.sylviameows.flask.api.game.Lobby;

public abstract class FlaskModule implements FlaskListener {
    protected static final FlaskDispatcher dispatcher = FlaskAPI.instance().getDispatcher();
    private Lobby<?> owner;

    public void init(Lobby<?> lobby) {
        this.owner = lobby;
        dispatcher.registerEvent(owner, this);
    }

    public void purge() {
        dispatcher.unregisterEvent(owner, this);
    }

    protected Lobby<?> getOwner() {
        return owner;
    }
}
