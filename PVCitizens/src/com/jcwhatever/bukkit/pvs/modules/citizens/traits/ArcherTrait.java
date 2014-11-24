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

package com.jcwhatever.bukkit.pvs.modules.citizens.traits;

import com.jcwhatever.bukkit.generic.utils.ProjectileUtils;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Scheduler.ScheduledTask;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

import net.citizensnpcs.api.ai.EntityTarget;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;

/**
 * Trait that shoots arrows at the targeted living entity.
 */
public class ArcherTrait extends Trait {

    ScheduledTask _task;

    protected ArcherTrait() {
        super("Archer");
    }

    @Override
    public void onAttach() {
        runTask();
    }

    @Override
    public void onDespawn() {
        stopTask();
    }

    @Override
    public void onRemove() {
        stopTask();
    }

    @Override
    public void onSpawn() {
        runTask();
    }

    private void runTask() {
        if (_task != null)
            return;

        NPC npc = getNPC();

        if (!npc.isSpawned())
            return;

        if (!(npc.getEntity() instanceof LivingEntity))
            return;

        _task = Scheduler.runTaskRepeat(PVStarAPI.getPlugin(), 40, 40, new ShootArrow());
    }

    private void stopTask() {
        if (_task == null)
            return;

        _task.cancel();
        _task = null;
    }

    private class ShootArrow implements Runnable {

        @Override
        public void run() {

            NPC npc = getNPC();

            if (!npc.isSpawned()) {
                return;
            }

            Entity entity = npc.getEntity();

            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity)entity;

                EntityTarget entityTarget = npc.getNavigator().getEntityTarget();
                if (entityTarget == null)
                    return;

                LivingEntity target = entityTarget.getTarget();
                if (target == null)
                    return;

                Location targetLocation = target instanceof HumanEntity
                        ? ProjectileUtils.getHeartLocation((HumanEntity) target)
                        : livingEntity.getEyeLocation();

                ProjectileUtils.shootBallistic(livingEntity, targetLocation, 1.0D, Arrow.class);
            }
        }
    }

}
