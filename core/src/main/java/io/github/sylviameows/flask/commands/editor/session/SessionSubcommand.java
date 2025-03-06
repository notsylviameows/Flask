package io.github.sylviameows.flask.commands.editor.session;

import com.mojang.brigadier.context.CommandContext;
import io.github.sylviameows.flask.Flask;
import io.github.sylviameows.flask.api.services.MessageService;
import io.github.sylviameows.flask.commands.editor.session.management.CloseSession;
import io.github.sylviameows.flask.commands.editor.session.management.OpenSession;
import io.github.sylviameows.flask.commands.structure.CommandProperties;
import io.github.sylviameows.flask.commands.structure.FlaskCommand;
import io.github.sylviameows.flask.editor.EditorUtilities;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

@CommandProperties(label = "session")
public class SessionSubcommand extends FlaskCommand {
    public SessionSubcommand() {
        super();

        addSubCommand(new OpenSession());
        addSubCommand(new CloseSession());
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> context) {
        if (context.getSource().getSender() instanceof Player player) {
            var flaskPlayer = Flask.getInstance().getPlayerManager().get(player);
            var session = EditorUtilities.getSession(flaskPlayer);
            if (session == null) {
                ms.sendMessage(player, MessageService.MessageType.EDITOR, "session.info_none");
            } else {
                var game = session.getGame();
                var map = session.getMap();
                ms.sendMessage(player, MessageService.MessageType.EDITOR, "session.info", map.getId(), game.getSettings().getName());
            }
            return 1;
        }

        ms.sendMessage(context.getSource().getSender(), MessageService.MessageType.ERROR, "not_player");
        return 1;
    }
}
