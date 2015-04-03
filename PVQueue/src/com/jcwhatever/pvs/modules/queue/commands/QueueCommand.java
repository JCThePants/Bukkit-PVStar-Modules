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


package com.jcwhatever.pvs.modules.queue.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.queue.Lang;
import com.jcwhatever.pvs.modules.queue.QueueManager;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

@CommandInfo(
        command={"q", "queue"},
        staticParams={"arenaName=$info"},
        description="Queue to join the specified arena as soon as it's available " +
                "or get your current queue status. [PVQueue]",
        permissionDefault= PermissionDefault.TRUE,

        paramDescriptions = {
                "arenaName= The name of the arena."})

public class QueueCommand extends AbstractPVCommand {

    @Localizable static final String _NOT_IN_QUEUE =
            "You are not in a queue.";

    @Localizable static final String _QUEUE_INFO =
            "You are queued for arena '{0: arena name}'. Your queue position is {1: number}.";

    @Localizable static final String _QUEUE_LEAVE =
            "Type '/{plugin-command} q leave' if you wish to leave the queue.";

    @Localizable static final String _CANT_JOIN_IN_GAME =
            "You cannot join a queue while you are in a game.";

    @Localizable static final String _ARENA_DISABLED =
            "Arena '{0: arena name}' is not enabled.";

    @Localizable static final String _ARENA_NOT_FOUND =
            "An arena named '{0: arena name}' was not found.";

    @Localizable static final String _SUCCESS =
            "Joined queue for arena '{0: arena name}'. Your queue position is {1: number}.";

    @Localizable static final String _FAILED = "Failed to join queue.";

    public QueueCommand() {
        super();

        registerCommand(LeaveSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        CommandException.checkNotConsole(this, sender);

        Player p = (Player)sender;
        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);

        // get info about players current queue status.
        if (args.getString("arenaName").equals("$info")) {

            Arena arena = QueueManager.getCurrentQueue(player);
            if (arena == null) {
                tell(p, Lang.get(_NOT_IN_QUEUE));
            }
            else {
                QueueManager manager = QueueManager.get(arena);
                tell(p, Lang.get(_QUEUE_INFO, arena.getName(), manager.getQueuePosition(player)));
                tell(p, Lang.get(_QUEUE_LEAVE));
            }

            return; // finish
        }

        // Make sure the player is not already in an arena
        Arena currentArena = player.getArena();
        if (currentArena != null) {
            tellError(p, Lang.get(_CANT_JOIN_IN_GAME));
            return; // finish
        }

        String arenaName = args.getName("arenaName");

        Arena arena = getArena(sender, arenaName);
        if (arena == null)
            return; // finish


        if (!arena.getSettings().isVisible()) {
            tellError(p, Lang.get(_ARENA_NOT_FOUND, arenaName));
            return; // finish
        }

        if (!arena.getSettings().isEnabled()) {
            tellError(p, Lang.get(_ARENA_DISABLED, arenaName));
            return; // finish
        }

        if (arena.canJoin()) {
            arena.join(player);
            return; // finish
        }

        QueueManager manager = QueueManager.get(arena);
        if (manager.addPlayer(player)) {
            tellSuccess(p, Lang.get(_SUCCESS, arena.getName(), manager.getQueuePosition(player)));
        }
        else {
            tellError(p, Lang.get(_FAILED));
        }
    }
}

