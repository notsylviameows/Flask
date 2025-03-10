package io.github.sylviameows.flask.api.events;

import io.github.sylviameows.flask.api.game.Lobby;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

public interface FlaskDispatcher extends EventExecutor, Listener {
    record ListenerInfo(Lobby<?> lobby, Method method, FlaskListener listener) {}

    void registerEvent(Lobby<?> lobby, FlaskListener listener);
    void unregisterEvent(Lobby<?> lobby, FlaskListener listener);

    @Override
    void execute(@NotNull Listener listener, @NotNull Event event) throws EventException;
}
