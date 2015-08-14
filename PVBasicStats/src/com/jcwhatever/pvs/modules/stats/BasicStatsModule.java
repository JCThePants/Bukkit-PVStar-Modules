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


package com.jcwhatever.pvs.modules.stats;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.collections.IArenaPlayerCollection;
import com.jcwhatever.pvs.api.events.ArenaEndedEvent;
import com.jcwhatever.pvs.api.events.ArenaStartedEvent;
import com.jcwhatever.pvs.api.events.players.PlayerLoseEvent;
import com.jcwhatever.pvs.api.events.players.PlayerPreRemoveFromContextEvent;
import com.jcwhatever.pvs.api.events.players.PlayerWinEvent;
import com.jcwhatever.pvs.api.modules.PVStarModule;
import com.jcwhatever.pvs.api.stats.IArenaStats;
import com.jcwhatever.pvs.api.stats.IPlayerStats;
import com.jcwhatever.pvs.api.stats.StatOrder;
import com.jcwhatever.pvs.api.stats.StatTracking;
import com.jcwhatever.pvs.api.stats.StatType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

public class BasicStatsModule extends PVStarModule implements IEventListener {

    public static final StatType KILLS = new StatType("kills", "Kills", StatTracking.TOTAL_MIN_MAX, StatOrder.ASCENDING);
    public static final StatType DEATHS = new StatType("deaths", "Deaths", StatTracking.TOTAL_MIN_MAX, StatOrder.DESCENDING);
    public static final StatType WINS = new StatType("wins", "Wins", StatTracking.TOTAL, StatOrder.ASCENDING);
    public static final StatType LOSSES = new StatType("losses", "Losses", StatTracking.TOTAL, StatOrder.DESCENDING);
    public static final StatType POINTS = new StatType("points", "Points", StatTracking.TOTAL_MIN_MAX, StatOrder.ASCENDING);

    private final Map<IArena, Map<StatType, Map<IArenaPlayer, SessionStatTracker>>> _playerMatches = new HashMap<>(30);

    private final TripleKeyEntryCache<IArena, StatType, IArenaPlayer, SessionStatTracker> _trackerCache =
            new TripleKeyEntryCache<>();

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

    @EventMethod
    private void onArenaStart(ArenaStartedEvent event) {
        IArenaPlayerCollection players = event.getArena().getGame().getPlayers();

        for (IArenaPlayer player : players) {

            // initialize stats
            getStatTracker(player, KILLS);
            getStatTracker(player, DEATHS);
            getStatTracker(player, POINTS);
        }
    }

    @EventMethod
    private void onPlayerKill(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity().getKiller());

        SessionStatTracker tracker = getStatTracker(player, KILLS);
        if (tracker == null)
            return;

        tracker.increment(1);
    }

    @EventMethod
    private void onPlayerDeath(PlayerDeathEvent event) {

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());

        SessionStatTracker tracker = getStatTracker(player, DEATHS);
        if (tracker == null)
            return;

        tracker.increment(1);
    }

    @EventMethod
    private void onPlayerWin(PlayerWinEvent event) {

        UUID playerId = event.getPlayer().getUniqueId();
        IArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        stats.get(playerId).onSuccess(new FutureResultSubscriber<IPlayerStats>() {
            @Override
            public void on(Result<IPlayerStats> result) {

                assert result.getResult() != null;
                result.getResult().addScore(WINS, 1);
            }
        });
    }

    @EventMethod
    private void onPlayerLose(PlayerLoseEvent event) {

        UUID playerId = event.getPlayer().getUniqueId();
        IArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        stats.get(playerId).onSuccess(new FutureResultSubscriber<IPlayerStats>() {
            @Override
            public void on(Result<IPlayerStats> result) {

                assert result.getResult() != null;
                result.getResult().addScore(LOSSES, 1);

            }
        });
    }

    private void onPlayerLeave(PlayerPreRemoveFromContextEvent event) {

        // get points earned before player leaves
        SessionStatTracker tracker = getStatTracker(event.getPlayer(), POINTS);
        if (tracker == null)
            return;

        tracker.increment(event.getPlayer().getTotalPoints());
    }

    @EventMethod
    private void onArenaEnd(ArenaEndedEvent event) {

        IArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(event.getArena().getId());

        saveSession(event.getArena(), stats, KILLS);
        saveSession(event.getArena(), stats, DEATHS);
        saveSession(event.getArena(), stats, POINTS);

        _playerMatches.remove(event.getArena());

        if (event.getArena().equals(_trackerCache.getKey1())) {
            _trackerCache.reset();
        }
    }

    private void saveSession(IArena arena, IArenaStats stats, final StatType type) {
        Map<IArenaPlayer, SessionStatTracker> sessionMap = getPlayerSessionMap(arena, type);
        if (sessionMap != null) {

            for (Entry<IArenaPlayer, SessionStatTracker> entry : sessionMap.entrySet()) {

                final SessionStatTracker tracker = entry.getValue();

                UUID playerId = entry.getKey().getUniqueId();

                stats.get(playerId).onSuccess(new FutureResultSubscriber<IPlayerStats>() {
                    @Override
                    public void on(Result<IPlayerStats> result) {

                        assert result.getResult() != null;
                        result.getResult().addScore(type, tracker.getTotal());
                    }
                });
            }
        }
    }

    @Nullable
    private Map<IArenaPlayer, SessionStatTracker> getPlayerSessionMap(IArena arena, StatType type) {

        Map<StatType, Map<IArenaPlayer, SessionStatTracker>> typeMap = _playerMatches.get(arena);
        if (typeMap == null)
            return null;

        return typeMap.get(type);
    }

    @Nullable
    private SessionStatTracker getStatTracker(IArenaPlayer player, StatType type) {
        if (player.getArena() == null)
            return null;

        if (_trackerCache.keyEquals(player.getArena(), type, player))
            return _trackerCache.getValue();

        Map<StatType, Map<IArenaPlayer, SessionStatTracker>> typeMap = _playerMatches.get(player.getArena());
        if (typeMap == null) {
            typeMap = new HashMap<>(5);
            _playerMatches.put(player.getArena(), typeMap);
        }

        Map<IArenaPlayer, SessionStatTracker> statMap = typeMap.get(type);
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
