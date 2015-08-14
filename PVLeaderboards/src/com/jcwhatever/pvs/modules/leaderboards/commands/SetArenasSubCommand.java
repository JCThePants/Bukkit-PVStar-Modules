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


package com.jcwhatever.pvs.modules.leaderboards.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.modules.leaderboards.Lang;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

@CommandInfo(
        parent="lb",
        command="setarenas",
        staticParams={"boardName", "arenaNames"},
        description="Change arenas the leaderboard will compile from.",

        paramDescriptions = {
                "boardName= The name of the leaderboard.",
                "arenaNames= A comma delimited list of arena names. No spaces."})

public class SetArenasSubCommand extends AbstractLeaderboardCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS =
            "Leaderboard '{0: leaderboard name}' arenas changed to '{1: arena list}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String boardName = args.getString("boardName");
        String arenaNames = args.getString("arenaNames");

        Leaderboard leaderboard = getLeaderboard(sender, boardName);
        if (leaderboard == null)
            return; // finished

        List<UUID> arenaIds = getArenaIds(sender, arenaNames);
        if (arenaIds == null)
            return; // finished

        leaderboard.setArenas(arenaIds);

        tellSuccess(sender, Lang.get(_SUCCESS, boardName, arenaNames));
    }
}
