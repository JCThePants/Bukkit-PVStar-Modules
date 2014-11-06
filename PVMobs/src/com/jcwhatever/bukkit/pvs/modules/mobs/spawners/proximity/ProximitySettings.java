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


package com.jcwhatever.bukkit.pvs.modules.mobs.spawners.proximity;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.SettingsManager;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawnerSettings;

public class ProximitySettings implements ISpawnerSettings {

    private static SettingDefinitions _settings;

    static {
        _settings = new SettingDefinitions();

        _settings
                .set("max-mobs", 20, ValueType.INTEGER, "Maximum mobs spawned.")
                .set("max-per-spawn", 2, ValueType.INTEGER, "Maximum mobs alive per spawnpoint.")
                .set("max-mobs-per-player", 4, ValueType.INTEGER, "Maximum mobs spawned per player.")
                .set("max-path-distance", 18, ValueType.INTEGER, "Maximum mob path distance when detecting proximity.")
                .set("max-distance", 24, ValueType.INTEGER, "Maximum distance when detecting proximity.")
        ;
    }

    private int _maxMobs = 20;
    private int _maxMobsPerSpawn = 2;
    private int _maxMobsPerPlayer = 4;
    private int _maxMobPathDistance = 18; // max distance of a valid mob path
    private int _maxMobDistance = 24;
    private int _maxMobDistanceSquared; // max distance when getting closest mob (squared)

    private final IDataNode _dataNode;
    private final SettingsManager _settingsManager;

    ProximitySettings(ProximitySpawner spawner) {
        _dataNode = spawner.getManager().getDataNode().getNode("spawners.proximity");
        _settingsManager = new SettingsManager(_dataNode, _settings);

        _settingsManager.addOnSettingsChanged(new Runnable() {
            @Override
            public void run() {

                _maxMobs = _dataNode.getInteger("max-mobs", _maxMobs);
                _maxMobsPerSpawn = _dataNode.getInteger("max-per-spawn", _maxMobsPerSpawn);
                _maxMobsPerPlayer = _dataNode.getInteger("max-mobs-per-player", _maxMobsPerPlayer);
                _maxMobPathDistance = _dataNode.getInteger("max-path-distance", _maxMobPathDistance);
                _maxMobDistance = _dataNode.getInteger("max-distance", _maxMobDistance);
                _maxMobDistanceSquared = _maxMobDistance * _maxMobDistance;
            }
        }, true);
    }

    @Override
    public SettingDefinitions getDefinitions() {
        return _settings;
    }

    @Override
    public ISettingsManager getManager() {
        return _settingsManager;
    }

    @Override
    public int getMaxMobs() {
        return _maxMobs;
    }

    public int getMaxMobsPerSpawn() {
        return _maxMobsPerSpawn;
    }


    public void setMaxMobsPerSpawn(int value) {
        _settingsManager.set("max-per-spawn", value);
    }


    public int getMaxMobsPerPlayer() {
        return _maxMobsPerPlayer;
    }


    public void setMaxMobsPerPlayer(int value) {
        _settingsManager.set("max-mobs-per-player", value);
    }

    public int getMaxPathDistance() {
        return _maxMobPathDistance;
    }

    public void setMaxMobPathDistance(int value) {
        _settingsManager.set("max-path-distance", value);
    }

    public int getMaxDistance() {
        return _maxMobDistance;
    }

    public void setMaxDistance(int value) {
        _settingsManager.set("max-distance", value);
    }

    public int getMaxMobDistanceSquared() {
        return _maxMobDistanceSquared;
    }
}
