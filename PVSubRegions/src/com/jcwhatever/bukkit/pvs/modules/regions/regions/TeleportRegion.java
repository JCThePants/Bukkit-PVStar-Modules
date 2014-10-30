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

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.api.utils.SpawnFilter;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@RegionTypeInfo(
        name="teleport",
        description="Teleport players that enter the region.")
public class TeleportRegion extends AbstractPVRegion {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("teleport-to", ValueType.LOCATION, "Set destination location to teleport player to.")
                .set("teleport-to-spawn", ValueType.STRING, "Set destination spawnpoints to teleport player to.")
                .set("teleport-to-region", ValueType.STRING, "Set destination region.")
                .set("yaw-adjust", 0.0D, ValueType.DOUBLE, "Used by 'teleport-region' setting. Adjust the players yaw position when teleported.")
        ;
    }

    private AbstractPVRegion _destinationRegion;
    private List<Spawnpoint> _spawnpoints;
    private Location _location;
    private float _yawAdjust = 0.0F;
    private int _available = 0;

    public TeleportRegion(String name) {
        super(name);
    }

    @Override
    protected boolean canDoPlayerEnter(Player p) {
        return isEnabled() && _available > 0 && super.canDoPlayerEnter(p);
    }

    @Override
    protected void onPlayerEnter(final ArenaPlayer player) {

        List<Location> locations = new ArrayList<Location>(_spawnpoints.size() + 3);

        if (_destinationRegion != null) {
            locations.add(getRegionDestination(player));
        }

        if (_spawnpoints != null) {
            Spawnpoint spawn = SpawnFilter.getRandomSpawn(player.getTeam(), _spawnpoints);
            if (spawn != null) {
                locations.add(spawn);
            }
        }

        if (_location != null)
            locations.add(_location);

        Location tpLocation = Rand.get(locations);

        if (tpLocation != null) {

            if (_yawAdjust != 0.0F) {
                float yaw = (tpLocation.getYaw() + _yawAdjust) % 360;
                tpLocation.setYaw(yaw);
            }
        }

        final Vector v = player.getHandle().getVelocity();
        player.getHandle().teleport(tpLocation, TeleportCause.PLUGIN);

        ArenaScheduler.runTaskLater(getArena(), 1, new Runnable() {

            @Override
            public void run() {
                player.getHandle().setVelocity(v);
            }

        });
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
        // do nothing
    }

    @Override
    protected void onDisable() {
        // do nothing
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _yawAdjust = (float)dataNode.getDouble("yaw-adjust", _yawAdjust);

        _available = 0;

        String regionName = dataNode.getString("teleport-to-region");
        if (regionName != null) {
            _destinationRegion = getModule().getManager(getArena()).getRegion(regionName);

            if (_destinationRegion != null)
                _available++;
        }

        String spawnNames = dataNode.getString("teleport-to-spawn");
        if (spawnNames != null) {
            String[] namesComp = TextUtils.PATTERN_COMMA.split(spawnNames);
            _spawnpoints = new ArrayList<>(namesComp.length);

            for (String name : namesComp) {
                if (name.isEmpty())
                    continue;

                Spawnpoint spawnpoint = getArena().getSpawnManager().getSpawn(name.trim());
                if (spawnpoint == null)
                    continue;

                _spawnpoints.add(spawnpoint);
                _available++;
            }
        }

        _location = dataNode.getLocation("teleport-to");

        if (_location != null)
            _available++;
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    private Location getRegionDestination(ArenaPlayer player) {
        Location pLoc = player.getLocation();

        double x = pLoc.getX() - getXStart();
        double y = pLoc.getY() - getYStart();
        double z = pLoc.getZ() - getZStart();

        double dx = _destinationRegion.getXStart() + x;
        double dy = _destinationRegion.getYStart() + y;
        double dz = _destinationRegion.getZStart() + z;

        float dyaw = pLoc.getYaw();

        return new Location(getWorld(), dx, dy, dz, dyaw, pLoc.getPitch());
    }
}
