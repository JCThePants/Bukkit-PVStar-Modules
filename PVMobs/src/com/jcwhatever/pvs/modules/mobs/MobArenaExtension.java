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


package com.jcwhatever.pvs.modules.mobs;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.events.ArenaStartedEvent;
import com.jcwhatever.pvs.api.events.spawns.SpawnAddedEvent;
import com.jcwhatever.pvs.api.events.spawns.SpawnRemovedEvent;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.pvs.modules.mobs.spawners.SpawnerManager;
import com.jcwhatever.pvs.modules.mobs.spawners.proximity.ProximitySpawner;
import com.jcwhatever.pvs.modules.mobs.spawngroups.SpawnGroupGenerator;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

@ArenaExtensionInfo(
        name="PVMobs",
        description = "Adds mob spawning support to an arena.")
public class MobArenaExtension extends ArenaExtension implements IEventListener {

    public static final String NAME = "PVMobs";

    private SpawnGroupGenerator _groups;
    private ISpawner _spawner;
    private MobTypeLimiter _limiter;

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    public MobTypeLimiter getTypeLimits() {
        return _limiter;
    }

    @Override
    protected void onEnable() {

        _limiter = new MobTypeLimiter(getDataNode().getNode("limits"));

        String spawnerName = getDataNode().getString("spawner", "proximity");

        //noinspection ConstantConditions
        setSpawner(spawnerName);

        loadSettings();
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onArenaStart(@SuppressWarnings("UnusedParameters") ArenaStartedEvent event) {

        // make sure there are spawns
        if (_groups.getSpawnGroups().isEmpty())
            return;

        // run spawner
        _spawner.run();
    }

    @EventMethod
    private void onArenaEnd(@SuppressWarnings("UnusedParameters") ArenaEndedEvent event) {
        _spawner.reset(DespawnMethod.REMOVE);
    }

    @EventMethod
    private void onAddSpawn(@SuppressWarnings("UnusedParameters") SpawnAddedEvent event) {
        loadSettings();
    }

    @EventMethod
    private void onRemoveSpawn(@SuppressWarnings("UnusedParameters") SpawnRemovedEvent event) {
        loadSettings();
    }

    public SpawnGroupGenerator getGroupGenerator() {
        return _groups;
    }

    public List<Spawnpoint> getMobSpawns() {
        return _groups.getSpawnGroups();
    }

    public ISpawner getSpawner() {
        return _spawner;
    }

    public void setSpawner(Class<? extends ISpawner> spawnerClass) {

        if (_spawner != null) {
            _spawner.dispose();
            _spawner = null;
        }

        try {
            _spawner = spawnerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (_spawner != null) {
            _spawner.init(this);
        }
    }

    public void setSpawner(String spawnerName) {
        PreCon.notNullOrEmpty(spawnerName);

        Class<? extends ISpawner> spawnerClass = SpawnerManager.getSpawnerClass(spawnerName);

        if (spawnerClass == null) {
            spawnerClass = ProximitySpawner.class;
        }

        getDataNode().set("spawner", spawnerName.toLowerCase());
        getDataNode().save();

        setSpawner(spawnerClass);
    }

    @Nullable
    public String getSpawnerName() {
        return getDataNode().getString("spawner");
    }

    // get game spawns that are alive
    private List<Spawnpoint> getGameMobSpawns() {
        List<Spawnpoint> spawns = getArena().getSpawns().getAll();

        Iterator<Spawnpoint> iterator = spawns.iterator();

        while(iterator.hasNext()) {
            Spawnpoint spawn = iterator.next();

            if (!spawn.getSpawnType().isSpawner() || !spawn.getSpawnType().isAlive())
                iterator.remove();
        }

        return spawns;
    }

    private void loadSettings() {
        _groups = new SpawnGroupGenerator(this, getGameMobSpawns());
    }
}
