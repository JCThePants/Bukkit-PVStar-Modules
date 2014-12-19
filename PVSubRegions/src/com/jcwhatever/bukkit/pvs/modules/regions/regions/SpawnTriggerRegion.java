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

import com.jcwhatever.bukkit.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.manager.IEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.storage.settings.ValueType;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RegionTypeInfo(
        name="spawntrigger",
        description="Trigger mob spawners by entering the region.")

public class SpawnTriggerRegion extends AbstractPVRegion implements IEventListener {

    private static SettingDefinitions _possibleSettings = new SettingDefinitions();

    static {
        _possibleSettings
            .set("spawn-count", 1, ValueType.INTEGER, "Set the number of entities each spawn will created when the region is triggered.")
            .set("spawns", ValueType.STRING, "Set the spawns that are triggered using a comma delimited list of spawn names.")
            .set("max-triggers", 1, ValueType.INTEGER, "Set the maximum times the region can be triggered.")
        ;
    }

    private List<Spawnpoint> _spawns;
    private int _spawnCount = 1;
    private int _maxTriggers = 1;

    private int _triggerCount = 0;

    public SpawnTriggerRegion(String name) {
        super(name);
    }

    @Override
    protected boolean canDoPlayerEnter(Player p, EnterRegionReason reason) {
        return isEnabled() && _triggerCount < _maxTriggers;
    }

    @Override
    protected void onPlayerEnter(ArenaPlayer player, EnterRegionReason reason) {
        for (Spawnpoint spawn : _spawns) {
            spawn.spawn(getArena(), _spawnCount);
        }
        _triggerCount++;
    }

    @Override
    protected boolean canDoPlayerLeave(Player p, LeaveRegionReason reason) {
        return false;
    }

    @Override
    protected void onPlayerLeave(ArenaPlayer player, LeaveRegionReason reason) {
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
        setEventListener(true);
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        setEventListener(false);
        getArena().getEventManager().unregister(this);
    }

    @Override
    protected void onLoadSettings(IDataNode dataNode) {

        _spawnCount = dataNode.getInteger("spawn-count", _spawnCount);
        _maxTriggers = dataNode.getInteger("max-triggers", _maxTriggers);

        String rawSpawnNames = dataNode.getString("spawns", "");

        String[] nameComp = TextUtils.PATTERN_COMMA.split(rawSpawnNames);
        _spawns = new ArrayList<>(nameComp.length);

        for (String untrimmed : nameComp) {

            String spawnName = untrimmed.trim();
            if (spawnName.isEmpty())
                continue;

            Spawnpoint spawn = getArena().getSpawnManager().getSpawn(spawnName);
            if (spawn != null && spawn.getSpawnType().isSpawner())
                _spawns.add(spawn);
        }
    }

    @Override
    protected SettingDefinitions getSettingDefinitions() {
        return _possibleSettings;
    }

    @GenericsEventHandler
    private void onArenaEnd(@SuppressWarnings("unused") ArenaEndedEvent event) {
        _triggerCount = 0;
    }
}
