/* This file is part of PV-Star Modules: PVSubRegions for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.api.utils.Converters;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RegionTypeInfo(
        name="forwarding",
        description="Forward players to another arena.")
public class ForwardingRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();


    static {
        _possibleSettings
                .set("forward-to-arena", ValueType.UUID, "Set the arena to forward to.")
                .set("do-teleport", true, ValueType.BOOLEAN, "Set flag for teleporting to the arena.")
                .set("teleport-region", ValueType.STRING, "Set destination special region to teleport player to. Overrides arenas teleport if set. Region must be in the arena specified in 'forward-to-arena' setting.")
                .set("yaw-adjust", 0.0D, ValueType.DOUBLE, "Used by 'teleport-region' setting. Adjust the players yaw position when teleported.")
                .setValueConverter("forward-to-arena", Converters.ARENA_NAME_ID)
        ;
    }

    private Arena _forwardArena;
    private boolean _doTeleport = true;
    private AbstractPVRegion _destinationRegion;
    private float _yawAdjust = 0.0F;

    private Map<UUID, Location> _forwardLocMap = new HashMap<>(10);
    private Map<UUID, Vector> _vectorMap = new HashMap<>(10);

    @GenericsEventHandler
    private void onPlayerAdded(final PlayerAddedEvent event) {

        Location location = _forwardLocMap.remove(event.getPlayer().getUniqueId());
        if (location == null)
            return;

        event.setSpawnLocation(location);

        final Vector vector = _vectorMap.remove(event.getPlayer().getUniqueId());
        if (vector == null)
            return;

        ArenaScheduler.runTaskLater(getArena(), 1, new Runnable() {

            @Override
            public void run() {
                 event.getPlayer().getHandle().setVelocity(vector);
            }
        });

    }

    @Override
    protected boolean canDoPlayerEnter(Player p) {
        return _forwardArena != null && super.canDoPlayerEnter(p);
    }

    @Override
    protected void onPlayerEnter(final ArenaPlayer player) {

        boolean doRegionTeleport = _doTeleport &&
                                   _destinationRegion != null &&
                                   _destinationRegion.isDefined();

        if (doRegionTeleport) {
            Location destination = getRegionDestination(player);
            _forwardLocMap.put(player.getUniqueId(), destination);

            Vector vector = player.getHandle().getVelocity();
            _vectorMap.put(player.getUniqueId(), vector);
        }

        getArena().getGameManager().forwardPlayer(player, _forwardArena);
    }

    @Override
    protected boolean canDoPlayerLeave(Player p) {
        return false;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
        // do nothing
    }

    @Override
    protected boolean onTrigger() {
        return false;
    }

    @Override
    protected boolean onUntrigger() {
        return false;
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
        setIsPlayerWatcher(true);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
        setIsPlayerWatcher(false);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {

        // get forwarding arena
        UUID arenaId = dataNode.getUUID("forward-to-arena");
        if (arenaId != null)
            _forwardArena = PVStarAPI.getArenaManager().getArena(arenaId);

        // teleport settings
        _doTeleport = dataNode.getBoolean("do-teleport", _doTeleport);

        // player yaw adjustment
        _yawAdjust = (float)dataNode.getDouble("yaw-adjust", _yawAdjust);

        // get destination region
        if (_forwardArena != null) {
            String regionName = dataNode.getString("teleport-region");
            if (regionName != null) {

                // search own region for destination region
                _destinationRegion = getModule().getManager(getArena()).getRegion(regionName);

                // search destination arena for destination region
                if (_destinationRegion == null) {
                    _destinationRegion = getModule().getManager(_forwardArena).getRegion(regionName);
                }
            }
        }
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    private Location getRegionDestination(ArenaPlayer p) {
        Location pLoc = p.getLocation();

        double x = pLoc.getX() - getXStart();
        double y = pLoc.getY() - getYStart();
        double z = pLoc.getZ() - getZStart();

        double dx = _destinationRegion.getXStart() + x;
        double dy = _destinationRegion.getYStart() + y;
        double dz = _destinationRegion.getZStart() + z;

        float dyaw = (pLoc.getYaw() + _yawAdjust) % 360;

        return new Location(getWorld(), dx, dy, dz, dyaw, pLoc.getPitch());
    }
}
