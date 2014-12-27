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


package com.jcwhatever.bukkit.pvs.modules.stats;

import com.jcwhatever.nucleus.events.manager.NucleusEventHandler;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.performance.TripleKeySingleCache;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.bukkit.pvs.api.events.ArenaStartedEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerPreRemoveEvent;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerWinEvent;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.api.stats.ArenaStats;
import com.jcwhatever.bukkit.pvs.api.stats.StatTracking;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;

import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class BasicStatsModule extends PVStarModule implements IEventListener {

    public static final StatType KILLS = new StatType("kills", "Kills", StatTracking.TOTAL_MIN_MAX);
    public static final StatType DEATHS = new StatType("deaths", "Deaths", StatTracking.TOTAL_MIN_MAX);
    public static final StatType WINS = new StatType("wins", "Wins", StatTracking.TOTAL);
    public static final StatType LOSSES = new StatType("losses", "Losses", StatTracking.TOTAL);
    public static final StatType POINTS = new StatType("points", "Points", StatTracking.TOTAL_MIN_MAX);

    private final Map<Arena, Map<StatType, Map<ArenaPlayer, SessionStatTracker>>> _playerMatches = new HashMap<>(30);

    private final TripleKeySingleCache<Arena, StatType, ArenaPlayer, SessionStatTracker> _trackerCache = new TripleKeySingleCache<>();

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

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

    @NucleusEventHandler
    private void onArenaStart(ArenaStartedEvent event) {
        List<ArenaPlayer> players = event.getArena().getGameManager().getPlayers();

        for (ArenaPlayer player : players) {

            // initialize stats
            getStatTracker(player, KILLS);
            getStatTracker(player, DEATHS);
            getStatTracker(player, POINTS);
        }
    }

    @NucleusEventHandler
    private void onPlayerKill(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity().getKiller());

        SessionStatTracker tracker = getStatTracker(player, KILLS);
        if (tracker == null)
            return;

        tracker.increment(1);
    }

    @NucleusEventHandler
    private void onPlayerDeath(PlayerDeathEvent event) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());

        SessionStatTracker tracker = getStatTracker(player, DEATHS);
        if (tracker == null)
            return;

        tracker.increment(1);
    }

    @NucleusEventHandler
    private void onPlayerWin(PlayerWinEvent event) {
        ArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        stats.addScore(WINS, event.getPlayer().getUniqueId(), 1);
    }

    @NucleusEventHandler
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

    @NucleusEventHandler
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
