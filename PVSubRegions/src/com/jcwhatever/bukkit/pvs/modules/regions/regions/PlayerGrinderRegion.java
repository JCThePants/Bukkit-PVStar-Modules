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


package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.player.collections.PlayerMap;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaBatchScheduler;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BlockIterator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RegionTypeInfo(
        name="playergrinder",
        description="Region with rotating explosions.")
public class PlayerGrinderRegion extends AbstractPVRegion implements GenericsEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
                .set("blade-count", 2, ValueType.INTEGER, "Set the number rotating blades.")
                .set("blade-speed", 10, ValueType.INTEGER, "Set degrees per delay time that the blades move.")
                .set("blade-delay", 10, ValueType.INTEGER, "Set delay in ticks before the blade moves.")
                .set("blade-height", 4, ValueType.INTEGER, "Set height of blade.")
                .set("blade-radius", -1, ValueType.INTEGER, "Set the radius of the blade. -1 to fill the region. Blade will never go outside of region.")
                .set("damage", 20.0D, ValueType.DOUBLE, "Set amount of damage done to players.")
        ;
    }


    private int _bladeCount = 2;
    private int _bladeSpeed = 10;
    private int _bladeDelay = 10;
    private int _bladeHeight = 4;
    private int _bladeRadius = -1;
    private double _damage = 20;
    private final Object _sync = new Object();
    private ArenaBatchScheduler _bladeTasks;

    private int _bladesRendered = 0;

    private Set<ArenaPlayer> _playersInRegion = new HashSet<>(25);
    private Map<UUID, Set<Location>> _cachedMoveLocations = new PlayerMap<Set<Location>>();

    public PlayerGrinderRegion(String name) {
        super(name);
    }


    public void resetBlades() {

        if (_bladeTasks != null) {
            _bladeTasks.cancelAll();
            _bladeTasks = null;
        }

        synchronized(_sync) {
            _playersInRegion.clear();
        }
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        synchronized(_sync) {
            _playersInRegion.add(player);
        }

        if (_bladeTasks != null)
            return;


        _bladeTasks = new ArenaBatchScheduler(getArena());

        int bladeSpacing = 360 / _bladeCount;

        for (int i = 0; i < _bladeCount; i++) {
            _bladeTasks.runTaskRepeat(10, _bladeDelay, new RotateBlade(this, i * bladeSpacing));
        }
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
        synchronized(_sync) {
            _playersInRegion.remove(player);
        }

        if (_bladeTasks != null && _playersInRegion.isEmpty()) {
            _bladeTasks.cancelAll();
            _bladeTasks = null;
        }
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
        setIsPlayerWatcher(true);
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        setIsPlayerWatcher(false);
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {
        _bladeCount = Math.min(20, dataNode.getInteger("blade-count", _bladeCount));
        _bladeSpeed = dataNode.getInteger("blade-speed", _bladeSpeed);
        _bladeDelay = dataNode.getInteger("blade-delay", _bladeDelay);
        _bladeHeight = dataNode.getInteger("blade-height", _bladeHeight);
        _bladeRadius = dataNode.getInteger("blade-radius", _bladeRadius);
        _damage = dataNode.getDouble("damage", _damage);
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @GenericsEventHandler
    private void onPlayerMove(PlayerMoveEvent event) {

        if (!contains(event.getFrom()))
            return;

        synchronized (_sync) {

            Set<Location> locations = _cachedMoveLocations.get(event.getPlayer().getUniqueId());

            if (locations == null) {
                locations = new HashSet<Location>(30);
                _cachedMoveLocations.put(event.getPlayer().getUniqueId(), locations);
            }

            locations.add(LocationUtils.getBlockLocation(event.getPlayer().getLocation()));
        }
    }

    private class RotateBlade implements Runnable {

        private Location origin;
        private int distance;
        private World world;
        private int height;
        private PlayerGrinderRegion _region;

        public RotateBlade (PlayerGrinderRegion region, float startYaw) {
            _region = region;

            origin = _region.getCenter();
            //noinspection ConstantConditions
            origin.setY(_region.getYStart());
            origin.setPitch(-90);
            origin.setYaw(startYaw);

            distance = _bladeRadius == -1
                    ? Math.max(_region.getXWidth(), _region.getZWidth())
                    : _bladeRadius;

            world = origin.getWorld();

            height = _region.getYStart() + _bladeHeight;
        }

        @Override
        public void run() {

            synchronized (_sync) {
                float yaw = origin.getYaw() + _bladeSpeed;
                float pitch = Math.min(origin.getPitch() + 5, 0);
                origin.setYaw(yaw);
                origin.setPitch(pitch);

                Set<Location> bladeLocations = new HashSet<Location>(height * distance * (Math.abs(origin.getBlockX() - origin.getBlockZ())));

                for (int y = _region.getYStart(); y <= height; y++) {

                    BlockIterator bit = new BlockIterator(world,
                            new Location(world, origin.getBlockX(), y, origin.getBlockZ()).toVector(),
                            origin.getDirection(),
                            0, distance);

                    Block next;
                    while(bit.hasNext())
                    {
                        next = bit.next();
                        if (next == null)
                            continue;

                        Location nextLocation = next.getLocation();

                        if (!_region.contains(nextLocation, true, false, true))
                            break;

                        if (next.getType() != Material.AIR)
                            continue;

                        nextLocation.getWorld().createExplosion(nextLocation, 0.0F, false);

                        if (pitch == 0) {
                            bladeLocations.add(LocationUtils.getBlockLocation(nextLocation));
                        }
                    }
                }

                for (ArenaPlayer player : _playersInRegion) {

                    Set<Location> locations = _cachedMoveLocations.get(player.getUniqueId());
                    if (locations == null || locations.isEmpty()) {

                        Location currentLocation = LocationUtils.getBlockLocation(player.getLocation());

                        if (bladeLocations.contains(currentLocation) ||
                                bladeLocations.contains(currentLocation.add(0, 1, 0))) {
                            damagePlayer(player);
                        }

                        continue;
                    }

                    for (Location location : locations) {
                        if (bladeLocations.contains(location)) {

                            damagePlayer(player);
                            break;
                        }
                    }
                }

                _bladesRendered++;

                if (_bladesRendered == _bladeCount) {
                    _bladesRendered = 0;
                    _cachedMoveLocations.clear();
                }
            }

        }

        private void damagePlayer(ArenaPlayer player) {
            if (player != null) {

                // don't damage invulnerable players
                if (player.isInvulnerable()) {
                    return;
                }

                // damage players
                player.getHandle().damage(_damage);
            }
        }

    }
}
