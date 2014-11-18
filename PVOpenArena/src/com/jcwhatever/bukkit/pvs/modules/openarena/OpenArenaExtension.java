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


package com.jcwhatever.bukkit.pvs.modules.openarena;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.events.GenericsEventPriority;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.PlayerMeta;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.managers.LobbyManager;
import com.jcwhatever.bukkit.pvs.api.arena.managers.PlayerManager;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaStartReason;
import com.jcwhatever.bukkit.pvs.api.arena.options.RemovePlayerReason;
import com.jcwhatever.bukkit.pvs.api.events.ArenaPreStartEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerPreAddEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerPreJoinEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerRemovedEvent;
import com.jcwhatever.bukkit.pvs.api.events.region.PlayerEnterArenaRegionEvent;
import com.jcwhatever.bukkit.pvs.api.events.region.PlayerLeaveArenaRegionEvent;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;

import java.util.List;

@ArenaExtensionInfo(
        name = "PVOpenArena",
        description = "Allow players to join an arena at any time and the game starts immediately.")
public class OpenArenaExtension extends ArenaExtension implements GenericsEventListener {

    private static final String META_LEAVE = OpenArenaExtension.class.getName() + "META_LEAVE";
    private static final String META_ENTER = OpenArenaExtension.class.getName() + "META_ENTER";

    @Override
    protected void onEnable() {

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler(priority = GenericsEventPriority.HIGH, ignoreCancelled = true)
    private void onPlayerJoin(PlayerPreJoinEvent event) {

        // override default behavior of rejecting players
        // because the arena is already running.
        if (event.isCancelled() && getArena().getGameManager().isRunning()) {
            event.setCancelled(false); // un-cancel event
        }
    }

    /*
     *  Make sure game is started
     */
    @GenericsEventHandler
    private void onPlayerPreAdd(PlayerPreAddEvent event) {

        if (event.getReason() != AddPlayerReason.PLAYER_JOIN &&
                event.getReason() != AddPlayerReason.FORWARDING)
            return;

        if (!getArena().getGameManager().isRunning()) {

            ArenaScheduler.runTaskLater(getArena(), new Runnable() {
                @Override
                public void run() {
                    getArena().getGameManager().start(ArenaStartReason.AUTO);
                }
            });
        }
    }

    /*
     *  Make sure players added to lobby are automatically moved to the game.
     */
    @GenericsEventHandler
    private void onPlayerAdded(PlayerAddedEvent event) {

        if (event.getReason() != AddPlayerReason.PLAYER_JOIN &&
                event.getReason() != AddPlayerReason.FORWARDING)
            return;

        // prevent spawn teleport if player enters arena on foot
        PlayerMeta meta = event.getPlayer().getMeta();
        if (meta.get(META_ENTER) == true) {
            event.setSpawnLocation(null);
            meta.set(META_ENTER, null);
        }

        // check if game is already running
        if (!getArena().getGameManager().isRunning())
            return;

        PlayerManager manager = event.getPlayer().getRelatedManager();

        // auto forward player to game manager
        if (manager instanceof LobbyManager) {

            getArena().getLobbyManager().removePlayer(event.getPlayer(), RemovePlayerReason.ARENA_RELATION_CHANGE);
            getArena().getGameManager().addPlayer(event.getPlayer(), AddPlayerReason.ARENA_RELATION_CHANGE);
        }
    }

    /*
     *  Make sure ALL players in the lobby are added to the game
     */
    @GenericsEventHandler
    private void onArenaPreStart(ArenaPreStartEvent event) {
        List<ArenaPlayer> players = getArena().getLobbyManager().getPlayers();

        event.getJoiningPlayers().addAll(players);
    }

    /*
     *  Add players entering region to arena.
     */
    @GenericsEventHandler
    private void onPlayerEnterArena(PlayerEnterArenaRegionEvent event) {

        if (event.getPlayer().getArena() == null) {
            event.getPlayer().getMeta().set(META_ENTER, true);
            getArena().join(event.getPlayer());
        }
    }

    /*
     *  Remove players who leave arena region.
     */
    @GenericsEventHandler
    private void onPlayerLeaveArena(PlayerLeaveArenaRegionEvent event) {

        if (getArena().equals(event.getPlayer().getArena())) {

            event.getPlayer().getMeta().set(META_LEAVE, true);
            getArena().remove(event.getPlayer(), RemovePlayerReason.PLAYER_LEAVE);

        }
    }

    /*
     * Prevent location restore if the player is leaving the arena on foot.
     */
    @GenericsEventHandler
    private void onPlayerRemove(PlayerRemovedEvent event) {

        PlayerMeta meta = event.getPlayer().getMeta();

        if (meta.get(META_LEAVE) == true) {
            event.setRestoreLocation(null);
            meta.set(META_LEAVE, null);
        }
    }
}
