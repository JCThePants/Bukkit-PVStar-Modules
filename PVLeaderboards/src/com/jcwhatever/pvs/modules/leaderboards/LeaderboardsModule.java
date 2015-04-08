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


package com.jcwhatever.pvs.modules.leaderboards;

import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.leaderboards.commands.LBCommand;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.UpdateTask;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.performance.queued.QueueWorker;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LeaderboardsModule extends PVStarModule implements IEventListener {

    private static LeaderboardsModule _module;

    public  static LeaderboardsModule getModule() {
        return _module;
    }

    // map of leader board using scope (arena id) as key
    private Map<UUID, Set<Leaderboard>> _scopedLeaderboards = new HashMap<>(15);

    // map of leader boards using the leader board name as key
    private Map<String, Leaderboard> _namedLeaderboards = new HashMap<>(15);

    private Map<Location, Leaderboard> _leaderboardBlocks = new HashMap<>(500);


    public LeaderboardsModule() {
        super();

        _module = this;
    }

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        loadLeaderboards();
        PVStarAPI.getEventManager().register(this);
        PVStarAPI.getCommandDispatcher().registerCommand(LBCommand.class);

        BukkitEventListener _bukkitListener = new BukkitEventListener();
        Bukkit.getPluginManager().registerEvents(_bukkitListener, PVStarAPI.getPlugin());
    }

    @EventMethod
    private void onArenaEnded(ArenaEndedEvent event) {

        Set<Leaderboard> leaderboards = _scopedLeaderboards.get(event.getArena().getId());
        if (leaderboards == null)
            return;

        for (Leaderboard leaderboard : leaderboards) {
            QueueWorker.get().addTask(new UpdateTask(leaderboard));
        }
    }

    /**
     * Update all leader board displays
     */
    public void updateAll() {
        for (Leaderboard lb : _namedLeaderboards.values()) {
            lb.update();
        }
    }

    /**
     * Update all leader boards that contain the specified scope
     *
     * @param arenaId  The name of the arena/scope
     */
    public void updateArenas(UUID arenaId) {

        Set<Leaderboard> leaderboards = _scopedLeaderboards.get(arenaId);
        if (leaderboards == null)
            return;

        for (Leaderboard leaderboard : leaderboards) {
            leaderboard.update();
        }
    }

    /**
     * Get a leader board by name
     * @param name - The name of the leader board
     */
    public Leaderboard getLeaderboard(String name) {
        return _namedLeaderboards.get(name.toLowerCase());
    }

    /**
     * Get a list of leader boards
     *
     * @return
     */
    public List<Leaderboard> getLeaderboards() {
        return new ArrayList<>(_namedLeaderboards.values());
    }

    /**
     * Determine if the block at the specified location is part of
     * a leader board.
     * @param blockLocaton
     * @return
     */
    public boolean isLeaderboardBlock(Location blockLocaton) {
        Leaderboard leaderboard = _leaderboardBlocks.get(blockLocaton);
        return leaderboard != null && leaderboard.isEnabled();
    }

    /**
     * Add a new leader board.
     *
     */
    public Leaderboard addLeaderboard(String name, Collection<UUID> arenaIds) {

        String key = name.toLowerCase();

        if (_namedLeaderboards.containsKey(key))
            return null;

        Leaderboard leaderboard = instantiateLeaderboard(name, arenaIds);

        getDataNode().save();

        return leaderboard;
    }

    /**
     * Remove a leader board. World blocks need to be removed manually.
     *
     * @param name - The name of the leader board to remove.
     * @return
     */
    public boolean removeLeaderboard(String name) {
        PreCon.notNullOrEmpty(name);

        String key = name.toLowerCase();

        Leaderboard leaderboard = _namedLeaderboards.remove(key);
        if (leaderboard == null)
            return false;

        List<UUID> arenaIds = leaderboard.getArenaIds();

        for (UUID arenaId : arenaIds) {

            Set<Leaderboard> boards = _scopedLeaderboards.get(arenaId);
            if (boards == null)
                continue;

            boards.remove(leaderboard);
        }

        leaderboard.getDataNode().clear();
        leaderboard.getDataNode().save();

        removeBlockLocations(leaderboard);

        return true;
    }


    public void addBlockLocations(Leaderboard leaderboard) {

        List<Block> blocks = leaderboard.getAttachedBlocks();

        for (Block block : blocks) {
            _leaderboardBlocks.put(block.getLocation(), leaderboard);
        }
    }

    /**
     * Remove leader board block locations for the specified leader board.
     * @param leaderboard
     */
    public void removeBlockLocations(Leaderboard leaderboard) {

        List<Location> blockLocations = new ArrayList<Location>(_leaderboardBlocks.keySet());

        for (Location loc : blockLocations) {

            Leaderboard lb = _leaderboardBlocks.get(loc);

            if (lb.getName().equals(leaderboard.getName())) {
                _leaderboardBlocks.remove(loc);
            }
        }
    }

    private void loadLeaderboards() {

        for (IDataNode node : getDataNode()) {

            String scope = node.getString("scope");
            String worldName = node.getString("world");

            if (scope == null) {
                Msg.warning("Failed to add leaderboard '{0}' because no scope was specified in config.", node.getName());
                continue;
            }

            if (worldName == null) {
                Msg.warning("Failed to add leaderboard '{0}' because no world was specified in config.", node.getName());
                continue;
            }

            if (Bukkit.getServer().getWorld(worldName) == null) {
                Msg.warning("Failed to add leaderboard '{0}' because the world it's in " +
                        "doesn't exist or isn't loaded. ({1})", node.getName(), worldName);
                continue;
            }

            Leaderboard leaderboard = instantiateLeaderboard(node.getName(), TextUtils.parseUUID(scope.split(",")));
            if (leaderboard == null) {
                Msg.warning("Failed to add leaderboard '{0}'.", node.getName());
                continue;
            }

            addBlockLocations(leaderboard);
        }
    }


    private Leaderboard instantiateLeaderboard(String name, Collection<UUID> arenaIds) {

        String key = name.toLowerCase();

        Leaderboard leaderboard = new Leaderboard(name, arenaIds, getDataNode().getNode(name));

        for (UUID arenaId : arenaIds) {

            Set<Leaderboard> leaderboards = _scopedLeaderboards.get(arenaId);
            if (leaderboards == null) {

                leaderboards = new HashSet<>(10);
                _scopedLeaderboards.put(arenaId, leaderboards);
            }
            leaderboards.add(leaderboard);
        }

        _namedLeaderboards.put(key, leaderboard);

        return leaderboard;
    }
}
