package io.github.sylviameows.flask.api.game.phase;

import io.github.sylviameows.flask.api.game.Lobby;
import io.github.sylviameows.flask.api.game.module.FlaskModule;

import java.util.ArrayList;
import java.util.List;

abstract public non-sealed class ModularPhase implements Phase {
    private final List<FlaskModule> modules = new ArrayList<>();
    private Lobby<?> lobby;

    public void addModule(FlaskModule module) {
        modules.add(module);
    }

    protected void enabled() {}

    protected void disabled() {}

    @Override
    public final void onEnabled(Lobby<?> parent) {
        lobby = parent;
        modules.forEach(module -> module.init(lobby));
        enabled();
    }

    @Override
    public final void onDisabled() {
        modules.forEach(FlaskModule::purge);
        disabled();
    }

    protected Lobby<?> getLobby() {
        return lobby;
    }
}
