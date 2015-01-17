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

import com.jcwhatever.nucleus.utils.pathing.astar.AStar.LocationAdjustment;
import com.jcwhatever.nucleus.utils.pathing.astar.AStarPathFinder;
import com.jcwhatever.nucleus.utils.scheduler.ScheduledTask;
import com.jcwhatever.nucleus.utils.scheduler.TaskHandler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.bukkit.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawnerSettings;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerInfo;
import com.jcwhatever.bukkit.pvs.modules.mobs.utils.DistanceUtils;

import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Spawns mobs in an arena using the settings
 * specified in the mob manager.
 */
@SpawnerInfo(
        name="Proximity",
        description = "Spawn mobs from spawns that are in proximity to players."
)
public class ProximitySpawner implements ISpawner {

    private Arena _arena;
    private MobArenaExtension _manager;
    private ProximitySettings _settings;
    private List<Spawnpoint> _mobSpawns;

    private boolean _isRunning;
    private boolean _isPaused;
    private int _maxMobs;

    private ScheduledTask _spawnMobsTask;
    private ScheduledTask _despawnMobsTask;

    private boolean _isDisposed;

    @Override
    public void init(MobArenaExtension manager) {
        PreCon.notNull(manager);

        _arena = manager.getArena();
        _manager = manager;
        _settings = new ProximitySettings(this);
    }

    @Override
    public ISpawnerSettings getSettings() {
        return _settings;
    }

    public MobArenaExtension getManager() {
        return _manager;
    }

    /**
     * Starts the spawner. The spawner stops when the arena ends,
     * there are no more players in the arena, or if there are no mob spawns.
     */
    @Override
    public void run() {

        _isPaused = false;

        if (_arena == null || _isRunning || !_arena.getGameManager().isRunning() || _arena.getGameManager().getPlayerCount() == 0)
            return;

        if (!_manager.isEnabled())
            return;

        _isRunning = true;

        int totalPlayers = _arena.getGameManager().getPlayerCount();
        _maxMobs = Math.min(
                _settings.getMaxMobs(),
                _settings.getMaxMobsPerPlayer() * totalPlayers);

        _mobSpawns = _manager.getMobSpawns();

        _spawnMobsTask = ArenaScheduler.runTaskRepeat(_arena, 5, 20 + (3 * totalPlayers), new SpawnMobs());
        _despawnMobsTask = ArenaScheduler.runTaskRepeat(_arena, 10, 10, new DespawnMobs());
    }


    @Override
    public void pause() {
        _isPaused = true;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        if (_spawnMobsTask != null) {
            _spawnMobsTask.cancel();
            _spawnMobsTask = null;
        }

        if (_despawnMobsTask != null) {
            _despawnMobsTask.cancel();
            _despawnMobsTask = null;
        }

        _arena = null;
        _manager = null;

        _isDisposed = true;
    }

    /*
     * Set mob targets
     */
    private void setMobTargets(List<LivingEntity> mobs) {

        for (LivingEntity entity : mobs) {

            if (!(entity instanceof Creature))
                continue;

            Creature creature = (Creature)entity;

            ArenaPlayer closest = DistanceUtils.getClosestPlayer(
                    _arena.getGameManager().getPlayers(), creature.getLocation(), _settings.getMaxMobDistanceSquared());

            if (closest == null) {
                _manager.removeMob(entity, DespawnMethod.REMOVE);
                continue;
            }

            creature.setTarget(closest.getPlayer());

            if (creature instanceof PigZombie) {
                PigZombie pigZ = (PigZombie)creature;
                pigZ.setAngry(true);
            }
        }
    }


    /*
     * Spawn mobs task
     */
    class SpawnMobs extends TaskHandler {

        private Map<Spawnpoint, SpawnpointInfo> _spawnInfoMap = new HashMap<>(25);

