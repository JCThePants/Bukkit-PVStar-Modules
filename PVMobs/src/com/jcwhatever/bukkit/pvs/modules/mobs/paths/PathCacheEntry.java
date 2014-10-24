package com.jcwhatever.bukkit.pvs.modules.mobs.paths;

import com.jcwhatever.bukkit.generic.file.GenericsByteReader;
import com.jcwhatever.bukkit.generic.file.GenericsByteWriter;
import com.jcwhatever.bukkit.generic.pathing.GroundPathCheck;
import com.jcwhatever.bukkit.generic.pathing.PathAreaFinder;
import com.jcwhatever.bukkit.generic.pathing.PathAreaFinder.PathAreaResults;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.utils.DistanceUtils;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class PathCacheEntry {

    private static int CACHE_FILE_VERSION = 1;

    private final MobArenaExtension _manager;
    private final Spawnpoint _spawnpoint;
    private final Arena _arena;


    private Set<Location> _cachedPaths;
    private boolean _isValidCachedPaths;


    public PathCacheEntry (MobArenaExtension manager, Spawnpoint spawnpoint) {
        PreCon.notNull(manager);
        PreCon.notNull(spawnpoint);

        _arena = manager.getArena();
        _manager = manager;
        _spawnpoint = spawnpoint;
    }

    /**
     * Determine if valid destinations are cached and loaded.
     */
    public boolean hasPathCache() {
        return _cachedPaths != null;
    }

    /**
     * Determine if the destination is valid from the entries spawn point.
     *
     * @param destination  The destination to check.
     */
    public boolean isValidDesination(Location destination) {
        destination = GroundPathCheck.findSolidBlockBelow(destination);

        if (_cachedPaths == null)
            throw new IllegalStateException("Cannot check destination because there is no path cache.");

        if (_isValidCachedPaths)
            return _cachedPaths.contains(destination);
        else
            return !_cachedPaths.contains(destination);
    }

    /**
     * Cache valid destinations from the location to memory and on disk.
     *
     * @param searchRadius     The max radius from this location of valid destinations.
     * @param maxPathDistance  The max distance traveled to get to a location.
     *
     * @throws java.io.IOException
     */
    public void cachePaths(int searchRadius, int maxPathDistance) throws IOException {
        PreCon.greaterThanZero(searchRadius);
        PreCon.greaterThanZero(maxPathDistance);

        PathAreaFinder finder = new PathAreaFinder();
        finder.setMaxDropHeight(DistanceUtils.MAX_DROP_HEIGHT);

        PathAreaResults result = finder.searchPossibleDestinations(_spawnpoint, searchRadius, maxPathDistance);

        if (result.getValid().size() > result.getInvalid().size()) {
            _isValidCachedPaths = false;
            _cachedPaths = result.getInvalid();
        }
        else {
            _isValidCachedPaths = true;
            _cachedPaths = result.getValid();
        }

        savePathCache();
    }


    /**
     * Delete cached paths from memory and on disk.
     *
     * @throws IOException
     */
    public void clearPathCache() throws IOException {

        _cachedPaths = null;
        getPathCacheFile(true); // delete file if exists
    }


    /**
     * Loads cached paths from disk.
     *
     * @return True if cached paths exist and were successfully loaded.
     *
     * @throws IOException
     */
    public boolean loadPathCache() throws IOException, ClassNotFoundException {

        File file = getPathCacheFile(false);

        if (!file.exists())
            return false;

        GenericsByteReader reader = new GenericsByteReader(new FileInputStream(file));

        int version = reader.getInteger();
        if (version != CACHE_FILE_VERSION) {
            Msg.warning("Attempted to load cached paths from outdated file version: " + file.getName());
            Msg.warning("Expected version was " + CACHE_FILE_VERSION + ", file version was: " + version);
            return false;
        }

        reader.getString(); // location name
        Location loc = reader.getLocation();

        World world = loc.getWorld();

        _isValidCachedPaths = reader.getByte() != 0;

        int totalLocations = reader.getInteger();

        Set<Location> locations = new HashSet<Location>(totalLocations);

        for (int i=0; i < totalLocations; i++) {
            Location location = reader.getLocation();
            locations.add(location);
        }

        reader.close();

        _cachedPaths = locations;

        return true;
    }

    /*
     * Get file that stores cached paths for this instance.
     */
    private File getPathCacheFile(boolean deleteIfExists) throws IOException {

        File baseDir = _arena.getDataFolder(_manager);

        File dir = new File(baseDir, "spawn-path-cache");
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }

        File file = new File(dir, _spawnpoint.getName() + ".bin");
        if (deleteIfExists && file.exists() && !file.delete()) {
            return null;
        }

        return file;
    }


    /*
     * Saves current cached paths to disk
     */
    private boolean savePathCache() throws IOException {

        if (_cachedPaths == null)
            return false;

        File file = getPathCacheFile(true);
        if (file == null)
            return false;

        GenericsByteWriter writer = new GenericsByteWriter(new FileOutputStream(file));

        LinkedList<Location> locations = new LinkedList<Location>(_cachedPaths);

        writer.write(CACHE_FILE_VERSION);
        writer.write(_spawnpoint.getName());
        writer.write(_spawnpoint.getLocation());
        writer.write(_isValidCachedPaths ? (byte)1 : (byte)0);

        writer.write(locations.size()); // int

        while (!locations.isEmpty()) {
            Location location = locations.remove();
            writer.write(location);
        }

        writer.close();

        return true;
    }


}