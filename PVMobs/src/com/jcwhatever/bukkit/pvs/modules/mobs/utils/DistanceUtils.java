package com.jcwhatever.bukkit.pvs.modules.mobs.utils;

import com.jcwhatever.bukkit.generic.pathing.astar.AStar.LocationAdjustment;
import com.jcwhatever.bukkit.generic.pathing.astar.AStarPathFinder;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.paths.PathCache;
import com.jcwhatever.bukkit.pvs.modules.mobs.paths.PathCacheEntry;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        AStarPathFinder groundPath = new AStarPathFinder();
        groundPath.setMaxRange(searchRadius);
        groundPath.setMaxDropHeight(MAX_DROP_HEIGHT);
        groundPath.setMaxIterations(MAX_ITERATIONS);

        int distance = groundPath.getPathDistance(source, destination, LocationAdjustment.FIND_SURFACE);

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
