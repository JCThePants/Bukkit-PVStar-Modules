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


package com.jcwhatever.bukkit.pvs.modules.messages;

import com.jcwhatever.bukkit.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.manager.IGenericsEventListener;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaTeam;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerReadyEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerWinEvent;
import com.jcwhatever.bukkit.pvs.api.events.team.TeamWinEvent;

@ArenaExtensionInfo(
        name = "PVMessages",
        description = "Adds basic status and event messages to an arena.")

public class MessagesExtension extends ArenaExtension implements IGenericsEventListener {

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onPlayerAdded(PlayerAddedEvent event) {
        if (event.getReason() == AddPlayerReason.ARENA_RELATION_CHANGE)
            return;

        if (event.getMessage() != null)
            return;

        event.setMessage(event.getPlayer().getName() + " joined.");
    }

    @GenericsEventHandler
    private void onPlayerWin(PlayerWinEvent event) {
        if (event.getWinMessage() == null ||
                event.getPlayer().getTeam() != ArenaTeam.NONE) {
            return;
        }

        event.setWinMessage(event.getPlayer().getName() + " wins!");
    }

    @GenericsEventHandler
    private void onTeamWin(TeamWinEvent event) {

        if (event.getWinMessage() != null)
            return;

        ArenaTeam team = event.getTeam();

        event.setWinMessage(team.getTextColor() + team.getDisplay() + " wins!");
    }

    @GenericsEventHandler
    private void onPlayerReady(PlayerReadyEvent event) {
        if (event.getMessage() != null)
            return;

        event.setMessage(
                event.getPlayer().getTeam().getTextColor() +
                        event.getPlayer().getName() + "{WHITE} is ready to start.");
    }

    @GenericsEventHandler
    private void onPlayerLose(PlayerLoseEvent event) {
        if (event.getLoseMessage() != null)
            return;

        event.setLoseMessage("{RED}" + event.getPlayer().getName() + " died.");
    }


}
