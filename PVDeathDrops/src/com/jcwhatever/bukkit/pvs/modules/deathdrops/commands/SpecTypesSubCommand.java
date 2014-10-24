package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="drops",
        command="spectypes",
        staticParams = { "page=1"},
        usage="/{plugin-command} {command} spectypes [page]",
        description="Lists all available specificity types.")

public class SpecTypesSubCommand extends AbstractDropsCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Specificity Types";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        int page = args.getInt("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<String> types = getSpecificityTypes();

        for (String type : types) {
            pagin.add(type);
        }

        pagin.show(sender, page, FormatTemplate.ITEM);
    }
}

