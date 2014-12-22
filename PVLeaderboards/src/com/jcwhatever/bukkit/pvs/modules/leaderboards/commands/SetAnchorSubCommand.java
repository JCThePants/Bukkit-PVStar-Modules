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

import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect;
import com.jcwhatever.bukkit.generic.player.PlayerBlockSelect.PlayerBlockSelectHandler;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.Lang;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;

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

    @Localizable static final String _NOT_A_SIGN = "The block you selected is not a sign.";
    @Localizable static final String _SELECT_SIGN = "Please click on the leaderboard anchor sign...";
    @Localizable static final String _ANCHOR_SET = "Leaderboard '{0}' anchor set.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.assertNotConsole(this, sender);

        String leaderboardName = args.getString("leaderboardName");

        final Leaderboard leaderboard = getLeaderboard(sender, leaderboardName);
        if (leaderboard == null)
            return; // finished

        Player p = (Player)sender;

        PlayerBlockSelect.query(p, new PlayerBlockSelectHandler() {
            @Override
            public boolean onBlockSelect(Player p, Block selectedBlock, Action clickAction) {


                if (!(selectedBlock.getState() instanceof Sign)) {

                    tellError(p, Lang.get(_NOT_A_SIGN));
                } else {

                    Sign sign = (Sign) selectedBlock.getState();

                    leaderboard.setAnchor(sign);

                    tellSuccess(p, Lang.get(_ANCHOR_SET, leaderboard.getName()));
                }
                return true;
            }
        });


        tell(sender, Lang.get(_SELECT_SIGN));
    }
}
