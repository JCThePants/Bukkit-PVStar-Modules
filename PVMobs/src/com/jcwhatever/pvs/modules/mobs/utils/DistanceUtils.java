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


package com.jcwhatever.pvs.modules.mobs.utils;

import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.paths.PathCache;
import com.jcwhatever.pvs.modules.mobs.paths.PathCacheEntry;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.AStar;
import com.jcwhatever.nucleus.utils.astar.AStarUtils;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;

public class DistanceUtils {

    private DistanceUtils() {}

    public static final int MAX_PATH_DISTANCE = 15;
    public static final int SEARCH_RADIUS = 18;
    public static final byte MAX_DROP_HEIGHT = 6;
    public static final int MAX_ITERATIONS = 5000;


    /**
     * Determine if the specified destination is valid. Uses cached paths if available,
     * otherwise uses AStar path finding.
     *
     * @param destination      The destination location.
     * @param searchRadius     The max radius of valid destinations.
     * @param maxPathDistance  The max path distance to a destination.
     * @return
     */
    public static boolean isValidMobDestination(Arena arena, Spawnpoint source,
                                                Location destination, int searchRadius, int maxPathDistance) {
        PreCon.notNull(destination);

        // must be in same world
        if (!source.getWorld().equals(destination.getWorld()))
            return false;

        // must be within a certain radius
        if (source.distanceSquared(destination) > searchRadius * searchRadius)
            return false;

        // check for cached paths first
        ArenaExtension manager = arena.getExtensionManager().get(MobArenaExtension.NAME);
        if (manager instanceof MobArenaExtension) {

            PathCache pathCache = ((MobArenaExtension) manager).getGroupGenerator().getPathCache();

            PathCacheEntry entry = pathCache.getEntry(source);

            if (entry != null && entry.hasPathCache()) {

                // return cached result
                return entry.isValidDesination(destination);
            }
        }

        // Use real time path checking (slower)
        AStar astar = AStarUtils.getAStar(source.getWorld());
        astar.setRange(searchRadius);
        astar.setMaxDropHeight(MAX_DROP_HEIGHT);
        astar.setMaxIterations(MAX_ITERATIONS);

        int distance = AStarUtils.searchSurface(astar, source, destination)
                    .getPathDistance();

        return distance > -1 && distance <= maxPathDistance;
    }


    public static <T extends Spawnpoint> ArrayList<T> getClosestSpawns(
            Arena arena, Collection<ArenaPlayer> players, Collection<T> spawnpoints, int maxPathDistance) {

        PreCon.notNull(arena);
        PreCon.notNull(players);
        PreCon.notNull(spawnpoints);
        PreCon.greaterThanZero(maxPathDistance);

        ArrayList<T> result = new ArrayList<>(spawnpoints.size());
        if (spawnpoints.isEmpty())
            return result;

        List<T> playerResults = new ArrayList<>(players.size());
        Set<T> spawns = new HashSet<>(spawnpoints);

        for (ArenaPlayer player : players) {

            Location playerLocation  = player.getLocation();

            for (T spawn : spawns) {

                if (isValidMobDestination(arena, spawn, playerLocation, SEARCH_RADIUS, maxPathDistance)) {
                    playerResults.add(spawn);
                }
            }

            if (!playerResults.isEmpty()) {
                spawns.removeAll(playerResults);
                result.addAll(playerResults);
                playerResults.clear();
            }
        }

        return result;
    }



    public static <T extends Spawnpoint> T getClosestSpawn(Collection<T> spawnpoints, Location location, @Nullable T exclude) {
        PreCon.notNull(spawnpoints);
        PreCon.notNull(location);

        double dist;
        double current = 1000000;
        T result = null;

        for (T spawn : spawnpoints) {

            if (!spawn.getWorld().equals(location.getWorld()))
                continue;

            dist = spawn.distanceSquared(location);

            if (dist >= current || dist >= 256.0)
                continue;

            if (spawn.equals(exclude))
                continue;

            current = dist;
            result = spawn;
        }

        return result;
    }


    public static ArenaPlayer getClosestPlayer(Collection<ArenaPlayer> players, Location loc, int maxDistanceSquared) {
        PreCon.notNull(players);
        PreCon.notNull(loc);


        double current = maxDistanceSquared;
        ArenaPlayer result = null;

        for (ArenaPlayer player : players) {

            if (player.isImmobilized())
                continue;

            Location pLoc = player.getLocation();

            if (!pLoc.getWorld().equals(loc.getWorld()))
                continue;

            double dist = pLoc.distanceSquared(loc);
            if (dist >= current || dist >= maxDistanceSquared)
                continue;

            current = dist;
            result = player;
        }

        return result;
    }

}
