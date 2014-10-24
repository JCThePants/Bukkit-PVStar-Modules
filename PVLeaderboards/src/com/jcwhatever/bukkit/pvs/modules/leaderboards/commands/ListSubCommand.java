package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ICommandInfo(
        parent="lb",
        command="list",
        staticParams={"page=1"},
        usage="/{plugin-command} {command} list [page]",
        description="List all leaderboards.")

public class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Leaderboards";
    @Localizable static final String _LABEL_ANCHOR_NOT_SET = "(anchor not set)";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        int page = args.getInt("page");

        List<Leaderboard> leaderboards = LeaderboardsModule.getInstance().getLeaderboards();

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        String labelAnchorNotSet = Lang.get(_LABEL_ANCHOR_NOT_SET);

        for (Leaderboard leaderboard : leaderboards) {
            if (leaderboard == null) {
                Msg.debug("Null leaderboard exists.");
                continue;
            }

            List<UUID> arenaIds = leaderboard.getArenaIds();
            List<String> arenaNames = new ArrayList<String>(arenaIds.size());

            for (UUID id : arenaIds) {
                Arena arena = PVStarAPI.getArenaManager().getArena(id);
                if (arena == null)
                    continue;

                arenaNames.add(arena.getName());
            }

            if (leaderboard.getAnchorSign() != null)
                pagin.add(leaderboard.getName(), TextUtils.concat(arenaNames, ", "));
            else {

                pagin.add(leaderboard.getName(), TextUtils.concat(arenaNames, ", ") + TextColor.RED + ' ' + labelAnchorNotSet);
            }
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}
