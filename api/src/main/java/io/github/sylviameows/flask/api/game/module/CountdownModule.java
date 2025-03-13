package io.github.sylviameows.flask.api.game.module;

import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.game.Lobby;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountdownModule extends FlaskModule {
    private int ticksLeft;

    private final Map<Integer, List<Runnable>> actions = new HashMap<>();
    private Runnable tickAction = () -> {};

    public CountdownModule(int ticks) {
        this.ticksLeft = ticks;
    }

    @Override
    public void init(Lobby<?> lobby) {
        run();
    }

    @Override
    public void purge() {
        ticksLeft = -1;
    }

    public void run() {
        List<Runnable> listeners = actions.get(ticksLeft);
        if (listeners != null) {
            listeners.forEach(Runnable::run);
        }

        tickAction.run();

        if (ticksLeft >= 0) {
            ticksLeft--;
            Bukkit.getScheduler().runTaskLater(FlaskAPI.instance().getPlugin(), this::run, 1L);
        }
    }

    public CountdownModule at(int second, Runnable action) {
        int timestamp = second * 20;

        List<Runnable> listeners = actions.get(timestamp);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        listeners.add(action);
        actions.put(timestamp, listeners);

        return this;
    }

    public CountdownModule tick(Runnable action) {
        this.tickAction = action;

        return this;
    }

    public CountdownModule end(Runnable action) {
        List<Runnable> listeners = actions.get(0);
        if (listeners == null) {
            listeners = new ArrayList<>();
        }

        listeners.add(action);
        actions.put(0, listeners);

        return this;
    }

    public static CountdownModule seconds(int seconds) {
        return new CountdownModule(seconds * 20);
    }

    public static CountdownModule minutes(int minutes) {
        return seconds(minutes * 60);
    }
}
