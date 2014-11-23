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

import com.jcwhatever.bukkit.generic.events.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.LocationUtils;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Scheduler.ScheduledTask;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BlockIterator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

@RegionTypeInfo(
        name="playergrinder",
        description="Region with rotating explosions.")
public class PlayerGrinderRegion extends AbstractPVRegion implements IGenericsEventListener {

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
    private ScheduledTask _bladeTask;
    private BladeRotator _bladeRotator;

    private Set<ArenaPlayer> _playersInRegion = new HashSet<>(25);

    public PlayerGrinderRegion(String name) {
        super(name);
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player) {
        synchronized(_sync) {
            _playersInRegion.add(player);
        }

        runBlades();
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player) {
        synchronized(_sync) {
            _playersInRegion.remove(player);
        }

        if (_bladeTask != null && _playersInRegion.isEmpty()) {
            _bladeTask.cancel();
            _bladeTask = null;
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

        resetBlades();
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onCoordsChanged(Location p1, Location p2) {
        resetBlades();
    }

    /*
     * Reset blade rotator and, if the rotator is running,
     * restart it.
     */
    private void resetBlades() {
        _bladeRotator = null;

        if (_bladeTask != null) {
            _bladeTask.cancel();
            _bladeTask = null;

            runBlades();
        }
    }

    /*
     * Begin rotating blades (if not already started)
     */
    private void runBlades() {
        if (_bladeTask != null)
            return;

        if (_bladeRotator == null) {
            _bladeRotator = new BladeRotator(this);
        }

        _bladeRotator.reset();

        _bladeTask = ArenaScheduler.runTaskRepeat(getArena(), 10, _bladeDelay, _bladeRotator);
    }


    /*
     * Contains locations where explosions occur for a single blade.
     */
    private static class Blade {
        private float _yaw;
        private float _pitch;
        private List<Location> _locations;
        private Set<Location> _blockLocations;

        Blade(float yaw, float pitch, int size) {
            _yaw = yaw;
            _pitch = pitch;
            _locations = new ArrayList<>(size);
            _blockLocations = new HashSet<>(size);
        }

        float getYaw() {
            return _yaw;
        }

        float getPitch() {
            return _pitch;
        }

        void add(Location location) {
            _locations.add(location);
            _blockLocations.add(LocationUtils.getBlockLocation(location));

        }

        void explode() {
            for (Location location : _locations) {
                location.getWorld().createExplosion(location, 0.0F, false);
            }
        }

        boolean contains(Location location) {
            return _blockLocations.contains(location);
        }
    }

    /*
     * Generates and rotates blades when scheduled for a repeating task.
     */
    private static class BladeRotator implements Runnable {

        private final PlayerGrinderRegion _region;
        private final Location _origin;
        private final World _world;
        private int _distance;
        private int _height;
        protected int _bladeSpacing;
        protected int _bladeCount;
        private Map<String, Blade> _blades;
        private int _currentYaw = 0;
        private int _currentPitch = -90;
        private volatile boolean _isGenerating;

        BladeRotator(PlayerGrinderRegion region) {
            _region = region;
            _world = region.getWorld();
            _origin = region.getCenter();

            _distance = _region._bladeRadius == -1
                    ? Math.max(_region.getXWidth(), _region.getZWidth())
                    : _region._bladeRadius;

            _height = _region.getYStart() + _region._bladeHeight;

            _bladeCount = _region._bladeCount;
            _bladeSpacing = 360 / _region._bladeCount;
        }

        @Override
        public void run() {

            if (_isGenerating)
                return;

            if (_blades == null) {
                generateBlades();
                return;
            }

            for (int i = 0; i < _bladeCount; i++) {
                int yaw = getYaw(_currentYaw + (i * _bladeSpacing));
                displayBlade(yaw, _currentPitch);
            }

            _currentPitch = Math.min(_currentPitch + 5, 0);
            _currentYaw = getYaw(_currentYaw + _region._bladeSpeed);
        }

        private void generateBlades() {
            _isGenerating = true;

            Scheduler.runTaskLaterAsync(PVStarAPI.getPlugin(), 1, new Runnable() {
                @Override
                public void run() {

                    int pitchIncrement = 5;
                    _blades = new HashMap<>(360);
                    _currentYaw = 0;
                    _currentPitch = -90;
                    boolean isFinished = false;

                    while (!isFinished) {

                        for (int i = 0; i < _bladeCount; i++) {

                            int yaw = getYaw(_currentYaw + (i * _bladeSpacing));

                            String bladeKey = getBladeKey(yaw, _currentPitch);
                            if (_blades.containsKey(bladeKey)) {
                                isFinished = true;
                                break;
                            }

                            Blade blade = createBlade(nextBlade(yaw, _currentPitch));
                            _blades.put(bladeKey, blade);
                        }

                        _currentYaw = getYaw(_currentYaw + _region._bladeSpeed);
                        _currentPitch = Math.min(_currentPitch + pitchIncrement, 0);
                    }

                    _isGenerating = false;

                    reset();
                }
            });
        }

        @Nullable
        private Blade getBlade(int yaw, int pitch) {
            String bladeKey = getBladeKey(yaw, pitch);
            return _blades.get(bladeKey);
        }

        public void reset() {
            if (_isGenerating)
                return;

            _currentPitch = -90;
            _currentYaw = 0;
        }

        Location nextBlade(int yaw, int pitch) {
            Location bladeLocation = _origin.clone();
            bladeLocation.setY(_region.getYStart());
            bladeLocation.setPitch(pitch);
            bladeLocation.setYaw(yaw);

            return bladeLocation;
        }

        Blade createBlade(Location bladeLocation) {

            Blade blade = new Blade(bladeLocation.getYaw(), bladeLocation.getPitch(), 50);

            for (int y = _region.getYStart(); y < _height; y++) {

                BlockIterator bit = new BlockIterator(_world,
                        new Location(_world, bladeLocation.getBlockX(), y, bladeLocation.getBlockZ()).toVector(),
                        bladeLocation.getDirection(),
                        0, _distance);

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

                    blade.add(nextLocation);
                }
            }

            return blade;
        }

        private void displayBlade(int yaw, int pitch) {
            Blade blade = getBlade(yaw, pitch);

            if (blade == null)
                return;

            blade.explode();

            for (ArenaPlayer player : _region._playersInRegion) {

                if (blade.contains(player.getLocation())) {
                    damagePlayer(player);
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
                player.getHandle().damage(_region._damage);
            }
        }

        private int getYaw(float yaw) {
            if (Float.compare(yaw, 180.0F) == 0) {
                return 180;
            }
            if (yaw > 0) {
                return (int) (yaw % 360) - 180;
            }
            else if (yaw < 0) {
                return (int)-((Math.abs(yaw) % 360) - 180);
            }

            return 0;
        }

        String getBladeKey(int yaw, int pitch) {
            return String.valueOf(yaw) + '.' + pitch;
        }
    }
}
