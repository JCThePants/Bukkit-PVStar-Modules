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


package com.jcwhatever.pvs.modules.mobs.spawngroups;

import com.jcwhatever.pvs.api.spawns.Spawnpoint;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.paths.PathCache;
import com.jcwhatever.pvs.modules.mobs.utils.DistanceUtils;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.astar.AStar;
import com.jcwhatever.nucleus.utils.astar.AStarUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SpawnGroupGenerator {

    private final Map<String, Spawnpoint> _mobSpawns;
    private final MobArenaExtension _manager;
    private final IDataNode _groupsNode;
    private final IDataNode _dataNode;
    private PathCache _pathCache;

    private List<Spawnpoint> _spawnGroups;
    private boolean _groupsLoaded;


    public SpawnGroupGenerator(MobArenaExtension manager, Collection<Spawnpoint> spawnpoints) {
        PreCon.notNull(manager);
        PreCon.notNull(spawnpoints);

        _manager = manager;
        _mobSpawns = new HashMap<>(spawnpoints.size());
        _dataNode = manager.getDataNode().getNode("spawn-groups");
        _groupsNode = manager.getDataNode().getNode("spawn-groups.groups");

        for (Spawnpoint spawn : spawnpoints) {
            _mobSpawns.put(spawn.getSearchName(), spawn);
        }

        loadSpawnGroups(false);

        assert _spawnGroups != null;

        if (_spawnGroups.size() == 0 && spawnpoints.size() > 0) {
            Msg.warning("PVMobs extension spawn group error. Groups are empty. " +
                            "Cache might be corrupted in arena '{0}'.",
                    manager.getArena().getName());
        }
    }

    public PathCache getPathCache() {
        return _pathCache;
    }

    public void reloadSpawnGroups() {
        if (!loadSpawnGroups(true)) {
            new CreateSpawnGroups().run();
        }
    }

    public List<Spawnpoint> getSpawnGroups() {
        if (_spawnGroups == null) {

            if (!_groupsLoaded && !loadSpawnGroups(false)) {
                new CreateSpawnGroups().run();
            }
            else {
                return new ArrayList<>(_mobSpawns.values());
            }
        }

        return new ArrayList<>(_spawnGroups);
    }

    public void clearGroupCache() {
        _groupsNode.clear();
        _groupsNode.save();
    }

    public void clearGroups() {
        _spawnGroups = null;
        clearGroupCache();
    }

    public void generateGroups() {
        new CreateSpawnGroups().run();
    }


    public boolean isSpawnsChanged() {

        Set<String> currentSpawnList = _mobSpawns.keySet();
        List<String> cachedSpawnList = _dataNode.getStringList("spawns", null);
        if (cachedSpawnList == null)
            return true;

        // if the size has changed, then spawns have changed.
        if (currentSpawnList.size() != cachedSpawnList.size())
            return true;

        // check that all spawn names are the same.
        for (String spawnName : cachedSpawnList) {
            if (!currentSpawnList.contains(spawnName))
                return true;
        }

        return false;
    }


    private boolean loadSpawnGroups(boolean force) {

        if (force || isSpawnsChanged()) {
            generateGroups();
        }

        int groupSize = _groupsNode.size();

        if (groupSize == 0) {
            _pathCache = new PathCache(_manager, new ArrayList<Spawnpoint>(0));
            return false;
        }

        List<Spawnpoint> groups = new ArrayList<>(groupSize);

        for (IDataNode node : _groupsNode) {

            String rawNames = node.getString("");
            if (rawNames == null)
                continue;

            String[] spawnNames = TextUtils.PATTERN_COMMA.split(rawNames.toLowerCase());

            SpawnGroup group = null;

            List<Spawnpoint> groupSpawns = new ArrayList<>(spawnNames.length);

            for (String spawnName : spawnNames) {

                Spawnpoint spawn = _mobSpawns.get(spawnName);
                if (spawn == null)
                    continue;

                if (spawnName.equalsIgnoreCase(node.getName())) {
                    group = new SpawnGroup(_manager, spawn);
                }

                groupSpawns.add(spawn);
            }

            if (group != null) {
                group.addSpawns(groupSpawns);
                groups.add(group);
            }
        }

        _spawnGroups = groups;
        _groupsLoaded = true;

        _pathCache = new PathCache(_manager, groups);

        return true;
    }


    private void saveSpawnGroups() {

        _groupsNode.clear();

        List<Spawnpoint> groups = new ArrayList<>(_spawnGroups);

        for (Spawnpoint group : groups) {

            List<Spawnpoint> spawns = ((SpawnGroup)group).getSpawns();
            if (spawns.isEmpty())
                continue;

            List<String> spawnNames = new ArrayList<String>(spawns.size());

            for (Spawnpoint spawn : spawns) {
                spawnNames.add(spawn.getName());
            }

            _groupsNode.set(group.getName(), TextUtils.concat(spawnNames, ","));
        }

        // add spawn list to data node so changes to spawns can be detected
        List<String> currentSpawnList = new ArrayList<>(_mobSpawns.keySet());
        _pathCache = new PathCache(_manager, groups);
        _dataNode.set("spawns", currentSpawnList);
        _dataNode.save();
    }

    /**
     * Generate spawn groups
     */
    private class CreateSpawnGroups implements Runnable {

        @Override
        public void run() {

            List<Spawnpoint> groups = new ArrayList<>(_mobSpawns.size());

            LinkedList<Spawnpoint> spawnPool = new LinkedList<>(_mobSpawns.values());
            int searchRadiusSquared = DistanceUtils.SEARCH_RADIUS * DistanceUtils.SEARCH_RADIUS;

            while (!spawnPool.isEmpty()) {

                // get a spawn and make it the primary of a new spawn group
                Spawnpoint primary = spawnPool.remove();
                SpawnGroup group = new SpawnGroup(_manager, primary);

                AStar astar = AStarUtils.getAStar(primary.getWorld());
                astar.setMaxDropHeight(DistanceUtils.MAX_DROP_HEIGHT);
                astar.setMaxIterations(DistanceUtils.MAX_ITERATIONS);
                astar.setRange(DistanceUtils.SEARCH_RADIUS);

                // find candidates to add to the group
                LinkedList<Spawnpoint> groupCandidates = new LinkedList<>(spawnPool);

                while (!groupCandidates.isEmpty()) {

                    Spawnpoint candidate = groupCandidates.remove();

                    double distance = primary.distanceSquared(candidate);

                    if (distance <= searchRadiusSquared) {

                        int pathDistance = AStarUtils.searchSurface(astar, primary, candidate)
                                .getPathDistance();

                        if (pathDistance > -1 && pathDistance <= DistanceUtils.SEARCH_RADIUS) {
                            group.addSpawn(candidate);

                            // remove candidate from spawn pool
                            spawnPool.remove(candidate);
                        }
                    }
                }

                groups.add(group);
            }


            _spawnGroups = groups;

            _pathCache = new PathCache(_manager, groups);

            try {
                _pathCache.clearCachePaths();
            } catch (IOException e) {
                e.printStackTrace();
            }
            _pathCache.cachePaths(16, 18);

            saveSpawnGroups();
        }
    }

}
