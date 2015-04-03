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


package com.jcwhatever.pvs.modules.mobs.paths;

import com.jcwhatever.nucleus.managed.scheduler.TaskHandler;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.Scheduler;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.annotation.Nullable;

public class PathCache {

    private final Map<String, PathCacheEntry> _entries;

    public PathCache (MobArenaExtension manager, Collection<? extends Spawnpoint> spawns) {
        PreCon.notNull(manager);
        PreCon.notNull(spawns);

        _entries = new HashMap<>(spawns.size());

        for (Spawnpoint spawn : spawns) {
            PathCacheEntry entry = new PathCacheEntry(manager, spawn);
            _entries.put(spawn.getSearchName(), entry);
        }

        try {
            loadPaths();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    public PathCacheEntry getEntry(Spawnpoint spawn) {
        return _entries.get(spawn.getSearchName());
    }

    /**
     * Cache possible mob path destinations to a file. When paths are cached,
     * The cache results are used instead of A-Star path finding to determine if
     * a player is in range of a spawn.
     *
     * @param searchRadius     The maximum radius for valid destinations around a spawn point.
     * @param maxPathDistance  The maximum distance traveled to get to a destination from the spawn point.
     */
    public void cachePaths(final int searchRadius, final int maxPathDistance) {
        PreCon.greaterThanZero(searchRadius);
        PreCon.greaterThanZero(maxPathDistance);

        if (_entries.size() == 0)
            return;

        final LinkedList<PathCacheEntry> entries = new LinkedList<>(_entries.values());

        Scheduler.runTaskRepeat(PVStarAPI.getPlugin(), 7, 7, new TaskHandler() {
            @Override
            public void run() {

                if (entries.isEmpty()) {
                    cancelTask();
                    return;
                }

                PathCacheEntry entry = entries.remove();

                try {
                    entry.cachePaths(searchRadius, maxPathDistance);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    /**
     * Load cached paths from an arena.
     *
     * @throws java.io.IOException
     * @throws java.lang.ClassNotFoundException
     */
    public void loadPaths() throws IOException, ClassNotFoundException {

        for (PathCacheEntry entry : _entries.values()) {
            entry.loadPathCache();
        }
    }

    /**
     * Removes cached paths from an arena.
     *
     * @throws java.io.IOException
     */
    public void clearCachePaths() throws IOException {

        for (PathCacheEntry entry : _entries.values()) {
            entry.clearPathCache();
        }
    }



}
