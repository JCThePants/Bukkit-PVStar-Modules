package com.jcwhatever.bukkit.pvs.modules.mobs.commands.spawner;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerInfo;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerManager;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="spawner",
        command="types",
        staticParams = { "page=1" },
        usage="/{plugin-command} spawner types [page]",
        description="List available mob spawners.")

public class TypesSubCommand extends AbstractPVCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Available Mob Spawners";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidCommandSenderException, InvalidValueException {

        int page = args.getInt("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<Class<? extends ISpawner>> spawnerTypes = SpawnerManager.getSpawnerClasses();
        for (Class<? extends ISpawner> type : spawnerTypes) {
            SpawnerInfo info = type.getAnnotation(SpawnerInfo.class);
            if (info == null)
                continue;

            pagin.add(info.name(), info.description());
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}

