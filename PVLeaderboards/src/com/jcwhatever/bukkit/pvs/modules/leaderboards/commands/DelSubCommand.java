package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="lb",
        command="del",
        staticParams={ "leaderboardName" },
        usage="/{plugin-command} {command} add <leaderboardName>",
        description="Removes the specified leader board.")

public class DelSubCommand extends AbstractLeaderboardCommand {


    @Localizable static final String _FAILED = "Failed to remove leaderboard.";
    @Localizable static final String _SUCCESS = "Leaderboard '{0}' removed.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getName("leaderboardName");

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finish

        if (!LeaderboardsModule.getInstance().removeLeaderboard(leaderboardName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, leaderboard.getName()));
    }
}
