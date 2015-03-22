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

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.pvs.modules.leaderboards.Lang;
import com.jcwhatever.pvs.modules.leaderboards.commands.AbstractLeaderboardCommand;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.AbstractColumn;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.AnchorColumn;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="columns",
        command="sign",
        staticParams={ "leaderboardName", "signIndex", "lineNumber", "text"},
        description="Edit a leaderboard header sign.",

        paramDescriptions = {
                "leaderboardName= The name of the leaderboard",
                "signIndex= The index location of the sign starting from the anchor sign" +
                        "and ending with the sign furthest to the right. The anchor sign is sign" +
                        "1, the one to its right is sign 2 and so on.",
                "lineNumber= The index number of the line to edit. The first line is 1. " +
                        "There are 4 lines in a sign.",
                "text= The text to put."})

public class SignSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _SIGN_NOT_FOUND = "A sign was not found at index {0} on leaderboard '{1}'.";
    @Localizable static final String _INVALID_LINE_NUMBER = "Valid values for <lineNumber> are 1 through 4.";
    @Localizable static final String _SUCCESS = "Leaderboard sign edited.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String leaderboardName = args.getName("leaderboardName");
        int signIndex = args.getInteger("signIndex");
        int lineNum = args.getInteger("lineNumber");
        String text = args.getString("text");

        text = ChatColor.translateAlternateColorCodes('&', text);

        Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        AbstractColumn column = null;

        if (signIndex == 1) {
            column = leaderboard.getAnchorColumn();
        }
        else if (signIndex > 1) {
            List<StatisticsColumn> columns = leaderboard.getStatisticsColumns();
            if (signIndex <= columns.size() + 1) {

                column = columns.get(signIndex - 2);
            }
        }

        if (column == null) {
            tellError(sender, Lang.get(_SIGN_NOT_FOUND, signIndex, leaderboard.getName()));
            return; // finished
        }

        if (lineNum < 1 || lineNum > 4) {
            tellError(sender, Lang.get(_INVALID_LINE_NUMBER));
            return; // finished
        }

        if (column instanceof AnchorColumn) {

            AnchorColumn anchorColumn = (AnchorColumn)column;
            anchorColumn.getHeaderSign().setLine(lineNum, text);
            anchorColumn.getHeaderSign().update(true);
        }
        else {

            StatisticsColumn statColumn = (StatisticsColumn)column;
            statColumn.getSettings().setDisplayLine(lineNum, text);

            statColumn.getHeaderSign().setLine(lineNum, text);
            statColumn.getHeaderSign().update(true);
        }

        tellSuccess(sender, Lang.get(_SUCCESS));
    }
}
