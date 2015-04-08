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
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.modules.leaderboards.Lang;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="lb",
        command="format",
        staticParams={"leaderboardName", "lineNumber", "lineColor="},
        description="Set line colors on leaderboard signs. Line number must be between 1 and 4.",

        paramDescriptions = {
                "leaderboardName= The name of the leaderboard.",
                "lineNumber= The index number of the line to edit. The first line is 1. " +
                        "There are 4 lines in a sign.",
                "lineColor=The name of the color or the color code prefix with '&'."})

public class FormatSubCommand extends AbstractLeaderboardCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS =
            "Line color changed for line {0: line number}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        String leaderboardName = args.getName("leaderboardName");
        int lineNumber = args.getInteger("lineNumber");
        String rawColor = args.getString("lineColor");

        String color = ChatColor.getLastColors(
                ChatColor.translateAlternateColorCodes('&', rawColor));

        color = TextUtils.format(color);

        if (lineNumber < 1 || lineNumber > 4)
            throw new CommandException(Lang.get(_INVALID_LINE_NUMBER));

        // adjust to zero based
        lineNumber -= 1;

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finish

        leaderboard.setLineFormat(lineNumber, color);

        tellSuccess(sender, Lang.get(_SUCCESS, lineNumber + 1));
    }
}
