package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.columns;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.AbstractLeaderboardCommand;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="columns",
        command="info",
        staticParams={"leaderboardName", "page=1"},
        usage="/{plugin-command} {command} columns info <leaderboardName> [page]",
        description="Display column info for the specified leaderboard name.")

public class InfoSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Columns for leaderboard '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getName("leaderboardName");
        int page = args.getInt("page");

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finish

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, leaderboardName));

        List<StatisticsColumn> columns = leaderboard.getStatisticsColumns();

        for (StatisticsColumn column : columns) {
            pagin.add(column.getStatType().getName(), column.getSettings().getSortOrder().name());
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}
