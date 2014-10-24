package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

@ICommandInfo(
        parent="lb",
        command="add",
        staticParams={"leaderboardName", "arenaNames"},
        usage="/{plugin-command} {command} add <leaderboardName> <arenaName1,arenaName2,arenaName3...>",
        description="Creates a new leaderboard that tracks the specified arenas.")

public class AddSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _ALREADY_EXISTS = "A leaderboard named '{0}' already exists.";
    @Localizable static final String _FAILED = "Failed to add leaderboard.";
    @Localizable static final String _SUCCESS = "Leaderboard '{0}' added. Set anchor to complete setup.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String leaderboardName = args.getName("leaderboardName");
        String arenaNames = args.getString("arenaNames");

        List<UUID> arenaIds = getArenaIds(sender, arenaNames);
        if (arenaIds == null)
            return; // finish

        Leaderboard leaderboard = LeaderboardsModule.getInstance().getLeaderboard(leaderboardName);
        if (leaderboard != null) {
            tellError(sender, Lang.get(_ALREADY_EXISTS, leaderboardName));
            return; // finish
        }

        leaderboard = LeaderboardsModule.getInstance().addLeaderboard(leaderboardName, arenaIds);
        if (leaderboard == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, leaderboard.getName()));
    }
}
