package io.github.sylviameows.flask.listeners;

import io.github.sylviameows.flask.Flask;
import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.FlaskPlayer;
import io.github.sylviameows.flask.api.annotations.FlaskEvent;
import io.github.sylviameows.flask.api.events.FlaskDispatcher;
import io.github.sylviameows.flask.api.events.FlaskListener;
import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.game.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
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
import java.util.*;

public class FlaskDispatcherImpl implements FlaskDispatcher {
    Map<Class<? extends Event>, ArrayList<ListenerInfo>> methodMap = new HashMap<>();

    @Override
    public void registerEvent(Lobby<?> lobby, FlaskListener listener) {
        System.out.println("testing???");
        Flask.logger.info("registering event: "+listener.getClass().getName());
        for (Method method : listener.getClass().getMethods()) {
            Flask.logger.info(method.getName() + " | " + Arrays.toString(method.getAnnotations()));
            if (method.isAnnotationPresent(FlaskEvent.class)) {
                Flask.logger.info("found method with event annotation "+method.getName());
                Parameter[] parameters = method.getParameters();
                if (parameters.length == 1) {
                    Class<?> clazz = parameters[0].getType();

                    if (Event.class.isAssignableFrom(clazz)) {
                        Flask.logger.info("is event type");

                        //noinspection unchecked
                        Class<? extends Event> eventClass = (Class<? extends Event>) clazz;

                        methodMap.computeIfAbsent(eventClass, k -> {
                            Flask.logger.info("registering");
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
        }
    }

    @Override
    public void unregisterEvent(Lobby<?> lobby, FlaskListener listener) {
        methodMap.forEach((clazz, listeners) -> {
            Optional<ListenerInfo> optional = listeners.stream().filter(info -> (info.listener() == listener && info.lobby() == lobby)).findFirst();
            if (optional.isEmpty()) return;
            ListenerInfo info = optional.get();

            listeners.remove(info);
//            if (listeners.isEmpty()) todo(): remove empty listeners
        });
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
        Flask.logger.info("running code: "+event.getEventName());
        ArrayList<ListenerInfo> listeners = methodMap.get(event.getClass());
        if (listeners == null || listeners.isEmpty()) return;

        ListenerInfo info = null;
        switch (event) {
            case PlayerEvent playerEvent -> {
                Player player = playerEvent.getPlayer();
                FlaskPlayer fp = Flask.getInstance().getPlayerManager().get(player);

                Lobby<?> lobby = fp.getLobby();
                Optional<ListenerInfo> optional = listeners.stream().filter(i -> i.lobby() == lobby).findFirst();
                if (optional.isEmpty()) return;
                info = optional.get();
            }
            case EntityEvent entityEvent -> {
                Entity entity = entityEvent.getEntity();
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

        if (info == null) return;

        try {
            info.method().invoke(info.listener(), event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new EventException(e);
        }
    }

    private ListenerInfo findListenerMatchingWorld(ArrayList<ListenerInfo> listeners, World world) {
        Optional<ListenerInfo> optional = listeners.stream().filter(i -> i.lobby().getWorld() == world).findFirst();
        return optional.orElse(null);
    }
}
