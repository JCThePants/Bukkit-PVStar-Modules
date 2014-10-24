package com.jcwhatever.bukkit.pvs.modules.mobs.paths;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Scheduler;
import com.jcwhatever.bukkit.generic.utils.Scheduler.TaskHandler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

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