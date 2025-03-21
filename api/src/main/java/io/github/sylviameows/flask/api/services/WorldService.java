package io.github.sylviameows.flask.api.services;

import com.infernalsuite.aswm.api.AdvancedSlimePaperAPI;
import com.infernalsuite.aswm.api.exceptions.CorruptedWorldException;
import com.infernalsuite.aswm.api.exceptions.NewerFormatException;
import com.infernalsuite.aswm.api.exceptions.UnknownWorldException;
import com.infernalsuite.aswm.api.world.SlimeWorld;
import com.infernalsuite.aswm.api.world.properties.SlimePropertyMap;
import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.util.SchedulerUtil;
import io.github.sylviameows.flask.api.util.WorldProperties;
import org.bukkit.Bukkit;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public interface WorldService {
    AdvancedSlimePaperAPI slime = AdvancedSlimePaperAPI.instance();
    FlaskAPI flask = FlaskAPI.instance();

    /**
     * Generates a temporary world that is an exact copy of the given "template" world. This world will not be saved.
     *
     * @param template the world to copy.
     * @param name     the name to give the clone.
     * @return the created clone.
     */
    default SlimeWorld useTemplate(SlimeWorld template, String name) {
        if (!template.isReadOnly()) {
            flask.getPlugin().getComponentLogger().warn("Loading template ("+template.getName()+") that is not read-only, it is recommended to only use read-only slime worlds.");
        }

        AtomicReference<SlimeWorld> world = new AtomicReference<>();
        SchedulerUtil.runSyncAndWait(flask.getPlugin(), () -> {
            var clone = template.clone(name);
            world.set(loadWorld(clone));
        });
        return world.get();
    }

    /**
     * Generates a temporary world that is an exact copy of the given "template" world.
     * This new world is given a random UUID as its name. This world will not be saved.
     * @param template the world to copy.
     * @return the created clone.
     */
    default SlimeWorld useTemplate(SlimeWorld template) {
        return useTemplate(template, UUID.randomUUID().toString());
    }

    /**
     * Generates a temporary world that is an exact copy of the found "template" world. This world will not be saved.
     *
     * @param templateName the name of the world to copy.
     * @param cloneName    the name to give the clone.
     * @return a promise of the created clone.
     */
    default CompletableFuture<SlimeWorld> findAndUseTemplate(String templateName, String cloneName) {
        var promise = new CompletableFuture<SlimeWorld>();

        var templatePromise = readWorldAsync(templateName, true, WorldProperties.defaultProperties()); // todo: better properties?
        templatePromise.whenComplete((template, exception) -> {
            if (exception != null || template == null) {
                throw new RuntimeException(exception);
            }
            promise.complete(useTemplate(template));
        });

        return promise;
    }

    /**
     * Generates a temporary world that is an exact copy of the found "template" world.
     * This new world is given a random UUID as its name. This world will not be saved.
     * @param templateName the name of the world to copy.
     * @return a promise of the created clone.
     */
    default CompletableFuture<SlimeWorld> findAndUseTemplate(String templateName) {
        return findAndUseTemplate(templateName, UUID.randomUUID().toString());
    }

    /**
     * Read a slime world into memory and deserialize it. This action is I/O and should be done asynchronously.
     * @param name the name of the world to read.
     * @param readOnly whether this world will be read-only.
     * @param properties the properties to apply to the world.
     * @return the deserialized slime world.
     * @throws CorruptedWorldException the world is corrupted.
     * @throws NewerFormatException the world was made for a newer version of SRF.
     * @throws UnknownWorldException the world doesn't exist.
     * @throws IOException the world is already being accessed by something else.
     */
    SlimeWorld readWorld(String name, boolean readOnly, SlimePropertyMap properties) throws CorruptedWorldException, NewerFormatException, UnknownWorldException, IOException;

    /**
     * Reads a slime world into memory and deserializes it asynchronously.
     * @param name the name of the world to read.
     * @param readOnly whether this world will be read-only.
     * @param properties the properties to apply to the world.
     * @return a promise of the deserialized slime world. may complete exceptionally.
     */
    default CompletableFuture<SlimeWorld> readWorldAsync(String name, boolean readOnly, SlimePropertyMap properties)  {
        var promise = new CompletableFuture<SlimeWorld>();
        var scheduler = Bukkit.getScheduler();

        // read the world.
        scheduler.runTaskAsynchronously(flask.getPlugin(), task -> {
            try {
                var world = readWorld(name, readOnly, properties);
                promise.complete(world);
            } catch (CorruptedWorldException | NewerFormatException | UnknownWorldException | IOException e) {
                promise.completeExceptionally(e);
            }
        });

        return promise;
    }

    /**
     * Loads a world into the server from memory. Must be done synchronously.
     * @param world the deserialized world to load, obtained from {@link WorldService#readWorld(String, boolean, SlimePropertyMap)}.
     * @param callEvent whether to trigger {@link WorldLoadEvent}.
     * @return a "mirror" or instance of the world that can now be interacted with.
     */
    default SlimeWorld loadWorld(SlimeWorld world, boolean callEvent) {
        return slime.loadWorld(world, callEvent);
    }

    /**
     * Loads a world into the server from memory. Must be done synchronously.
     * @param world the deserialized world to load, obtained from {@link WorldService#readWorld(String, boolean, SlimePropertyMap)}.
     * @return a "mirror" or instance of the world that can now be interacted with.
     */
    default SlimeWorld loadWorld(SlimeWorld world) {
        return loadWorld(world, true);
    }

    /**
     * Saves a world asynchronously.
     * @param world the world to save.
     * @return a promise that returns true when the world has been saved.
     */
    default CompletableFuture<Boolean> saveWorld(SlimeWorld world) {
        var promise = new CompletableFuture<Boolean>();
        var scheduler = Bukkit.getScheduler();

        scheduler.runTaskAsynchronously(flask.getPlugin(), () -> {
            try {
                slime.saveWorld(world);
                promise.complete(true);
            } catch (IOException e) {
                promise.complete(false);
            }
        });

        return promise;
    }

    /**
     * Creates an empty world with the given name. Should be run asynchronously.
     * @param name
     * @return a promise of the slime world.
     */
    SlimeWorld createWorld(String name, boolean readOnly, SlimePropertyMap properties) throws IOException;

    default SlimeWorld createWorld(String name) throws IOException {
        return createWorld(name, false, WorldProperties.defaultProperties());
    }

    default CompletableFuture<SlimeWorld> createWorldAsync(String name, boolean readOnly, SlimePropertyMap properties) {
        var promise = new CompletableFuture<SlimeWorld>();
        var scheduler = Bukkit.getScheduler();

        scheduler.runTaskAsynchronously(flask.getPlugin(), () -> {
            try {
                promise.complete(createWorld(name,readOnly,properties));
            } catch (IOException e) {
                promise.completeExceptionally(e);
            }
        });

        return promise;
    }

    default CompletableFuture<SlimeWorld> createWorldAsync(String name) {
        return createWorldAsync(name, false, WorldProperties.defaultProperties());
    }
}
