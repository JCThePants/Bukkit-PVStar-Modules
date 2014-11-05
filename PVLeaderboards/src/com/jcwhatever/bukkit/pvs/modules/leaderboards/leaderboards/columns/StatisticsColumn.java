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


package com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns;

import com.jcwhatever.bukkit.generic.collections.TimedMap;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.stats.ArenaStats;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.block.Sign;

import java.util.List;

public class StatisticsColumn extends AbstractColumn {

    private final StatType _statType;
    // keyed to player id as a string
    private TimedMap<String, Double> _cachedValues = new TimedMap<String, Double>(1);
    private ColumnSetting _settings;

    public StatisticsColumn(StatType type, Leaderboard leaderboard, Sign columnHeader, ColumnSetting settings) {
        super(leaderboard, columnHeader);

        _statType = type;
        _settings = settings;
    }

    public StatType getStatType() {
        return _statType;
    }

    public ColumnSetting getSettings() {
        return _settings;
    }

    @Override
    protected String getPlayerStatDisplay(int signLine, String playerId) {
        PreCon.notNullOrEmpty(playerId);

        Double value = _cachedValues.get(playerId);
        if (value == null) {
            value = getPlayerStatValue(playerId);
        }

        return _settings.getLineFormat(signLine) + getStatType().formatDisplay(value);
    }

    @Override
    public double getPlayerStatValue(String playerId) {
        PreCon.notNull(playerId);

        double value = 0;

        List<ArenaStats> arenaStats = getLeaderboard().getArenaStats();

        for (ArenaStats stats : arenaStats) {
            value += stats.getValue(getStatType(), playerId, getSettings().getTrackingType());
        }

        _cachedValues.put(playerId, value);

        return value;
    }
}
