package com.jcwhatever.bukkit.pvs.modules.leaderboards;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.performance.queued.QueueWorker;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.Utils;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.LBCommand;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.UpdateTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LeaderboardsModule extends PVStarModule implements GenericsEventListener {

    private static LeaderboardsModule _instance;

    public  static LeaderboardsModule getInstance() {
        return _instance;
    }

    // map of leader board using scope (arena id) as key
    private Map<UUID, Set<Leaderboard>> _scopedLeaderboards = new HashMap<>(15);

    // map of leader boards using the leader board name as key
    private Map<String, Leaderboard> _namedLeaderboards = new HashMap<>(15);

    private Map<Location, Leaderboard> _leaderboardBlocks = new HashMap<>(500);


    public LeaderboardsModule() {
        super();

        _instance = this;
    }

    @Override
    protected void onRegisterTypes() {
        // do nothing
    }

    @Override
    protected void onEnable() {

        loadLeaderboards();
        PVStarAPI.getEventManager().register(this);
        PVStarAPI.getCommandHandler().registerCommand(LBCommand.class);

        BukkitEventListener _bukkitListener = new BukkitEventListener();
        Bukkit.getPluginManager().registerEvents(_bukkitListener, PVStarAPI.getPlugin());
    }

    @GenericsEventHandler
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

        getDataNode().saveAsync(null);

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
        leaderboard.getDataNode().saveAsync(null);

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

        Set<String> leaderboardNames = getDataNode().getSubNodeNames();

        for (String name : leaderboardNames) {

            IDataNode node = getDataNode().getNode(name);
            String scope = node.getString("scope");
            String worldName = node.getString("world");

            if (scope == null) {
                Msg.warning("Failed to add leaderboard '{0}' because no scope was specified in config.", name);
                continue;
            }

            if (worldName == null) {
                Msg.warning("Failed to add leaderboard '{0}' because no world was specified in config.", name);
                continue;
            }

            if (Bukkit.getServer().getWorld(worldName) == null) {
                Msg.warning("Failed to add leaderboard '{0}' because the world it's in doesn't exist or isn't loaded. ({1})", name, worldName);
                continue;
            }

            Leaderboard leaderboard = instantiateLeaderboard(name, Utils.getIds(scope));
            if (leaderboard == null) {
                Msg.warning("Failed to add leaderboard '{0}'.", name);
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
