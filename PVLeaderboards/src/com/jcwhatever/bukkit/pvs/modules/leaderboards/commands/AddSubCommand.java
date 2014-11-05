/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.Lang;
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

        Leaderboard leaderboard = LeaderboardsModule.getModule().getLeaderboard(leaderboardName);
        if (leaderboard != null) {
            tellError(sender, Lang.get(_ALREADY_EXISTS, leaderboardName));
            return; // finish
        }

        leaderboard = LeaderboardsModule.getModule().addLeaderboard(leaderboardName, arenaIds);
        if (leaderboard == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, leaderboard.getName()));
    }
}
