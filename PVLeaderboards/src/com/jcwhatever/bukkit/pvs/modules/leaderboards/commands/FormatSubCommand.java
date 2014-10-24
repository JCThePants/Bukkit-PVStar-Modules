package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="lb",
        command="format",
        staticParams={"leaderboardName", "lineNumber", "lineColor="},
        usage="/{plugin-command} {command} format <leaderboardName> <lineNumber> [lineColor]",
        description="Set line colors on leaderboard signs. Line number must be between 1 and 4.")

public class FormatSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _INVALID_LINE_NUMBER = "Invalid argument. <lineNumber> must be a number between 1 and 4.";
    @Localizable static final String _SUCCESS = "Line color changed for line {0}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getName("leaderboardName");
        int lineNumber = args.getInt("lineNumber");
        String rawColor = args.getString("lineColor");

        String color = ChatColor.getLastColors(
                ChatColor.translateAlternateColorCodes('&', rawColor));

        if (lineNumber < 1 || lineNumber > 4) {
            tellError(sender, Lang.get(_INVALID_LINE_NUMBER));
            return; // finish
        }

        // adjust to zero based
        lineNumber -= 1;

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finish

        leaderboard.setLineFormat(lineNumber, color);

        tellSuccess(sender, Lang.get(_SUCCESS, lineNumber + 1));
    }
}
