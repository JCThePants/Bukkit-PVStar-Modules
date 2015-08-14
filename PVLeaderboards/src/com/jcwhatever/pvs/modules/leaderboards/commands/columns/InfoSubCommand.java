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


package com.jcwhatever.pvs.modules.leaderboards.commands.columns;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.pvs.api.stats.StatType;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.leaderboards.Lang;
import com.jcwhatever.pvs.modules.leaderboards.commands.AbstractLeaderboardCommand;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="columns",
        command="info",
        staticParams={"boardName", "page=1"},
        description="Display column info for the specified leaderboard name.",

        paramDescriptions = {
                "boardName= The name of the leaderboard",
                "page= {PAGE}"})

public class InfoSubCommand extends AbstractLeaderboardCommand implements IExecutableCommand {

    @Localizable static final String _PAGINATOR_TITLE =
            "Columns for leaderboard '{0: leaderboard name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String boardName = args.getString("boardName");
        int page = args.getInteger("page");

        Leaderboard leaderboard = getLeaderboard(sender, boardName);
        if (leaderboard == null)
            return; // finish

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, boardName));

        List<StatisticsColumn> columns = leaderboard.getStatisticsColumns();

        for (StatisticsColumn column : columns) {
            StatType statType = column.getStatType();
            pagin.add(statType.getName(), statType.getOrder().name());
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
