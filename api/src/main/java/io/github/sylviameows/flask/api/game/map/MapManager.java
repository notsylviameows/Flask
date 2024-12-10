package io.github.sylviameows.flask.api.game.map;

import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.manager.Manager;
import io.github.sylviameows.flask.api.map.FlaskMap;
import io.github.sylviameows.flask.api.services.WorldService;
import io.github.sylviameows.flask.api.util.SchedulerUtil;
import io.github.sylviameows.flask.api.util.WorldProperties;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MapManager implements Manager<FlaskMap> {
    WorldService ws = FlaskAPI.instance().getWorldService();

    protected final Map<String, FlaskMap> map;
    protected final String prefix;

    public MapManager(Game game) {
        this(game.getKey().namespace()+"/"+game.getKey().value()+"/");
    }

    protected MapManager(String prefix) {
        this.prefix = prefix;
        this.map = new ConcurrentHashMap<>();
    }

    public CompletableFuture<SlimeWorld> getWorld(String id) {
        var promise = new CompletableFuture<SlimeWorld>();

        Bukkit.getScheduler().runTaskAsynchronously(FlaskAPI.instance().getPlugin(), () -> {
            try {
                var world = ws.readWorld(prefix+id, false, WorldProperties.defaultProperties());

                SchedulerUtil.runSyncAndWait(FlaskAPI.instance().getPlugin(), () -> promise.complete(ws.loadWorld(world)));
            } catch (CorruptedWorldException | IOException | NewerFormatException e) {
                promise.completeExceptionally(e);
                throw new RuntimeException(e);
            } catch (UnknownWorldException e) {
                ws.createWorldAsync(prefix+id).whenComplete((world, ex) -> {
                    if (ex != null || world == null) promise.completeExceptionally(ex);
                    promise.complete(world);
                });
            }

        });

        return promise;
    }

    public CompletableFuture<SlimeWorld> getWorld(FlaskMap map) {
        return getWorld(map.getId());
    }

    public FlaskMap add(FlaskMap map) {
        return add(map.getId(), map);
    }

    public FlaskMap add(String key, FlaskMap entry) {
        return map.put(key, entry);
    }

    public FlaskMap get(String key) {
        return map.get(key);
    }

    public FlaskMap remove(FlaskMap map) {
        return remove(map.getId());
    }

    public FlaskMap remove(String key) {
        return map.remove(key);
    }
}
