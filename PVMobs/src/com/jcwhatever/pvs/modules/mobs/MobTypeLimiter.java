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

import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.spawns.SpawnType;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Mob type limit settings.
 */
public class MobTypeLimiter {

    // stores max spawn limit for an entity type
    private final Map<EntityType, Integer> _mobLimits = new EnumMap<>(EntityType.class);

    // count the number of entities of each type spawned
    private final ElementCounter<EntityType> _mobCounter = new ElementCounter<EntityType>(
            ElementCounter.RemovalPolicy.KEEP_COUNTING);

    private final IDataNode _dataNode;

    /**
     * Constructor.
     *
     * @param dataNode  The limiter data node.
     */
    public MobTypeLimiter(IDataNode dataNode) {
        PreCon.notNull(dataNode);

        _dataNode = dataNode;
    }

    /**
     * Determine if a spawn type is allowed to spawn.
     *
     * @param type  The spawn type.
     */
    public boolean canSpawnType(SpawnType type) {
        PreCon.notNull(type);

        // check to see if limits have been imposed
        if (_mobLimits.size() == 0) {
            return true; // no limits, can spawn
        }

        // make sure the spawn type hasn't reached its spawn limit
        EntityType[] entityTypes = type.getEntityTypes();
        if (entityTypes == null)
            return false;

        // check each entity type to see if its limit is reached.
        for (EntityType entityType : entityTypes) {

            Integer limit = _mobLimits.get(entityType);
            if (limit == null || limit < 0) {
                continue;
            }

            if (limit == 0) {
                return false;
            }

            int count = _mobCounter.count(entityType);

            if (count >= limit) {
                return false;
            }
        }

        return true;
    }

    /**
     * Get the limit for an entity type. Returns -1
     * if there is no limit.
     *
     * @param type  The entity type.
     */
    public int get(EntityType type) {
        Integer value = _mobLimits.get(type);
        return (value != null) ? value : -1;
    }

    /**
     * Set the limit for an entity type.
     *
     * @param type   The entity type.
     * @param limit  The max allowed to spawn.
     */
    public void set(EntityType type, int limit) {
        PreCon.notNull(type);

        _mobLimits.put(type, limit);

        if (limit >= 0)
            _dataNode.set(type.name(), limit);
        else
            _dataNode.remove(type.name());

        _dataNode.save();
    }

    /**
     * Remove mob limit for an entity type.
     *
     * @param type  The entity type.
     */
    public void remove(EntityType type) {
        PreCon.notNull(type);

        if (_mobLimits.remove(type) != null) {
            _dataNode.remove(type.name());
            _dataNode.save();
        }
    }

    /**
     * Increment or decrement the entity type counter by the specified
     * amount.
     *
     * @param type    The entity type.
     * @param amount  The amount.
     */
    public void increment(EntityType type, int amount) {
        PreCon.notNull(type);

        _mobCounter.add(type, amount);
    }

    /**
     * Get the current count of an entity type.
     *
     * @param type  The entity type.
     */
    public int getCount(EntityType type) {
        PreCon.notNull(type);

        return _mobCounter.count(type);
    }

    /**
     * Reset all mob type counts to 0.
     */
    public void resetCount() {
        _mobCounter.reset();
    }

    private void load() {
        // entity type limits
        _mobLimits.clear();

        for (IDataNode node : _dataNode) {

            EntityType type = EnumUtils.getEnum(node.getName(), EntityType.class);
            if (type == null)
                continue;

            int limit = node.getInteger("", -1);
            if (limit < 0)
                continue;

            _mobLimits.put(type, limit);
        }
    }
}
