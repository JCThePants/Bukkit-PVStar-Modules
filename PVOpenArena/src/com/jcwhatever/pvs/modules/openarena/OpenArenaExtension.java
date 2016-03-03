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
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.IBukkitPlayer;
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
import com.jcwhatever.pvs.api.events.players.PlayerAddToContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerLeaveArenaEvent;
import com.jcwhatever.pvs.api.events.players.PlayerPreAddToContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerPreJoinArenaEvent;
import com.jcwhatever.pvs.api.events.region.PlayerEnterArenaRegionEvent;
import com.jcwhatever.pvs.api.events.region.PlayerLeaveArenaRegionEvent;
import com.jcwhatever.pvs.api.utils.ArenaScheduler;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name = "PVOpenArena",
        description = "Allow players to join an arena at any time and the game starts immediately.")
public class OpenArenaExtension extends ArenaExtension implements IEventListener {

    private static final String META_LEAVE = OpenArenaExtension.class.getName() + "META_LEAVE";
    private static final String META_ENTER = OpenArenaExtension.class.getName() + "META_ENTER";
    private static final String META_TRANSFER = OpenArenaExtension.class.getName() + "META_TRANSFER";
    private static final Location PLAYER_LOCATION = new Location(null, 0, 0, 0);

    // stores player-->arena so players entering an arena that is busy can be added
    // when it becomes idle.
    private static final PlayerSet _joinOnIdle = new PlayerSet(PVStarAPI.getPlugin(), 10);

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

    @EventMethod(priority = EventSubscriberPriority.HIGH, invokeForCancelled = true)
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
    private void onPlayerAdded(PlayerAddToContextEvent event) {

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

        IArenaPlayer arenaPlayer = event.getPlayer();
        if (!(arenaPlayer instanceof IBukkitPlayer))
            return;

        IArena arena = event.getArena();
        IArena current = arenaPlayer.getArena();
        if (current != null) {

            if (arena.equals(current)) {
                return;
            }

            arenaPlayer.getMeta().set(META_TRANSFER, true);
            arenaPlayer.leaveArena();

            // make sure player is still in arena after leaving previous one
            if (!arena.getRegion().contains(arenaPlayer.getLocation(PLAYER_LOCATION)))
                return;
        }

        if (getArena().isBusy()) {
            _joinOnIdle.add(((IBukkitPlayer) arenaPlayer).getPlayer());
        }
        else {
            arenaPlayer.getMeta().set(META_ENTER, true);
            getArena().join(arenaPlayer);
        }
    }

    /*
     *  Remove players who leave arena region.
     */
    @EventMethod
    private void onPlayerLeaveArena(PlayerLeaveArenaRegionEvent event) {

        if (!(event.getPlayer() instanceof IBukkitPlayer))
            return;

        IBukkitPlayer bukkitPlayer = (IBukkitPlayer)event.getPlayer();

        // prevent removing player if they are simply dead
        if (event.getReason() == LeaveRegionReason.DEAD)
            return;

        if (getArena().equals(bukkitPlayer.getArena())) {

            if (_joinOnIdle.contains(bukkitPlayer.getPlayer())) {
                _joinOnIdle.remove(bukkitPlayer.getPlayer());
            }
            else {
                bukkitPlayer.getMeta().set(META_LEAVE, true);
                bukkitPlayer.leaveArena();
            }
        }
    }

    /*
     * Prevent location restore if the player is leaving the arena on foot
     * or if leaving due to transfer to another open arena.
     */
    @EventMethod
    private void onPlayerRemove(PlayerLeaveArenaEvent event) {

        MetaStore meta = event.getPlayer().getMeta();

        if (meta.get(META_LEAVE) == Boolean.TRUE ||
                meta.get(META_TRANSFER) == Boolean.TRUE) {
            event.setRestoreLocation(null);
            meta.set(META_LEAVE, null);
            meta.set(META_TRANSFER, null);
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
