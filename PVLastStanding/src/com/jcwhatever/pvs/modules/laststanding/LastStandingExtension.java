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

package com.jcwhatever.pvs.modules.laststanding;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.arena.ArenaTeam;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.managers.IGameManager;
import com.jcwhatever.pvs.api.arena.options.RemovePlayerReason;
import com.jcwhatever.pvs.api.events.players.PlayerRemovedEvent;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

@ArenaExtensionInfo(
        name="PVLastStanding",
        description = "Declares the last player or team in an arena the winner.")
public class LastStandingExtension extends ArenaExtension implements IEventListener {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onAttach() {
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onRemove() {
        getArena().getEventManager().unregister(this);
    }

    /*
     * Check for a winner when a player is removed.
     */
    @EventMethod
    private void onCheckForWinner(PlayerRemovedEvent event) {

        if (event.getReason() == RemovePlayerReason.ARENA_RELATION_CHANGE ||
                event.getReason() == RemovePlayerReason.FORWARDING)
            return;

        if (!(event.getRelatedManager() instanceof IGameManager))
            return;

        IGameManager manager = (IGameManager)event.getRelatedManager();

        if (manager.isRunning() && !manager.isGameOver()) {

            // check for team winner
            if (getArena().getTeamManager().totalTeams() > 0) {

                if (getArena().getTeamManager().totalCurrentTeams() == 1) {
                    List<ArenaTeam> teams = new ArrayList<>(getArena().getTeamManager().getCurrentTeams());

                    manager.setWinner(teams.get(0));
                    return; // finished
                }
            }

            // Check for player winner
            IArenaPlayer winner = checkForWinnerOnRemove(event.getPlayer());
            if (winner != null) {
                manager.setWinner(winner);
            }
        }
    }

    /*
     * Checks to see if a player should be declared the winner
     * after the specified player is removed.
     */
    @Nullable
    private IArenaPlayer checkForWinnerOnRemove(IArenaPlayer removedPlayer) {

        IGameManager manager = getArena().getGameManager();

        IArenaPlayerCollection players = manager.getPlayers();

        if (players.size() == 1) {

            Iterator<IArenaPlayer> iterator = players.iterator();
            iterator.hasNext();

            IArenaPlayer winner = iterator.next();
            if (!winner.equals(removedPlayer)) {
                return winner;
            }
        }
        return null;
    }
}
