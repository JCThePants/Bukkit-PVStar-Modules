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

import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/*
 * Tracks entities spawned on a spawnpoint.
 */
public class SpawnpointInfo {

    private final Spawnpoint _spawnpoint;
    private final Map<Entity, Void> _spawnedEntities;

    public SpawnpointInfo(Spawnpoint spawnpoint, int maxMobsPerSpawn) {
        _spawnpoint = spawnpoint;
        _spawnedEntities = new HashMap<>(maxMobsPerSpawn + 5);
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
