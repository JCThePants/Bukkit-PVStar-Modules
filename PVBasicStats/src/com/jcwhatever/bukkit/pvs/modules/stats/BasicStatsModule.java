package com.jcwhatever.bukkit.pvs.modules.stats;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.performance.TripleKeySingleCache;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.ArenaStartedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaDeathEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerPreRemoveEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerWinEvent;
import com.jcwhatever.bukkit.pvs.api.stats.ArenaStats;
import com.jcwhatever.bukkit.pvs.api.stats.StatTracking;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class BasicStatsModule extends PVStarModule implements GenericsEventListener {

    public static final StatType KILLS = new StatType("kills", "Kills", StatTracking.TOTAL_MIN_MAX);
    public static final StatType DEATHS = new StatType("deaths", "Deaths", StatTracking.TOTAL_MIN_MAX);
    public static final StatType WINS = new StatType("wins", "Wins", StatTracking.TOTAL);
    public static final StatType LOSSES = new StatType("losses", "Losses", StatTracking.TOTAL);
    public static final StatType POINTS = new StatType("points", "Points", StatTracking.TOTAL_MIN_MAX);

    private final Map<Arena, Map<StatType, Map<ArenaPlayer, SessionStatTracker>>> _playerMatches = new HashMap<>(30);

    private final TripleKeySingleCache<Arena, StatType, ArenaPlayer, SessionStatTracker> _trackerCache = new TripleKeySingleCache<>();

    @Override
    protected void onRegisterTypes() {
        PVStarAPI.getStatsManager().registerType(KILLS);
        PVStarAPI.getStatsManager().registerType(DEATHS);
        PVStarAPI.getStatsManager().registerType(WINS);
        PVStarAPI.getStatsManager().registerType(LOSSES);
        PVStarAPI.getStatsManager().registerType(POINTS);
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getEventManager().register(this);
    }

    @GenericsEventHandler
    private void onArenaStart(ArenaStartedEvent event) {
        List<ArenaPlayer> players = event.getArena().getGameManager().getPlayers();

        for (ArenaPlayer player : players) {

            // initialize stats
            getStatTracker(player, KILLS);
            getStatTracker(player, DEATHS);
            getStatTracker(player, POINTS);
        }
    }

    @GenericsEventHandler
    private void onPlayerKill(PlayerArenaKillEvent event) {

        SessionStatTracker tracker = getStatTracker(event.getPlayer(), KILLS);
        if (tracker == null)
            return;

        tracker.increment(1);
    }

    @GenericsEventHandler
    private void onPlayerDeath(PlayerArenaDeathEvent event) {
        SessionStatTracker tracker = getStatTracker(event.getPlayer(), DEATHS);
        if (tracker == null)
            return;

        tracker.increment(1);
    }

    @GenericsEventHandler
    private void onPlayerWin(PlayerWinEvent event) {
        ArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        stats.addScore(WINS, event.getPlayer().getUniqueId(), 1);
    }

    @GenericsEventHandler
    private void onPlayerLose(PlayerLoseEvent event) {
        ArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        stats.addScore(LOSSES, event.getPlayer().getUniqueId(), 1);
    }

    private void onPlayerLeave(PlayerPreRemoveEvent event) {

        // get points earned before player leaves
        SessionStatTracker tracker = getStatTracker(event.getPlayer(), POINTS);
        if (tracker == null)
            return;

        tracker.increment(event.getPlayer().getTotalPoints());
    }

    @GenericsEventHandler
    private void onArenaEnd(ArenaEndedEvent event) {

        ArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        saveSession(event.getArena(), stats, KILLS);
        saveSession(event.getArena(), stats, DEATHS);
        saveSession(event.getArena(), stats, POINTS);

        _playerMatches.remove(event.getArena());

        if (event.getArena().equals(_trackerCache.getKey1())) {
            _trackerCache.reset();
        }
    }

    private void saveSession(Arena arena, ArenaStats stats, StatType type) {
        Map<ArenaPlayer, SessionStatTracker> sessionMap = getPlayerSessionMap(arena, type);
        if (sessionMap != null) {

            for (Entry<ArenaPlayer, SessionStatTracker> arenaPlayerSessionStatTrackerEntry : sessionMap.entrySet()) {

                SessionStatTracker tracker = arenaPlayerSessionStatTrackerEntry.getValue();

                stats.addScore(type, arenaPlayerSessionStatTrackerEntry.getKey().getUniqueId(), tracker.getTotal());
            }
        }
    }

    @Nullable
    private Map<ArenaPlayer, SessionStatTracker> getPlayerSessionMap(Arena arena, StatType type) {

        Map<StatType, Map<ArenaPlayer, SessionStatTracker>> typeMap = _playerMatches.get(arena);
        if (typeMap == null)
            return null;

        return typeMap.get(type);
    }

    @Nullable
    private SessionStatTracker getStatTracker(ArenaPlayer player, StatType type) {
        if (player.getArena() == null)
            return null;

        if (_trackerCache.keyEquals(player.getArena(), type, player))
            return _trackerCache.getValue();

        Map<StatType, Map<ArenaPlayer, SessionStatTracker>> typeMap = _playerMatches.get(player.getArena());
        if (typeMap == null) {
            typeMap = new HashMap<>(5);
            _playerMatches.put(player.getArena(), typeMap);
        }

        Map<ArenaPlayer, SessionStatTracker> statMap = typeMap.get(type);
        if (statMap == null) {
            statMap = new HashMap<>(100);
            typeMap.put(type, statMap);
        }

        SessionStatTracker tracker = statMap.get(player);
        if (tracker == null) {
            tracker = new SessionStatTracker(player, type);
            statMap.put(player, tracker);
        }

        _trackerCache.set(player.getArena(), type, player, tracker);

        return tracker;
    }
}
