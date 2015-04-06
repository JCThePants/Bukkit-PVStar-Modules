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


package com.jcwhatever.pvs.modules.mobs.spawntypes.hostile;

import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.spawns.SpawnType;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

public class WitherSkeletonSpawn extends SpawnType {

    private static final EntityType[] _types = new EntityType[] { EntityType.SKELETON };

    @Override
    public String getName() {
        return "WitherSkeleton";
    }

    @Override
    public String getDescription() {
        return "Wither skeleton mob spawn point.";
    }

    @Override
    public boolean isSpawner() {
        return true;
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public boolean isHostile() {
        return true;
    }

    @Override
    public EntityType[] getEntityTypes() {
        return _types;
    }

    @Nullable
    @Override
    public List<Entity> spawn(IArena arena, Location location, int count) {
        List<Entity> result = new ArrayList<>(count);

        for (int i=0; i < count; i++) {
            Skeleton skeleton = location.getWorld().spawn(location, Skeleton.class);
            skeleton.setSkeletonType(SkeletonType.WITHER);
            skeleton.getEquipment().clear();
            skeleton.getEquipment().setItemInHand(new ItemStack(Material.GOLD_SWORD));
            result.add(skeleton);
        }

        return result;
    }
}
