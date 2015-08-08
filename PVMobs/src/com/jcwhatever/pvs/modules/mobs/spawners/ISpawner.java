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


package com.jcwhatever.pvs.modules.mobs.spawners;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import org.bukkit.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.List;

public interface ISpawner extends IDisposable {

    /**
     * Invoked when instantiated. Should only be invoked once.
     */
    void init(MobArenaExtension manager);


    ISpawnerSettings getSettings();

    IArena getArena();

    MobArenaExtension getExtension();

    /**
     * Get the current limit on new mob spawns.
     */
    int getSpawnLimit();

    /**
     * Determine if the spawner is running.
     */
    boolean isRunning();

    /**
     * Determine if the spawner is paused.
     */
    boolean isPaused();

    /**
     * run or resume the spawner
     */
    void run();

    /**
     * Pause spawning of mobs.
     */
    void pause();

    /**
     * Stop and reset the spawner.
     */
    void stop();

    /**
     * Get number of spawned mobs.
     */
    int getMobCount();

    /**
     * Get a list of the spawned mobs
     */
    List<LivingEntity> getMobs();

    /**
     *
     * @param spawn
     * @return
     */
    @Nullable
    List<LivingEntity> spawn(Spawnpoint spawn, int count);

    void reset(DespawnMethod method);

    void removeMob(LivingEntity entity, DespawnMethod method, MobRemoveReason reason);

    void removeDead();

    /**
     * Called when no longer needed. Cleans up resources, breaks down
     * association with arena and stops all internal tasks.
     */
    @Override
    void dispose();
}
