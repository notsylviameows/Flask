package io.github.sylviameows.flask.commands.editor.session.management;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sylviameows.flask.Flask;
import io.github.sylviameows.flask.api.FlaskPlayer;
import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.services.MessageService;
import io.github.sylviameows.flask.commands.structure.CommandProperties;
import io.github.sylviameows.flask.commands.structure.FlaskCommand;
import io.github.sylviameows.flask.commands.structure.types.GameArgumentType;
import io.github.sylviameows.flask.editor.EditorSession;
import io.github.sylviameows.flask.editor.EditorUtilities;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandProperties(label = "open", aliases = {"create", "start"})
public class OpenSession extends FlaskCommand {
    public OpenSession() {
        addArgument(Commands.argument("game", GameArgumentType.game()).executes(context -> {
            Game<?> game = context.getArgument("game", Game.class);
            return executeWithArgs(context, game);
        }).then(Commands.argument("map", StringArgumentType.word()).suggests((context,builder) -> {
            Game<?> game = context.getArgument("game", Game.class);
            game.getMapManager().keys().forEach(builder::suggest);
            return builder.buildFuture();
        }).executes(context -> {
            Game<?> game = context.getArgument("game", Game.class);
            String mapId = context.getArgument("map", String.class);
            // var map = game.getMapManager().get(mapId); //fixme later (re-add when creating maps is separated from sessions)
            // if (map == null) {
            //     return 0; //TODO: prompt to create map or just error?
            // }

            return executeWithArgs(context, game, mapId);
        })));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            ms.sendMessage(player, MessageService.MessageType.ERROR, "missing_args", "game & map");
            return 1;
        }

        ms.sendMessage(context.getSource().getSender(), MessageService.MessageType.ERROR, "not_player");
        return 1;
    }

    private int executeWithArgs(CommandContext<CommandSourceStack> context, Game<?> game) {
        if (context.getSource().getSender() instanceof Player player) {
            ms.sendMessage(player, MessageService.MessageType.ERROR, "missing_args", "map");
            return 1;
        }

        ms.sendMessage(context.getSource().getSender(), MessageService.MessageType.ERROR, "not_player");
        return 1;
    }

    private int executeWithArgs(CommandContext<CommandSourceStack> context, Game<?> game, String mapId) {
        if (context.getSource().getSender() instanceof Player player) {
            FlaskPlayer flaskPlayer = Flask.getInstance().getPlayerManager().get(player);
            EditorSession<?> session = EditorUtilities.getSession(flaskPlayer);

            if (session != null) {
                ms.sendMessage(player, MessageService.MessageType.EDITOR, "session.occupied");
                return 1;
            }

            var world = Bukkit.getWorld(mapId);
            if (world != null) {
                ms.sendMessage(player, MessageService.MessageType.EDITOR, "session.in_use", "<TODO>"); // TODO central editor session storage, maybe static var on EditorSession?
            }

            session = new EditorSession<>(player, game, mapId);
            EditorUtilities.setSession(flaskPlayer, session);
            ms.sendMessage(player, MessageService.MessageType.EDITOR, "session.open", mapId, game.getSettings().getName());
            return 1;
        }

        ms.sendMessage(context.getSource().getSender(), MessageService.MessageType.ERROR, "not_player");
        return 1;
    }
}
