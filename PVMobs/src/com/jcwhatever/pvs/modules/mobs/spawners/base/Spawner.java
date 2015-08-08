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

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.context.IGameContext;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.MobTypeLimiter;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.pvs.modules.mobs.spawners.MobRemoveReason;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*
 * 
 */
public abstract class Spawner implements ISpawner {

    private IArena _arena;
    private MobArenaExtension _extension;
    private List<LivingEntity> _mobs = new ArrayList<LivingEntity>(100);
    private MobTypeLimiter _limiter;

    private boolean _isRunning;
    private boolean _isPaused;
    private boolean _isDisposed;

    @Override
    public IArena getArena() {
        return _arena;
    }

    @Override
    public MobArenaExtension getExtension() {
        return _extension;
    }

    @Override
    public final void init(MobArenaExtension extension) {
        PreCon.notNull(extension);

        _arena = extension.getArena();
        _extension = extension;
        _limiter = extension.getTypeLimits();

        onInit(extension);
    }

    @Override
    public boolean isRunning() {
        return _isRunning;
    }

    @Override
    public boolean isPaused() {
        return _isPaused;
    }

    /**
     * Starts the spawner. The spawner stops when the arena ends,
     * there are no more players in the arena, or if there are no mob spawns.
     */
    @Override
    public final void run() {

        _isPaused = false;

        if (_arena == null || _isRunning)
            return;

        IGameContext gameManager = _arena.getGame();

        if (!gameManager.isRunning() || gameManager.getPlayers().size() == 0) {
            return;
        }

        _isRunning = true;

        onRun();
    }

    @Override
    public void pause() {
        _isPaused = true;

        onPause();
    }

    @Override
    public void stop() {
        _isRunning = false;
        _isPaused = false;

        onStop();
    }

    @Override
    public int getMobCount() {
        return _mobs.size();
    }

    @Override
    public List<LivingEntity> getMobs() {
        return _mobs;
    }

    @Override
    public List<LivingEntity> spawn(Spawnpoint spawn, int count) {
        PreCon.notNull(spawn);

        // make sure the type hasn't reached its limit
        if (!_limiter.canSpawnType(spawn.getSpawnType()))
            return null;

        // spawn the entity
        List<Entity> entities = spawn.spawn(getArena(), count);
        if (entities == null)
            return null;

        List<LivingEntity> result = new ArrayList<>(entities.size());

        // record each spawned entity and place into LivingEntity result list
        Iterator<Entity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {

            Entity entity = entityIterator.next();

            if (entity == null)
                throw new NullPointerException("Entity array has a null entry.");

            if (!(entity instanceof LivingEntity)) {
                entity.remove();
                entityIterator.remove();
                continue;
            }

            result.add((LivingEntity) entity);
            _mobs.add((LivingEntity)entity);

            _limiter.increment(entity.getType(), 1);
            onMobSpawn((LivingEntity)entity);
        }

        return result;
    }

    @Override
    public void reset(DespawnMethod method) {
        PreCon.notNull(method);

        for (LivingEntity entity : _mobs) {
            _limiter.increment(entity.getType(), -1);

            if (method == DespawnMethod.KILL)
                entity.damage(entity.getMaxHealth());
            else
                entity.remove();
        }

        _mobs.clear();
        stop();
    }

    @Override
    public void removeMob(LivingEntity entity, DespawnMethod method, MobRemoveReason reason) {
        PreCon.notNull(entity);
        PreCon.notNull(method);

        _mobs.remove(entity);
        _limiter.increment(entity.getType(), -1);

        if (method == DespawnMethod.KILL)
            entity.damage(entity.getMaxHealth());
        else
            entity.remove();

        onMobRemove(entity, reason);
    }

    @Override
    public void removeDead() {

        Iterator<LivingEntity> iterator = _mobs.iterator();
        while (iterator.hasNext()) {
            LivingEntity entity = iterator.next();

            if (!entity.isDead())
                continue;

            iterator.remove();
            entity.remove();
            _limiter.increment(entity.getType(), -1);

            onMobRemove(entity, MobRemoveReason.KILLED);
        }
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public final void dispose() {
        _arena = null;
        _extension = null;

        onDispose();

        _isDisposed = true;
    }

    protected abstract void onRun();

    protected abstract void onDispose();

    protected void onInit(@SuppressWarnings("unused") MobArenaExtension extension) {}

    protected void onPause() {}

    protected void onStop() {}

    protected void onMobRemove(LivingEntity entity, MobRemoveReason reason) {}

    protected void onMobSpawn(LivingEntity entity) {}
}
