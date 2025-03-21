package io.github.sylviameows.flask;

import io.github.sylviameows.flask.api.FlaskAPI;
import io.github.sylviameows.flask.api.FlaskPlugin;
import io.github.sylviameows.flask.api.Palette;
import io.github.sylviameows.flask.api.events.FlaskDispatcher;
import io.github.sylviameows.flask.api.manager.PlayerManager;
import io.github.sylviameows.flask.api.registry.GameRegistry;
import io.github.sylviameows.flask.api.services.MessageService;
import io.github.sylviameows.flask.api.services.WorldService;
import io.github.sylviameows.flask.commands.SetSpawnCommand;
import io.github.sylviameows.flask.commands.editor.EditorCommand;
import io.github.sylviameows.flask.commands.hologram.HologramCommand;
import io.github.sylviameows.flask.commands.queue.QueueCommand;
import io.github.sylviameows.flask.hub.holograms.GameHologram;
import io.github.sylviameows.flask.listeners.FlaskDispatcherImpl;
import io.github.sylviameows.flask.listeners.JoinListener;
import io.github.sylviameows.flask.listeners.LeaveListener;
import io.github.sylviameows.flask.listeners.RightClickEntity;
import io.github.sylviameows.flask.managers.PlayerManagerImpl;
import io.github.sylviameows.flask.registries.GameRegistryImpl;
import io.github.sylviameows.flask.services.MessageServiceImpl;
import io.github.sylviameows.flask.services.FileWorldService;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The main plugin file, where access to all necessary information is given.
 */
public class Flask extends FlaskPlugin implements FlaskAPI {
    private static ComponentLogger logger;
    private static MessageServiceImpl messageService;
    private static WorldService worldService;
    private static Flask instance;
    private static FlaskDispatcherImpl dispatcher;

    @Override
    public void onEnable() {
        Holder.setInstance(this);
        instance = this;
        logger = getComponentLogger();

        GameHologram.load();

        // loads default config.
        saveResource("config.yml", false);

        RightClickEntity.register(this);
        JoinListener.register(this);
        LeaveListener.register(this);

        Flask.dispatcher = new FlaskDispatcherImpl();
        Flask.messageService = new MessageServiceImpl(this);
        Flask.worldService = new FileWorldService();

        // commands
        registerCommands();

        // display plugin loaded message (aka motd)
        for (Component component : motd()) {
            logger.info(component);
        }
    }

    @Override
    public void onDisable() {
        purgeFlaskEntities();
    }

    public void registerCommands() {
        var lifecycle = this.getLifecycleManager();
        lifecycle.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            new QueueCommand().register(commands);
            new HologramCommand().register(commands);
            new SetSpawnCommand().register(commands);
            new EditorCommand().register(commands);
        });
    }

    public static Flask getInstance() { // todo(): try to remove usages
        return instance;
    }

    /**
     * Removes any entities with the pdc tag "flask:purge," use to remove any potentially hanging text displays on shutdown.
     */
    private void purgeFlaskEntities() {
        logger.info("Purging flask entities...");
        var worlds = Bukkit.getWorlds();
        for (World world : worlds) {
            var purge = world.getEntities().stream().filter(entity ->
                    entity.getPersistentDataContainer().has(new NamespacedKey("flask", "purge"))
            ).toList();
            var count = purge.size();
            if (count > 0) {
                purge.forEach(Entity::remove);
                logger.info("Purged "+count+" flask entities from "+world.getName());
            }
        }
        logger.info("Flask entities purged!");
    }

    /**
     * Gets the plugins message of the day ("motd"), using a random message.
     * @return an array of each component in the motd.
     */
    private Component @NotNull [] motd() {
        List<String> messages = Arrays.asList(
                "shipping untested code to your server's doorstep.",
                "preparing for a science experiment gone wrong.",
                "who even reads these messages anyway?",
                "powering minigames using \"chemistry\" since 2024.",
                "<- is this one of those mark rober volcanos?",
                "so uh.. what the hell is a vial and why do i open it?"
        );
        var random = new Random();
        String message = messages.get(random.nextInt(messages.size()));

        return new Component[]{
                Component.text("  ] [").color(Palette.MINT),
                Component.text("  |~|   Flask v"+this.getPluginMeta().getVersion()+" loaded successfully.").color(Palette.MINT),
                Component.text(" /o  \\   ").color(Palette.MINT).append(Component.text(message).color(Palette.WHITE)),
                Component.text("/___o_\\").style(Style.style(Palette.MINT, TextDecoration.UNDERLINED))
        };
    }

    // API
    @Override
    public WorldService getWorldService() {
        return worldService;
    }

    @Override
    public GameRegistry getGameRegistry() {
        return GameRegistryImpl.instance();
    }

    @Override
    public PlayerManager getPlayerManager() {
        return PlayerManagerImpl.instance();
    }

    @Override
    public MessageService getMessageService() {
        return messageService;
    }

    @Override
    public FlaskDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }

    @Override
    public Location getSpawnLocation() {
        Location location = getConfig().getLocation("spawn_location");
        if (location != null) {
            return location;
        }

        World world = Bukkit.getWorld("world");
        if (world == null) {
            world = Bukkit.getWorlds().getFirst();
        }
        return world.getSpawnLocation();
    }

    @Override
    public FlaskAPI getFlaskAPI() {
        return this;
    }

    public static ComponentLogger logger() {
        return logger;
    }
}
