package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="lb",
        command="update",
        staticParams={ "leaderboardName" },
        usage="/{plugin-command} {command} udpate <leaderboardName>",
        description="Update the specified leaderboard.")

public class UpdateSubCommand extends AbstractLeaderboardCommand {

    @Localizable
    static final String _SUCCESS = "Leaderboard '{0}' updated.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getString("leaderboardName");

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        leaderboard.update();

        tellSuccess(sender, Lang.get(_SUCCESS, leaderboardName));
    }
}
