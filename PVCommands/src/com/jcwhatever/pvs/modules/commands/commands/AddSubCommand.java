package com.jcwhatever.pvs.modules.commands.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.commands.CommandExtension;
import com.jcwhatever.pvs.modules.commands.Lang;
import org.bukkit.command.CommandSender;

@CommandInfo(
        command="add",
        staticParams = { "commandName" },
        description="Add an allowed command to the currently selected arena.",

        paramDescriptions = {
                "commandName= The name of the (root) command that will be allowed."})

public class AddSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS =
            "Command '{0: command name}' added to arena '{1: arena name}'";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String commandName = args.getString("commandName");

        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return;

        CommandExtension extension = getExtension(sender, arena, CommandExtension.class);
        if (extension == null)
            return;

        extension.addCommand(commandName);

        tellSuccess(sender, Lang.get(_SUCCESS, commandName, arena.getName()));
    }
}

