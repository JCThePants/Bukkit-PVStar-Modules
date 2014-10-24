package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

public class AbstractLeaderboardCommand extends AbstractPVCommand {

    @Localizable static final String _NOT_FOUND = "A leaderboard named '{0}' was not found.";

    protected Leaderboard getLeaderboard(CommandSender sender, String leaderboardName) {
        Leaderboard leaderboard = LeaderboardsModule.getInstance().getLeaderboard(leaderboardName);
        if (leaderboard == null) {
            tellError(sender, Lang.get(_NOT_FOUND, leaderboardName));
            return null;
        }

        return leaderboard;
    }

}
