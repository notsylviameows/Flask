package io.github.sylviameows.flask.listeners;

import io.github.sylviameows.flask.Flask;
import io.github.sylviameows.flask.api.FlaskPlayer;
import io.github.sylviameows.flask.api.annotations.FlaskEvent;
import io.github.sylviameows.flask.api.events.FlaskDispatcher;
import io.github.sylviameows.flask.api.events.FlaskListener;
import io.github.sylviameows.flask.api.game.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.vehicle.VehicleEvent;
import org.bukkit.event.world.WorldEvent;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class FlaskDispatcherImpl implements FlaskDispatcher {
    private final Map<Class<? extends Event>, List<ListenerInfo>> methodMap = new HashMap<>();

    @Override
    public void registerEvent(Lobby<?> lobby, FlaskListener listener) {
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(FlaskEvent.class)) {
                continue;
            }

            Parameter[] parameters = method.getParameters();
            if (parameters.length == 1) {
                Class<?> clazz = parameters[0].getType();
                if (!Event.class.isAssignableFrom(clazz)) {
                    continue;
                }

                //noinspection unchecked
                Class<? extends Event> eventClass = (Class<? extends Event>) clazz;
                method.setAccessible(true);

                methodMap.computeIfAbsent(eventClass, k -> {
                    Bukkit.getPluginManager().registerEvent(
                            eventClass,
                            this,
                            EventPriority.HIGH,
                            this,
                            Flask.getInstance()
                    );

                    return new ArrayList<>();
                }).add(new ListenerInfo(lobby, method, listener));
            }
        }
    }

    @Override
    public void unregisterEvent(Lobby<?> lobby, FlaskListener listener) {
        for (Map.Entry<Class<? extends Event>, List<ListenerInfo>> entry : methodMap.entrySet()) {
            Class<? extends Event> clazz = entry.getKey();
            List<ListenerInfo> listeners = entry.getValue();

            Optional<ListenerInfo> optional = listeners.stream().filter(
                    info -> info.listener() == listener && info.lobby() == lobby
            ).findFirst();
            if (optional.isEmpty()) {
                continue;
            }
            ListenerInfo info = optional.get();

            listeners.remove(info);
            if (!listeners.isEmpty()) {
                return;
            }

            // unregister event when no lobby needs to listen to it.
            methodMap.remove(clazz);
            try {
                Method method = clazz.getMethod("getHandlerList");
                Object object = method.invoke(null);
                if (object instanceof HandlerList handlerList) {
                    handlerList.unregister(this);
                }
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                Flask.logger().warn("Could not unregister listener for event type " + clazz.getName());
            }
        }
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        List<ListenerInfo> listeners = new ArrayList<>();

        Class<? extends Event> clazz = event.getClass();
        methodMap.forEach((e, i) -> {
            if (e.isAssignableFrom(clazz)) {
                listeners.addAll(i);
            }
        });
        if (listeners.isEmpty()) {
            return;
        }

        ListenerInfo info = null;
        switch (event) {
            case PlayerEvent playerEvent -> {
                Player player = playerEvent.getPlayer();
                info = findListenerMatchingPlayer(listeners, player);
            }
            case EntityEvent entityEvent -> {
                Entity entity = entityEvent.getEntity();
                if (entity instanceof Player player) {
                    info = findListenerMatchingPlayer(listeners, player);
                    break;
                }

                World world = entity.getWorld();
                info = findListenerMatchingWorld(listeners, world);
            }
            case WorldEvent worldEvent -> {
                World world = worldEvent.getWorld();
                info = findListenerMatchingWorld(listeners, world);
            }
            case BlockEvent blockEvent -> {
                World world = blockEvent.getBlock().getWorld();
                info = findListenerMatchingWorld(listeners, world);
            }
            case VehicleEvent vehicleEvent -> {
                World world = vehicleEvent.getVehicle().getWorld();
                info = findListenerMatchingWorld(listeners, world);
            }
            default -> {
            }
        }

        if (info == null) {
            return;
        }

        try {
            info.method().invoke(info.listener(), event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new EventException(e);
        }
    }

    private ListenerInfo findListenerMatchingPlayer(List<ListenerInfo> listeners, Player player) {
        FlaskPlayer fp = Flask.getInstance().getPlayerManager().get(player);

        Lobby<?> lobby = fp.getLobby();
        for (ListenerInfo i : listeners) {
            if (i.lobby() == lobby) {
                return i;
            }
        }
        return null;
    }

    private ListenerInfo findListenerMatchingWorld(List<ListenerInfo> listeners, World world) {
        for (ListenerInfo info : listeners) {
            if (info.lobby().getWorld().getName().equals(world.getName())) {
                return info;
            }
        }
        return null;
    }
}
