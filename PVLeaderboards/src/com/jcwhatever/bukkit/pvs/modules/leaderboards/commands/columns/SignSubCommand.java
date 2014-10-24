package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.columns;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.AbstractLeaderboardCommand;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.AbstractColumn;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.AnchorColumn;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

@ICommandInfo(
        parent="columns",
        command="sign",
        staticParams={ "leaderboardName", "signIndex", "lineNumber", "text"},
        usage="/{plugin-command} {command} columns sign <leaderboardName> <signIndex> <lineNumber> <text>",
        description="Edit a leaderboard header sign.")

public class SignSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _SIGN_NOT_FOUND = "A sign was not found at index {0} on leaderboard '{1}'.";
    @Localizable static final String _INVALID_LINE_NUMBER = "Valid values for <lineNumber> are 1 through 4.";
    @Localizable static final String _SUCCESS = "Leaderboard sign edited.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getName("leaderboardName");
        int signIndex = args.getInt("signIndex");
        int lineNum = args.getInt("lineNumber");
        String text = args.getString("text");

        text = ChatColor.translateAlternateColorCodes('&', text);

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        AbstractColumn column = null;

        if (signIndex == 1) {
            column = leaderboard.getAnchorColumn();
        }
        else if (signIndex > 1) {
            List<StatisticsColumn> columns = leaderboard.getStatisticsColumns();
            if (signIndex <= columns.size() + 1) {

                column = columns.get(signIndex - 2);
            }
        }

        if (column == null) {
            tellError(sender, Lang.get(_SIGN_NOT_FOUND, signIndex, leaderboard.getName()));
            return; // finished
        }

        if (lineNum < 1 || lineNum > 4) {
            tellError(sender, Lang.get(_INVALID_LINE_NUMBER));
            return; // finished
        }

        if (column instanceof AnchorColumn) {

            AnchorColumn anchorColumn = (AnchorColumn)column;
            anchorColumn.getHeaderSign().setLine(lineNum, text);
            anchorColumn.getHeaderSign().update(true);
        }
        else {

            StatisticsColumn statColumn = (StatisticsColumn)column;
            statColumn.getSettings().setDisplayLine(lineNum, text);

            statColumn.getHeaderSign().setLine(lineNum, text);
            statColumn.getHeaderSign().update(true);
        }

        tellSuccess(sender, Lang.get(_SUCCESS));
    }
}