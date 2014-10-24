package com.jcwhatever.bukkit.pvs.modules.regions.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionManager;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="regions",
        command="list",
        staticParams={ "page=1" },
        usage="/{plugin-command} {command} list [page]",
        description="List all sub regions in the currently selected arena.")

public class ListSubCommand extends AbstractRegionCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Sub Regions in Arena '{0}'";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        int page = args.getInt("page");

        RegionManager manager = SubRegionsModule.getInstance().getManager(arena);

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, arena.getName()));

        List<AbstractPVRegion> regions = manager.getRegions();

        for (AbstractPVRegion region : regions) {
            pagin.add(region.getName(), region.getTypeName());
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}
