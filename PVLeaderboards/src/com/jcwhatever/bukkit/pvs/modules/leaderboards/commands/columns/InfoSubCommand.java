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


package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.columns;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.Lang;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.AbstractLeaderboardCommand;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="columns",
        command="info",
        staticParams={"leaderboardName", "page=1"},
        description="Display column info for the specified leaderboard name.",

        paramDescriptions = {
                "leaderboardName= The name of the leaderboard",
                "page= {PAGE}"})

public class InfoSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Columns for leaderboard '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String leaderboardName = args.getName("leaderboardName");
        int page = args.getInteger("page");

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finish

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, leaderboardName));

        List<StatisticsColumn> columns = leaderboard.getStatisticsColumns();

        for (StatisticsColumn column : columns) {
            pagin.add(column.getStatType().getName(), column.getSettings().getSortOrder().name());
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