        @Override
        public void run() {

            _manager.removeDead();

            if (_isPaused)
                return;

            List<ArenaPlayer> players = _arena.getGameManager().getPlayers();

            int maxMobsPerSpawn = _settings.getMaxMobsPerSpawn();

            // make sure mob limit has no been reached.
            if (_manager.getMobCount() < _maxMobs) {

                // get spawns in proximity to players
                List<Spawnpoint> spawns = DistanceUtils.getClosestSpawns(
                        _arena, players, _mobSpawns, _settings.getMaxPathDistance());

                if (!spawns.isEmpty()) {

                    // spawn till max is reached
                    while (canAddMobs() && !spawns.isEmpty()) {

                        Spawnpoint spawn = Rand.get(spawns);

                        // get spawn info to track entities spawned at spawnpoint
                        SpawnpointInfo info = _spawnInfoMap.get(spawn);
                        if (info == null) {
                            info = new SpawnpointInfo(spawn, _settings);
                            _spawnInfoMap.put(spawn, info);
                        }

                        spawns.remove(spawn);

                        // make sure mobs per spawn is not reached.
                        if (info.getEntityCount() >= _settings.getMaxMobsPerSpawn()) {

                            // remove maxed spawn from candidates
                            continue;
                        }

                        int spawnCount = getSpawnCount(maxMobsPerSpawn);

                        List<LivingEntity> spawned = _manager.spawn(spawn, spawnCount);
                        if (spawned != null) {
                            setMobTargets(spawned);

                            for (LivingEntity entity : spawned) {
                                info.addEntity(entity);
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void onCancel() {
            _manager.reset(DespawnMethod.REMOVE);
            _isRunning = false;
            _isPaused = false;
        }

        private boolean canAddMobs() {
            return _manager.getMobCount() < _maxMobs;
        }

        private int getSpawnCount(int maxMobsPerSpawn) {
            return Math.min(_maxMobs - _manager.getMobCount(), maxMobsPerSpawn);
        }
    }


    /*
     * Despawn mobs that are out of range of players
     */
    class DespawnMobs implements Runnable {

        @Override
        public void run() {

            List<LivingEntity> mobs = _manager.getMobs();

            if (mobs.size() > 0) {

                LivingEntity mob = Rand.get(mobs);

                if (mob.isDead()) {
                    _manager.removeMob(mob, DespawnMethod.REMOVE);
                }
                else {
                    ArenaPlayer closest = DistanceUtils.getClosestPlayer(
                            _arena.getGameManager().getPlayers(), mob.getLocation(), _settings.getMaxMobDistanceSquared());

                    if (closest == null) {
                        _manager.removeMob(mob, DespawnMethod.REMOVE);
                        return;
                    }

                    if (!mob.hasLineOfSight(closest.getPlayer())) {

                        AStarPathFinder astar = new AStarPathFinder();
                        astar.setMaxDropHeight(DistanceUtils.MAX_DROP_HEIGHT);
                        astar.setMaxRange(_settings.getMaxDistance());

                        int distance = astar.getPathDistance(
                                mob.getLocation(), closest.getLocation(), LocationAdjustment.FIND_SURFACE);

                        if (distance == -1 || distance > _settings.getMaxPathDistance())
                            _manager.removeMob(mob, DespawnMethod.REMOVE);
                    }
                }
            }
        }
    }


    /*
     * Tracks entities spawned on a spawnpoint.
     */
    static class SpawnpointInfo {

        private final Spawnpoint _spawnpoint;
        private final Map<Entity, Void> _spawnedEntities;

        SpawnpointInfo(Spawnpoint spawnpoint, ProximitySettings settings) {
            _spawnpoint = spawnpoint;
            _spawnedEntities = new HashMap<>(settings.getMaxMobsPerSpawn() + 5);
        }

        public Spawnpoint getSpawnpoint() {
            return _spawnpoint;
        }

        public int getEntityCount() {
            int count = 0;
            for (Entity entity : _spawnedEntities.keySet()) {

                if (entity.isDead())
                    continue;

                count++;
            }

            return count;
        }

        public void addEntity(Entity entity) {
            _spawnedEntities.put(entity, null);
        }
    }
}
