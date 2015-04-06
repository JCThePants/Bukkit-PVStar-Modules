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


package com.jcwhatever.pvs.modules.openarena;

import com.jcwhatever.nucleus.collections.players.PlayerSet;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.utils.MetaStore;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.context.ILobbyContext;
import com.jcwhatever.pvs.api.arena.context.IContextManager;
import com.jcwhatever.pvs.api.arena.options.AddToContextReason;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import com.jcwhatever.pvs.api.arena.options.ArenaStartReason;
import com.jcwhatever.pvs.api.events.ArenaIdleEvent;
import com.jcwhatever.pvs.api.events.ArenaPreStartEvent;
import com.jcwhatever.pvs.api.events.players.PlayerAddedToContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerLeaveArenaEvent;
import com.jcwhatever.pvs.api.events.players.PlayerPreAddToContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerPreJoinArenaEvent;
import com.jcwhatever.pvs.api.events.region.PlayerEnterArenaRegionEvent;
import com.jcwhatever.pvs.api.events.region.PlayerLeaveArenaRegionEvent;
import com.jcwhatever.pvs.api.utils.ArenaScheduler;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name = "PVOpenArena",
        description = "Allow players to join an arena at any time and the game starts immediately.")
public class OpenArenaExtension extends ArenaExtension implements IEventListener {

    private static final String META_LEAVE = OpenArenaExtension.class.getName() + "META_LEAVE";
    private static final String META_ENTER = OpenArenaExtension.class.getName() + "META_ENTER";

    // stores player-->arena so players entering an arena that is busy can be added
    // when it becomes idle.
    private static final PlayerSet _joinOnIdle = new PlayerSet(PVStarAPI.getPlugin(), 10);

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

    @EventMethod(priority = EventSubscriberPriority.HIGH, ignoreCancelled = true)
    private void onPlayerJoin(PlayerPreJoinArenaEvent event) {

        // override default behavior of rejecting players
        // because the arena is already running.
        if (event.isCancelled() && getArena().getGame().isRunning()) {
            event.setCancelled(false); // un-cancel event
        }
    }

    /*
     *  Make sure game is started
     */
    @EventMethod
    private void onPlayerPreAdd(PlayerPreAddToContextEvent event) {

        if (event.getReason() != AddToContextReason.PLAYER_JOIN &&
                event.getReason() != AddToContextReason.FORWARDING)
            return;

        if (!getArena().getGame().isRunning()) {

            ArenaScheduler.runTaskLater(getArena(), new Runnable() {
                @Override
                public void run() {
                    getArena().getGame().start(ArenaStartReason.AUTO);
                }
            });
        }
    }

    /*
     *  Make sure players added to lobby are automatically moved to the game.
     */
    @EventMethod
    private void onPlayerAdded(PlayerAddedToContextEvent event) {

        if (event.getReason() != AddToContextReason.PLAYER_JOIN &&
                event.getReason() != AddToContextReason.FORWARDING)
            return;

        // prevent spawn teleport if player enters arena on foot
        MetaStore meta = event.getPlayer().getMeta();
        if (meta.get(META_ENTER) == Boolean.TRUE) {
            event.setSpawnLocation(null);
            meta.set(META_ENTER, null);
        }

        // check if game is already running
        if (!getArena().getGame().isRunning())
            return;

        IContextManager manager = event.getPlayer().getContextManager();

        // auto forward player to game manager
        if (manager instanceof ILobbyContext) {

            event.getPlayer().changeContext(ArenaContext.GAME);
        }
    }

    /*
     *  Make sure ALL players in the lobby are added to the game
     */
    @EventMethod
    private void onArenaPreStart(ArenaPreStartEvent event) {
        IArenaPlayerCollection players = getArena().getLobby().getPlayers();

        event.getJoiningPlayers().addAll(players);
    }

    /*
     *  Add players entering region to arena.
     */
    @EventMethod
    private void onPlayerEnterArena(PlayerEnterArenaRegionEvent event) {

        if (event.getPlayer().getArena() == null) {

            if (getArena().isBusy()) {
                _joinOnIdle.add(event.getPlayer().getPlayer());
            }
            else {
                event.getPlayer().getMeta().set(META_ENTER, true);
                getArena().join(event.getPlayer());
            }
        }
    }

    /*
     *  Remove players who leave arena region.
     */
    @EventMethod
    private void onPlayerLeaveArena(PlayerLeaveArenaRegionEvent event) {

        // prevent removing player if they are simply dead
        if (event.getReason() == LeaveRegionReason.DEAD)
            return;

        if (getArena().equals(event.getPlayer().getArena())) {

            if (_joinOnIdle.contains(event.getPlayer().getPlayer())) {
                _joinOnIdle.remove(event.getPlayer().getPlayer());
            }
            else {
                event.getPlayer().getMeta().set(META_LEAVE, true);
                event.getPlayer().leaveArena();
            }
        }
    }

    /*
     * Prevent location restore if the player is leaving the arena on foot.
     */
    @EventMethod
    private void onPlayerRemove(PlayerLeaveArenaEvent event) {

        MetaStore meta = event.getPlayer().getMeta();

        if (meta.get(META_LEAVE) == Boolean.TRUE) {
            event.setRestoreLocation(null);
            meta.set(META_LEAVE, null);
        }
    }

    /*
     * Add players that were unable to join because the arena was busy.
     */
    @EventMethod
    private void onArenaIdle(@SuppressWarnings("unused") ArenaIdleEvent event) {

        synchronized (_joinOnIdle) {

            for (Player p : _joinOnIdle) {

                IArenaPlayer player = PVStarAPI.getArenaPlayer(p);

                if (player.getArena() != null)
                    continue;

                if (!getArena().getRegion().contains(p.getLocation()))
                    continue;

                player.getMeta().set(META_ENTER, true);
                getArena().join(player);
            }
        }

        _joinOnIdle.clear();
    }
}
