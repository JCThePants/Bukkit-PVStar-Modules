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


package com.jcwhatever.pvs.modules.regions.regions;

import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.events.players.PlayerArenaRespawnEvent;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.regions.options.EnterRegionReason;
import com.jcwhatever.nucleus.regions.options.LeaveRegionReason;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.settings.PropertyDefinition;
import com.jcwhatever.nucleus.storage.settings.PropertyValueType;
import com.jcwhatever.nucleus.storage.settings.SettingsBuilder;
import com.jcwhatever.nucleus.utils.Rand;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RegionTypeInfo(
        name="checkpoint",
        description="Changes a players respawn point.")
public class CheckpointRegion extends AbstractPVRegion implements IEventListener {

    private static Map<String, PropertyDefinition> _possibleSettings;

    static {
        _possibleSettings = new SettingsBuilder()
                .set("spawns", PropertyValueType.STRING,
                        "The name of the spawnpoint to set the players respawn point to.")
                .build()
        ;
    }

    private List<Spawnpoint> _spawnpoints;
    private Map<UUID, Spawnpoint> _checkpointMap = new HashMap<>(25);

    public CheckpointRegion(String name) {
        super(name);
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
        String spawnNames = dataNode.getString("spawns");

        if (spawnNames != null)
            _spawnpoints = getArena().getSpawnManager().getSpawns(spawnNames);
    }

    @Override
    protected Map<String, PropertyDefinition> getDefinitions() {
        return _possibleSettings;
    }

    @Override
    protected void onPlayerEnter(IArenaPlayer player, EnterRegionReason reason) {
        Spawnpoint spawn = Rand.get(_spawnpoints);

        _checkpointMap.put(player.getUniqueId(), spawn);
    }

    @Override
    protected void onPlayerLeave(IArenaPlayer player, LeaveRegionReason reason) {
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
    protected boolean canDoPlayerEnter(Player p, EnterRegionReason reason) {
        return _spawnpoints != null && !_spawnpoints.isEmpty() && !_checkpointMap.containsKey(p.getUniqueId());
    }

    @EventMethod
    private void onPlayerRespawn(PlayerArenaRespawnEvent event) {

        Spawnpoint spawn = _checkpointMap.get(event.getPlayer().getUniqueId());
        if (spawn == null)
            return;

        event.setRespawnLocation(spawn);
    }

    @EventMethod
    private void onArenaEnd(@SuppressWarnings("unused") ArenaEndedEvent event) {
        _checkpointMap.clear();
    }
}
