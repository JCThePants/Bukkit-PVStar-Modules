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

package com.jcwhatever.pvs.modules.mobs.spawners.wave;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.ISettingsManager;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.storage.settings.SettingsBuilder;
import com.jcwhatever.nucleus.storage.settings.SettingsManager;
import com.jcwhatever.nucleus.utils.observer.update.UpdateSubscriber;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawnerSettings;

import java.util.Map;

/*
 * 
 */
public class WaveSettings implements ISpawnerSettings {

    private static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
                .set("max-mobs", PropertyValueType.INTEGER, 45,
                        "Maximum mobs spawned.")

                .set("max-per-spawn", PropertyValueType.INTEGER, 2,
                        "Maximum mobs alive per spawnpoint.")

                .set("max-mobs-per-player", PropertyValueType.INTEGER, 8,
                        "Maximum mobs spawned per player.")

                .set("wave-multiplier", PropertyValueType.INTEGER, 1,
                        "Sets multiplier used to determine how many mobs to spawn in current wave.")

                .set("max-distance", PropertyValueType.INTEGER, 24,
                        "Maximum distance when detecting proximity.")

                .set("seconds-between-waves", PropertyValueType.INTEGER, 10,
                        "The number of seconds before the next wave begins.")

                .set("display-wave-title", PropertyValueType.BOOLEAN, true,
                        "Determine if a title message should be displayed to indicate the current wave.")

                .set("wave-based-health", PropertyValueType.BOOLEAN, true,
                        "Determine if mob initial health matches wave number.")

                .set("wave-based-health-factor", PropertyValueType.DOUBLE, 2.0D,
                        "Set the factor applied to the mobs health when affected by the current wave number.")

                .build()
        ;
    }

    private int _maxMobs = 45;
    private int _maxMobsPerSpawn = 2;
    private int _maxMobsPerPlayer = 8;
    private int _waveMultiplier = 1;
    private int _maxMobDistance = 24;
    private int _secondsBetweenWaves = 10;
    private boolean _displayWaveTitle = true;
    private int _maxMobDistanceSquared; // max distance when getting closest mob (squared)
    private boolean _isWaveBasedHealth = true;
    private double _waveBasedHealthFactor = 2.0D;

    private final IDataNode _dataNode;
    private final SettingsManager _settingsManager;

    WaveSettings(WaveSpawner spawner) {
        _dataNode = spawner.getExtension().getDataNode().getNode("spawners.wave");
        _settingsManager = new SettingsManager(_dataNode, _possibleSettings);

        UpdateSubscriber<SettingsManager.PropertyValue> onChange = new UpdateSubscriber<SettingsManager.PropertyValue>() {
            @Override
            public void on(SettingsManager.PropertyValue argument) {
                _maxMobs = _dataNode.getInteger("max-mobs", _maxMobs);
                _maxMobsPerSpawn = _dataNode.getInteger("max-per-spawn", _maxMobsPerSpawn);
                _maxMobsPerPlayer = _dataNode.getInteger("max-mobs-per-player", _maxMobsPerPlayer);
                _waveMultiplier = _dataNode.getInteger("wave-multiplier", _waveMultiplier);
                _maxMobDistance = _dataNode.getInteger("max-distance", _maxMobDistance);
                _secondsBetweenWaves = _dataNode.getInteger("seconds-between-waves", _secondsBetweenWaves);
                _displayWaveTitle = _dataNode.getBoolean("display-wave-title", _displayWaveTitle);
                _isWaveBasedHealth = _dataNode.getBoolean("wave-based-health", _isWaveBasedHealth);
                _waveBasedHealthFactor = _dataNode.getDouble("wave-based-health-factor", _waveBasedHealthFactor);

                _maxMobDistanceSquared = _maxMobDistance * _maxMobDistance;
            }
        };

        _settingsManager.onChange(onChange);
        onChange.on(null);
    }

    @Override
    public Map<String, PropertyDefinition> getDefinitions() {
        return _possibleSettings;
    }

    @Override
    public ISettingsManager getManager() {
        return _settingsManager;
    }

    public int getMaxMobs() {
        return _maxMobs;
    }

    public int getMaxMobsPerPlayer() {
        return _maxMobsPerPlayer;
    }

    public void setMaxMobsPerPlayer(int value) {
        _settingsManager.set("max-mobs-per-player", value);
    }

    public int getMaxMobsPerSpawn() {
        return _maxMobsPerSpawn;
    }

    public void setMaxMobsPerSpawn(int value) {
        _settingsManager.set("max-per-spawn", value);
    }

    public int getWaveMultiplier() {
        return _waveMultiplier;
    }

    public void setWaveMultiplier(int value) {
        _settingsManager.set("wave-multiplier", value);
    }

    public boolean isWaveBasedHealth() {
        return _isWaveBasedHealth;
    }

    public void setWaveBasedHealth(boolean isWaveBased) {
        _settingsManager.set("wave-based-health", isWaveBased);
    }

    public double getWaveBasedHealthFactor() {
        return _waveBasedHealthFactor;
    }

    public void setWaveBasedHealthFactor(double factor) {
        _settingsManager.set("wave-based-health-factor", factor);
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

    public int getSecondsBetweenWaves() {
        return _secondsBetweenWaves;
    }

    public void setSecondsBetweenWaves(int seconds) {
        _settingsManager.set("seconds-between-waves", seconds);
    }

    public boolean isWaveTitleDisplayed() {
        return _displayWaveTitle;
    }

    public void setWaveTitleDisplayed(boolean isDisplayed) {
        _settingsManager.set("display-wave-title", isDisplayed);
    }
}