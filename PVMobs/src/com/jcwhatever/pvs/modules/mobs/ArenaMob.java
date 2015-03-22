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


package com.jcwhatever.pvs.modules.mobs;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.arena.Arena;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

public class ArenaMob {

    private static Map<Entity, Arena> _mobs = new WeakHashMap<>(50);

    public static void registerEntity(Entity entity, Arena arena) {
        PreCon.notNull(entity);
        PreCon.notNull(arena);

        _mobs.put(entity, arena);
    }

    public static void registerLivingEntities(Collection<LivingEntity> entities, Arena arena) {
        PreCon.notNull(entities);
        PreCon.notNull(arena);

        for (LivingEntity entity : entities)
            _mobs.put(entity, arena);
    }

    public static void registerEntities(Collection<Entity> entities, Arena arena) {
        PreCon.notNull(entities);
        PreCon.notNull(arena);

        for (Entity entity : entities)
            _mobs.put(entity, arena);
    }

    public static Arena getEntityArena(Entity entity) {
        return _mobs.get(entity);
    }

}
