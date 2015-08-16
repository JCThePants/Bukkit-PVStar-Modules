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
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriberPriority;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.events.players.PlayerLeaveArenaEvent;
import com.jcwhatever.pvs.api.events.players.PlayerLoseEvent;
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

import java.util.UUID;

public class BasicStatsModule extends PVStarModule implements IEventListener {

    public static final StatType KILLS = new StatType("kills", "Kills", StatTracking.TOTAL_MIN_MAX, StatOrder.ASCENDING);
    public static final StatType DEATHS = new StatType("deaths", "Deaths", StatTracking.TOTAL_MIN_MAX, StatOrder.DESCENDING);
    public static final StatType WINS = new StatType("wins", "Wins", StatTracking.TOTAL, StatOrder.ASCENDING);
    public static final StatType LOSSES = new StatType("losses", "Losses", StatTracking.TOTAL, StatOrder.DESCENDING);
    public static final StatType POINTS = new StatType("points", "Points", StatTracking.TOTAL_MIN_MAX, StatOrder.ASCENDING);

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
    private void onPlayerKill(EntityDeathEvent event) {

        if (event.getEntity().getKiller() == null)
            return;

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity().getKiller());
        player.getSessionStats().increment(KILLS, 1);
    }

    @EventMethod
    private void onPlayerDeath(PlayerDeathEvent event) {

        IArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());
        player.getSessionStats().increment(DEATHS, 1);
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

    @EventMethod(priority = EventSubscriberPriority.FIRST)
    private void onPlayerLeave(PlayerLeaveArenaEvent event) {
        event.getPlayer().getSessionStats().increment(POINTS, event.getPlayer().getTotalPoints());
    }
}
