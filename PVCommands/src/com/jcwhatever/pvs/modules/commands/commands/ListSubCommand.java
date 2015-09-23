package com.jcwhatever.pvs.modules.commands.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.commands.CommandExtension;
import com.jcwhatever.pvs.modules.commands.Lang;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        command="list",
        staticParams = { "page=1" },
        floatingParams = { "search=" },
        description="List all whitelisted commands in the currently selected arena.",

        paramDescriptions = {
                "page= {PAGE}",
                "search= Search for a specific command"
        })

public class ListSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE =
            "Whitelisted Commands in '{0: arena name}'";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return;

        CommandExtension extension = getExtension(sender, arena, CommandExtension.class);
        if (extension == null)
            return;

        ChatPaginator pagin = createPagin(args, 7, Lang.get(_PAGINATOR_TITLE, arena.getName()));

        List<String> commands = extension.getCommands();

        for (String command : commands) {
            pagin.add(command);
        }

        if (!args.isDefaultValue("search")) {
            pagin.setSearchTerm(args.getString("search"));
        }

        pagin.show(sender, page, TextUtils.FormatTemplate.LIST_ITEM);
    }
}
