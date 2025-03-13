package io.github.sylviameows.flask.commands.editor;

import com.mojang.brigadier.context.CommandContext;
import io.github.sylviameows.flask.commands.editor.session.SessionSubcommand;
import io.github.sylviameows.flask.commands.structure.CommandProperties;
import io.github.sylviameows.flask.commands.structure.FlaskCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;

@CommandProperties(label = "editor", aliases = {"e", "edit"}, permission = "flask.editor")
public class EditorCommand extends FlaskCommand{
    public EditorCommand() {
        addSubCommand(new SessionSubcommand());
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> context) {
        return 1;
    }

}
