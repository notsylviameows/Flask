package io.github.sylviameows.flask.commands.editor.session;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.sylviameows.flask.Flask;
import io.github.sylviameows.flask.api.FlaskPlayer;
import io.github.sylviameows.flask.api.game.Game;
import io.github.sylviameows.flask.api.manager.PlayerManager;
import io.github.sylviameows.flask.api.services.MessageService;
import io.github.sylviameows.flask.commands.structure.CommandProperties;
import io.github.sylviameows.flask.commands.structure.FlaskCommand;
import io.github.sylviameows.flask.commands.structure.types.GameArgumentType;
import io.github.sylviameows.flask.editor.EditorSession;
import io.github.sylviameows.flask.editor.EditorUtilities;
import io.github.sylviameows.flask.players.FlaskPlayerImpl;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.entity.Player;

@CommandProperties(label = "session", permission = "flask.editor.session")
public class SessionSubcommand extends FlaskCommand {
    private PlayerManager pm = Flask.getInstance().getPlayerManager();

    public SessionSubcommand() {
        super();

        addSubCommand(new Open());
        addSubCommand(new Close());
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> context) {
        var sender = context.getSource().getSender();
        if (sender instanceof Player player) {
            FlaskPlayer fp = pm.get(player);
            EditorSession session;
            if (fp instanceof FlaskPlayerImpl fpi) session = fpi.getSession();
            else {
                ms.sendMessage(sender, MessageService.MessageType.ERROR, "unknown");
                return 1;
            }

            if (session == null) {
                ms.sendMessage(sender, MessageService.MessageType.STANDARD, "session.none");
            } else {
                ms.sendMessage(sender, MessageService.MessageType.STANDARD, "session.info");
            }
        } else {
            Flask.getInstance().getMessageService().sendMessage(sender, MessageService.MessageType.ERROR, "not_player");
        }

        return 1;
    }


    @CommandProperties(label = "open", permission = "flask.editor.session")
    private class Open extends FlaskCommand {

        public Open() {
            super();

            arguments.add(Commands.argument("game", GameArgumentType.game()).executes(context -> {
                Game game = context.getArgument("game", Game.class);
                return executeWithArgs(context, game);
            }).then(Commands.argument("id", StringArgumentType.word()).executes(context -> {
                Game game = context.getArgument("game", Game.class);
                String id = context.getArgument("id", String.class);
                return executeWithArgs(context, game, id);
            })));
        }

        private int executeWithArgs(CommandContext<CommandSourceStack> context, Game game, String id) {
            var sender = context.getSource().getSender();
            if (sender instanceof Player player) {
                FlaskPlayer fp = pm.get(player);
                EditorSession session = EditorUtilities.getSession(fp);

                if (session != null) {
                    ms.sendMessage(sender, MessageService.MessageType.ERROR, "editor.session.occupied");
                    return 1;
                }

                EditorUtilities.setSession(fp, new EditorSession(player, game, id));
            }

            return 1;
        }

        private int executeWithArgs(CommandContext<CommandSourceStack> context, Game game) {
            // fixme missing args
            return 1;
        }

        @Override
        public int execute(CommandContext<CommandSourceStack> context) {
            // fixme missing args
            return 1;
        }
    }

    @CommandProperties(label = "close", permission = "flask.editor.session")
    private class Close extends FlaskCommand {

        @Override
        public int execute(CommandContext<CommandSourceStack> context) {
            var sender = context.getSource().getSender();
            if (sender instanceof Player player) {
                FlaskPlayer fp = pm.get(player);
                EditorSession session = EditorUtilities.getSession(fp);

                if (session == null) {
                    ms.sendMessage(sender, MessageService.MessageType.ERROR, "editor.session.no_session");
                    return 1;
                }

                session.save();
            }

            return 1;
        }
    }
}
