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
        command="enable",
        staticParams={ "leaderboardName" },
        usage="/{plugin-command} {command} enable <leaderboardName>",
        description="Enables the specified leader board.")

public class EnableSubCommand extends AbstractLeaderboardCommand {

    @Localizable
    static final String _SUCCESS = "Leaderboard '{0}' enabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getName("leaderboardName");

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finish

        leaderboard.setEnabled(true);

        tellSuccess(sender, Lang.get(_SUCCESS, leaderboard.getName()));
    }
}

