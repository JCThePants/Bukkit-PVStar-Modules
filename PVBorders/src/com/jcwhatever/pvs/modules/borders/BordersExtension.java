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


package com.jcwhatever.pvs.modules.borders;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import com.jcwhatever.pvs.api.events.region.PlayerEnterArenaRegionEvent;
import com.jcwhatever.pvs.api.events.region.PlayerLeaveArenaRegionEvent;
import com.jcwhatever.pvs.api.utils.Msg;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name="PVBorders",
        description="Add arena region entry and exit handling.")
public class BordersExtension extends ArenaExtension implements IEventListener {

    private final Location PREVENT_MOVE_LOCATION = new Location(null, 0, 0, 0);
    private OutOfBoundsAction _outOfBoundsAction = OutOfBoundsAction.NONE;
    private OutsidersAction _outsidersAction = OutsidersAction.NONE;

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    /*
     * Get action to take when player leaves the arena region.
     */
    public OutOfBoundsAction getOutOfBoundsAction() {
        return _outOfBoundsAction;
    }

    /*
     * Set out of bounds action.
     */
    public void setOutOfBoundsAction(OutOfBoundsAction action) {
        PreCon.notNull(action);

        _outOfBoundsAction = action;

        getDataNode().set("out-of-bounds", action);
        getDataNode().save();
    }

    /*
     * Get the action to take when an outsider enters the
     * arena region.
     */
    public OutsidersAction getOutsidersAction() {
        return _outsidersAction;
    }

    /*
     * Set the action to take when an outsider enters the
     * arena region.
     */
    public void setOutsidersAction(OutsidersAction action) {
        PreCon.notNull(action);

        getDataNode().set("outsiders-action", action);
        getDataNode().save();
    }

    @Override
    protected void onEnable() {

        _outOfBoundsAction = getDataNode().getEnum("out-of-bounds", _outOfBoundsAction, OutOfBoundsAction.class);
        _outsidersAction = getDataNode().getEnum("outsiders-action", _outsidersAction, OutsidersAction.class);

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onPlayerMove(PlayerMoveEvent event) {
        if (getOutsidersAction() != OutsidersAction.PREVENT)
            return;

        IArena arena = getArena();

        Location to = event.getTo();
        if (!arena.getRegion().contains(to))
            return;

        Player player = event.getPlayer();
        IArenaPlayer arenaPlayer = PVStarAPI.getArenaPlayer(player);

        if (arena.equals(arenaPlayer.getArena()))
            return;

        event.setCancelled(true);
    }

    @EventMethod
    private void onPlayerEnterRegion(PlayerEnterArenaRegionEvent event) {

        if (getOutsidersAction() == OutsidersAction.NONE ||
                getOutsidersAction() == OutsidersAction.PREVENT)
            return;

        if (!getArena().getGame().isRunning())
            return;

        final IArenaPlayer player = event.getPlayer();
        if (getArena().equals(player.getArena()))
            return;

        // check again later, gives time for leaving players to be removed from arena
        Scheduler.runTaskLater(PVStarAPI.getPlugin(), 5, new Runnable() {

            @Override
            public void run() {

                if (!getArena().getRegion().contains(player.getLocation()))
                    return;

                OutsidersAction action = getOutsidersAction();

                switch (action) {
                    case NONE:
                        // do nothing
                        return;

                    case JOIN:
                        getArena().join(player);
                        break;

                    case KICK:
                        Location kickLocation = getArena().getSettings().getRemoveLocation();

                        player.teleport(kickLocation);
                        Msg.tellError(player, "You're not allowed inside the arena during a match.");
                        break;
                }
            }
        });
    }

    @EventMethod(priority = EventSubscriberPriority.LAST)
    private void onPlayerLeaveRegionPrevent(PlayerMoveEvent event) {

        if (getOutOfBoundsAction() != OutOfBoundsAction.PREVENT)
            return;

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getPlayer());
        if (player == null)
            return;

        IArena arena = player.getArena();
        if (arena == null)
            return;

        Location to = event.getTo();

        if (arena.getRegion().contains(to))
            return;

        Location from = event.getFrom();
        Location prevent = PREVENT_MOVE_LOCATION;

        prevent.setWorld(from.getWorld());
        prevent.setX(from.getX());
        prevent.setY(from.getY());
        prevent.setZ(from.getZ());
        prevent.setYaw(to.getYaw());
        prevent.setPitch(to.getPitch());
        event.setTo(prevent);
    }

    @EventMethod
    private void onPlayerLeaveRegion(PlayerLeaveArenaRegionEvent event) {

        if (getOutOfBoundsAction() == OutOfBoundsAction.NONE ||
                getOutOfBoundsAction() == OutOfBoundsAction.PREVENT)
            return;

        if (!getArena().getGame().isRunning())
            return;

        final IArenaPlayer player = event.getPlayer();
        if (!getArena().equals(player.getArena()))
            return;

        if (getArena().equals(player.getArena()) && player.getContext() == ArenaContext.GAME) {

            switch (getOutOfBoundsAction()) {

                case KICK:
                    player.kick();
                    Msg.tellError(player, "Kicked for leaving the arena.");
                    break;

                case WIN:
                    if (!getArena().getGame().isGameOver())
                        getArena().getGame().setWinner(player);
                    break;

                case LOSE:
                    if (!getArena().getGame().isGameOver())
                        player.loseGame();
                    break;

                case RESPAWN:
                    player.respawn();
                    break;

                default:
                    break;

            }
        }
    }
}
