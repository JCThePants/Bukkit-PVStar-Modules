package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

@ICommandInfo(
        parent="lb",
        command="setarenas",
        staticParams={"leaderboardName", "arenaNames"},
        usage="/{plugin-command} {command} setarenas <leaderboardName> <arenaName1,arenaName2,arenaName3...>",
        description="Change arenas the leaderboard will compile from.")

public class SetArenasSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _SUCCESS = "Leaderboard '{0}' arenas changed to '{1}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String arenaNames = args.getString("arenaNames");
        String leaderboardName = args.getString("leaderboardName");

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        List<UUID> arenaIds = getArenaIds(sender, arenaNames);
        if (arenaIds == null)
            return; // finished

        leaderboard.setArenas(arenaIds);

        tellSuccess(sender, Lang.get(_SUCCESS, leaderboardName, arenaNames));
    }
}
