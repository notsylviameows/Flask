package io.github.sylviameows.flask.api.game.phase;

import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.events.FlaskDispatcher;
import io.github.sylviameows.flask.api.events.FlaskListener;
import io.github.sylviameows.flask.api.game.Lobby;

abstract public non-sealed class ListenerPhase implements Phase, FlaskListener {
    private final FlaskDispatcher dispatcher = FlaskAPI.instance().getDispatcher();

    private Lobby<?> lobby;

    protected void enabled() {}

    protected void disabled() {}

    @Override
    public final void onEnabled(Lobby<?> parent) {
        this.lobby = parent;
        dispatcher.registerEvent(lobby, this);

        enabled();
    }

    @Override
    public final void onDisabled() {
        dispatcher.unregisterEvent(lobby, this);

        disabled();
    }

    protected Lobby<?> getLobby() {
        return lobby;
    }
}
