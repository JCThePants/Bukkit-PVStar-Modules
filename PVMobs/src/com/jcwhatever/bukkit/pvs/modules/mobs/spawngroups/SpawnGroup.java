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


package com.jcwhatever.bukkit.pvs.modules.mobs.spawngroups;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;

import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

public class SpawnGroup extends Spawnpoint {

    private MobArenaExtension _manager;
    private List<Spawnpoint> _spawns = new ArrayList<>(10);

    public SpawnGroup(MobArenaExtension manager, Spawnpoint primary) {
        super(primary.getName(), primary.getSpawnType(), primary.getTeam(), primary.getWorld(), primary.getX(), primary.getY(), primary.getZ(), primary.getYaw(), primary.getPitch());
        _manager = manager;
        _spawns.add(primary);
    }

    public void addSpawn(Spawnpoint spawnpoint) {
        _spawns.add(spawnpoint);
    }

    public void addSpawns(Collection<Spawnpoint> groupSpawns) {
        _spawns.addAll(groupSpawns);
    }

    public List<Spawnpoint> getSpawns() {
        return new ArrayList<>(_spawns);
    }

    @Nullable
    @Override
    public List<Entity> spawn(Arena arena, int count) {
        PreCon.notNull(arena);
        PreCon.greaterThanZero(count);
        PreCon.isValid(arena.equals(_manager.getArena()), "Can only spawn for arena: " + _manager.getArena().getName());

        List<Entity> result = new ArrayList<>(count * _spawns.size());

        for (Spawnpoint spawn : _spawns) {

            int max = _manager.getSpawner().getSettings().getMaxMobs() - _manager.getMobCount();

            if (max <= 0)
                break;

            List<Entity> mobs = spawn.spawn(arena, Math.min(max, count));
            if (mobs == null)
                continue;

            result.addAll(mobs);
        }

        return result;
    }
}
