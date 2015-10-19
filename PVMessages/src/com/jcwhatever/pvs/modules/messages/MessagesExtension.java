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


package com.jcwhatever.pvs.modules.messages;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.managed.actionbar.ActionBarPriority;
import com.jcwhatever.nucleus.managed.actionbar.ActionBars;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.text.components.IChatMessage;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.ArenaTeam;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.events.players.PlayerAddToLobbyEvent;
import com.jcwhatever.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.pvs.api.events.players.PlayerReadyEvent;
import com.jcwhatever.pvs.api.events.players.PlayerWinEvent;
import com.jcwhatever.pvs.api.events.team.TeamWinEvent;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name = "PVMessages",
        description = "Adds basic status and event messages to an arena.")

public class MessagesExtension extends ArenaExtension implements IEventListener {

    @Localizable static final String _JOINED =
            "{GRAY}{0: player name} joined.";

    @Localizable static final String _PLAYER_WIN =
            "{GREEN}{0: player name} wins!";

    @Localizable static final String _PLAYER_WIN_GLOBAL =
            "{GREEN}{0: player name} won the match in {1: arena name}!";

    @Localizable static final String _TEAM_WIN =
            "{GREEN}{0: team name} wins!";

    @Localizable static final String _READY =
            "{LIGHT_PURPLE}{0: player name} is ready to start. {GRAY}({1: votes required} more needed)";

    @Localizable static final String _PLAYER_LOSE =
            "{RED}{0: player name} lost.";

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onPlayerAdded(PlayerAddToLobbyEvent event) {
        if (event.getMessage() != null)
            return;

        event.setMessage(Lang.get(_JOINED, event.getPlayer().getName()));
    }

    @EventMethod
    private void onPlayerWin(PlayerWinEvent event) {
        if (event.getWinMessage() != null ||
                event.getPlayer().getTeam() != ArenaTeam.NONE) {
            return;
        }

        event.setWinMessage(Lang.get(_PLAYER_WIN, event.getPlayer().getName()));

        IChatMessage globalMessage = Lang.get(_PLAYER_WIN_GLOBAL, event.getPlayer().getName(), getArena().getName());
        if (globalMessage.length() != 0)
            ActionBars.showTo(Bukkit.getOnlinePlayers(), globalMessage, ActionBarPriority.LOW);
    }

    @EventMethod
    private void onTeamWin(TeamWinEvent event) {

        if (event.getWinMessage() != null)
            return;

        ArenaTeam team = event.getTeam();

        event.setWinMessage(Lang.get(_TEAM_WIN, team.getTextColor() + team.getDisplay()));
    }

    @EventMethod
    private void onPlayerReady(PlayerReadyEvent event) {
        if (event.getMessage() != null)
            return;

        int totalPlayers = event.getArena().getLobby().getPlayers().size();
        int totalNeeded = totalPlayers - event.getTotalReady();

        event.setMessage(Lang.get(_READY,
                event.getPlayer().getTeam().getTextColor() + event.getPlayer().getName(), totalNeeded));
    }

    @EventMethod
    private void onPlayerLose(PlayerLoseEvent event) {
        if (event.getLoseMessage() != null)
            return;

        event.setLoseMessage(Lang.get(_PLAYER_LOSE, event.getPlayer().getName()));
    }
}
