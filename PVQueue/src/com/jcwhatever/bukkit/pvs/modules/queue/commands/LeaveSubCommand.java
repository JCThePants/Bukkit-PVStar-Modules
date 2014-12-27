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


package com.jcwhatever.bukkit.pvs.modules.queue.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.queue.Lang;
import com.jcwhatever.bukkit.pvs.modules.queue.QueueManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@CommandInfo(
        parent="queue",
        command={"leave"},
        description="Leave the queue.",
        permissionDefault= PermissionDefault.TRUE)

public class LeaveSubCommand extends AbstractPVCommand {

    @Localizable static final String _SUCCESS = "Left the queue for arena '{0}'.";
    @Localizable static final String _FAILED = "You're not in a queue.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.assertNotConsole(this, sender);

        Player p = (Player)sender;
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        // check queue
        Arena arena = QueueManager.getCurrentQueue(player);
        if (arena == null) {
            tellError(p, Lang.get(_FAILED));
            return; // finish
        }

        QueueManager.removePlayer(player);
        tellSuccess(p, Lang.get(_SUCCESS, arena.getName()));
    }
}
