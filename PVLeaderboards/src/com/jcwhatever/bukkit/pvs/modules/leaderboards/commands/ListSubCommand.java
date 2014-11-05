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

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ICommandInfo(
        parent="lb",
        command="list",
        staticParams={"page=1"},
        usage="/{plugin-command} {command} list [page]",
        description="List all leaderboards.")

public class ListSubCommand extends AbstractCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Leaderboards";
    @Localizable static final String _LABEL_ANCHOR_NOT_SET = "(anchor not set)";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        int page = args.getInt("page");

        List<Leaderboard> leaderboards = LeaderboardsModule.getModule().getLeaderboards();

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        String labelAnchorNotSet = Lang.get(_LABEL_ANCHOR_NOT_SET);

        for (Leaderboard leaderboard : leaderboards) {
            if (leaderboard == null) {
                Msg.debug("Null leaderboard exists.");
                continue;
            }

            List<UUID> arenaIds = leaderboard.getArenaIds();
            List<String> arenaNames = new ArrayList<String>(arenaIds.size());

            for (UUID id : arenaIds) {
                Arena arena = PVStarAPI.getArenaManager().getArena(id);
                if (arena == null)
                    continue;

                arenaNames.add(arena.getName());
            }

            if (leaderboard.getAnchorSign() != null)
                pagin.add(leaderboard.getName(), TextUtils.concat(arenaNames, ", "));
            else {

                pagin.add(leaderboard.getName(), TextUtils.concat(arenaNames, ", ") + TextColor.RED + ' ' + labelAnchorNotSet);
            }
        }

        pagin.show(sender, page, FormatTemplate.ITEM_DESCRIPTION);
    }
}
