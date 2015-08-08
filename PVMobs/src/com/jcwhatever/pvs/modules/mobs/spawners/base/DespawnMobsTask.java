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
import com.jcwhatever.nucleus.utils.Rand;
import com.jcwhatever.nucleus.utils.astar.AStar;
import com.jcwhatever.nucleus.utils.astar.AStarUtils;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.modules.mobs.DespawnMethod;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.pvs.modules.mobs.spawners.MobRemoveReason;
import com.jcwhatever.pvs.modules.mobs.utils.DistanceUtils;
import org.bukkit.entity.LivingEntity;

import java.util.List;

/*
     * Despawn mobs that are out of range of players
     */
public abstract class DespawnMobsTask implements Runnable {

    private final ISpawner _spawner;
    private final IArena _arena;

    public DespawnMobsTask(MobArenaExtension extension, ISpawner spawner) {
        PreCon.notNull(extension);
        PreCon.notNull(spawner);

        _arena = extension.getArena();
        _spawner = spawner;
    }

    @Override
    public void run() {

        List<LivingEntity> mobs = _spawner.getMobs();

        if (mobs.size() == 0)
            return;

        LivingEntity mob = Rand.get(mobs);

        if (mob.isDead()) {
            _spawner.removeMob(mob, DespawnMethod.REMOVE, MobRemoveReason.KILLED);
        }
        else {
            IArenaPlayer closest = DistanceUtils.getClosestPlayer(
                    _arena.getGame().getPlayers(), mob.getLocation(), getMaxDistanceSquared());

            if (closest == null) {
                _spawner.removeMob(mob, DespawnMethod.REMOVE, MobRemoveReason.OUT_OF_RANGE);
                return;
            }

            if (!mob.hasLineOfSight(closest.getPlayer())) {

                AStar astar = AStarUtils.getAStar(mob.getWorld());
                astar.setMaxDropHeight(DistanceUtils.MAX_DROP_HEIGHT);
                astar.setRange(getMaxDistance());

                int distance = AStarUtils.searchSurface(
                        astar, mob.getLocation(), closest.getLocation())
                        .getPathDistance();

                if (distance == -1 || distance > getMaxPathDistance()) {
                    _spawner.removeMob(mob, DespawnMethod.REMOVE, MobRemoveReason.OUT_OF_RANGE);
                }
            }
        }
    }

    protected abstract int getMaxDistanceSquared();

    protected abstract int getMaxDistance();

    protected abstract int getMaxPathDistance();
}
