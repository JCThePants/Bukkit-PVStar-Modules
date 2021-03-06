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


package com.jcwhatever.pvs.modules.mobs.spawners.proximity;

import com.jcwhatever.nucleus.managed.scheduler.IScheduledTask;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.api.utils.ArenaScheduler;
import com.jcwhatever.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawnerSettings;
import com.jcwhatever.pvs.modules.mobs.spawners.MobRemoveReason;
import com.jcwhatever.pvs.modules.mobs.spawners.SpawnerInfo;
import com.jcwhatever.pvs.modules.mobs.spawners.base.DespawnMobsTask;
import com.jcwhatever.pvs.modules.mobs.spawners.base.SpawnMobsTask;
import com.jcwhatever.pvs.modules.mobs.spawners.base.Spawner;
import com.jcwhatever.pvs.modules.mobs.utils.DistanceUtils;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;

import java.util.List;

/**
 * Spawns mobs in an arena using the settings
 * specified in the mob manager.
 */
@SpawnerInfo(
        name="Proximity",
        description = "Spawn mobs from spawns that are in proximity to players."
)
public class ProximitySpawner extends Spawner {

    private ProximitySettings _settings;
    private List<Spawnpoint> _mobSpawns;

    private int _maxMobs;

    private IScheduledTask _spawnMobsTask;
    private IScheduledTask _despawnMobsTask;


    @Override
    protected void onInit(MobArenaExtension _extension) {
        _settings = new ProximitySettings(this);
    }

    @Override
    public ISpawnerSettings getSettings() {
        return _settings;
    }

    @Override
    public int getSpawnLimit() {
        return _maxMobs - getMobCount();
    }

    @Override
    protected void onRun() {
        int totalPlayers = getArena().getGame().getPlayers().size();
        _maxMobs = Math.min(
                _settings.getMaxMobs(),
                _settings.getMaxMobsPerPlayer() * totalPlayers);

        _mobSpawns = getExtension().getMobSpawns();

        _spawnMobsTask = ArenaScheduler.runTaskRepeat(getArena(), 5, 20 + (3 * totalPlayers), new SpawnTask());
        _despawnMobsTask = ArenaScheduler.runTaskRepeat(getArena(), 10, 10, new DespawnMobs());
    }

    @Override
    protected void onPause() {
        // do nothing
    }

    @Override
    protected void onDispose() {
        if (_spawnMobsTask != null) {
            _spawnMobsTask.cancel();
            _spawnMobsTask = null;
        }

        if (_despawnMobsTask != null) {
            _despawnMobsTask.cancel();
            _despawnMobsTask = null;
        }
    }


    /*
     * Set mob targets
     */
    private void setMobTargets(List<LivingEntity> mobs) {

        for (LivingEntity entity : mobs) {

            if (!(entity instanceof Creature))
                continue;

            Creature creature = (Creature)entity;

            IArenaPlayer closest = DistanceUtils.getClosestPlayer(
                    getArena().getGame().getPlayers(), creature.getLocation(), _settings.getMaxMobDistanceSquared());

            if (closest == null) {
                removeMob(entity, DespawnMethod.REMOVE, MobRemoveReason.OUT_OF_RANGE);
                continue;
            }

            if (!(closest.getEntity() instanceof LivingEntity))
                continue;

            creature.setTarget((LivingEntity)closest.getEntity());

            if (creature instanceof PigZombie) {
                PigZombie pigZ = (PigZombie)creature;
                pigZ.setAngry(true);
            }
        }
    }

    class SpawnTask extends SpawnMobsTask {

        public SpawnTask() {
            super(getExtension(), ProximitySpawner.this);
        }

        @Override
        protected int getSpawnLimit() {
            return ProximitySpawner.this.getSpawnLimit();
        }

        @Override
        protected int getMaxMobsPerSpawn() {
            return _settings.getMaxMobsPerSpawn();
        }

        @Override
        protected List<Spawnpoint> getMobSpawns(IArenaPlayerCollection players) {
            return DistanceUtils.getClosestSpawns(
                    getArena(), players, _mobSpawns, _settings.getMaxPathDistance());
        }

        @Override
        protected void setMobTargets(List<LivingEntity> mobs) {
            ProximitySpawner.this.setMobTargets(mobs);
        }
    }

    class DespawnMobs extends DespawnMobsTask {

        public DespawnMobs() {
            super(getExtension(), ProximitySpawner.this);
        }

        @Override
        protected int getMaxDistanceSquared() {
            return _settings.getMaxMobDistanceSquared();
        }

        @Override
        protected int getMaxDistance() {
            return _settings.getMaxDistance();
        }

        @Override
        protected int getMaxPathDistance() {
            return _settings.getMaxPathDistance();
        }
    }
}
