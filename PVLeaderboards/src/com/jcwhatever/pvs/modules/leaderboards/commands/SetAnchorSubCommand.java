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

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelectHandler;
import com.jcwhatever.nucleus.managed.blockselect.IBlockSelector.BlockSelectResult;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.modules.leaderboards.Lang;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

@CommandInfo(
        parent="lb",
        command="setanchor",
        staticParams={"leaderboardName"},
        description="Sets the leaderboards anchor sign (top left) to the sign the player clicks on.",

        paramDescriptions = {
                "leaderboardName= The name of the leaderboard."})

public class SetAnchorSubCommand extends AbstractLeaderboardCommand {

    @Localizable static final String _NOT_A_SIGN =
            "The block you selected is not a sign.";

    @Localizable static final String _SELECT_SIGN =
            "Please click on the leaderboard anchor sign...";

    @Localizable static final String _ANCHOR_SET =
            "Leaderboard '{0: leaderboard name}' anchor set.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        String leaderboardName = args.getString("leaderboardName");

        final Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        Player p = (Player)sender;

        Nucleus.getBlockSelector().query(p, new IBlockSelectHandler() {
            @Override
            public BlockSelectResult onBlockSelect(Player player, Block selectedBlock, Action clickAction) {

                if (selectedBlock.getState() instanceof Sign) {

                    Sign sign = (Sign) selectedBlock.getState();

                    leaderboard.setAnchor(sign);

                    tellSuccess(player, Lang.get(_ANCHOR_SET, leaderboard.getName()));
                } else {

                    tellError(player, Lang.get(_NOT_A_SIGN));
                }
                return BlockSelectResult.FINISHED;
            }
        });

        tell(sender, Lang.get(_SELECT_SIGN));
    }
}
