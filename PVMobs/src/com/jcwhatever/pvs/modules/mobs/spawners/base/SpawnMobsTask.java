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

package com.jcwhatever.pvs.modules.mobs.spawners.base;

import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawner;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Spawn mobs task
 */
public abstract class SpawnMobsTask extends TaskHandler {

    private final Map<Spawnpoint, SpawnpointInfo> _spawnInfoMap = new HashMap<>(25);
    private final ISpawner _spawner;
    private final IArena _arena;

    public SpawnMobsTask(MobArenaExtension extension, ISpawner spawner) {
        PreCon.notNull(extension);
        PreCon.notNull(spawner);

        _spawner = spawner;
        _arena = extension.getArena();
    }

    @Override
    public void run() {

        _spawner.removeDead();

        if (_spawner.isPaused())
            return;

        IArenaPlayerCollection players = _arena.getGame().getPlayers();

        int maxMobsPerSpawn = getMaxMobsPerSpawn();

        // make sure mob limit has no been reached.
        if (canAddMobs()) {

            // get spawns in proximity to players
            List<Spawnpoint> spawns = getMobSpawns(players);

            if (!spawns.isEmpty()) {

                // spawn till max is reached
                while (canAddMobs() && !spawns.isEmpty()) {

                    Spawnpoint spawn = Rand.get(spawns);

                    spawns.remove(spawn);

                    SpawnpointInfo info = null;

                    if (maxMobsPerSpawn > -1) {
                        // get spawn info to track entities spawned at spawnpoint
                        info = _spawnInfoMap.get(spawn);
                        if (info == null) {
                            info = new SpawnpointInfo(spawn, maxMobsPerSpawn);
                            _spawnInfoMap.put(spawn, info);
                        }

                        // make sure mobs per spawn is not reached.
                        if (info.getEntityCount() >= maxMobsPerSpawn) {

                            // remove maxed spawn from candidates
                            continue;
                        }
                    }

                    int spawnCount = getSpawnCount(maxMobsPerSpawn);

                    List<LivingEntity> spawned = _spawner.spawn(spawn, spawnCount);
                    if (spawned != null) {
                        setMobTargets(spawned);

                        if (info != null) {
                            for (LivingEntity entity : spawned) {
                                info.addEntity(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    protected abstract int getSpawnLimit();

    protected abstract int getMaxMobsPerSpawn();

    protected abstract List<Spawnpoint> getMobSpawns(IArenaPlayerCollection players);

    protected abstract void setMobTargets(List<LivingEntity> mobs);

    @Override
    protected void onCancel() {
        _spawner.reset(DespawnMethod.REMOVE);
        _spawner.stop();
    }

    protected boolean canAddMobs() {
        return _spawner.getMobCount() < getSpawnLimit();
    }

    protected int getSpawnCount(int maxMobsPerSpawn) {
        return Math.min(getSpawnLimit(), maxMobsPerSpawn);
    }
}
