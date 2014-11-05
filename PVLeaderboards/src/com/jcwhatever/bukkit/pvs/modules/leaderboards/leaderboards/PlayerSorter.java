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


package com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards;

import com.jcwhatever.bukkit.pvs.api.stats.ArenaStats;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PlayerSorter {

    private final Leaderboard _leaderboard;
    private final List<SortOrder> _sortOrders;
    private final List<StatisticsColumn> _columns;
    private final Set<String> _idCheckBuffer = new HashSet<>(1000);

    public PlayerSorter (Leaderboard leaderboard) {
        _leaderboard = leaderboard;
        List<StatType> _statTypes = leaderboard.getColumnStatTypes();
        _sortOrders = new ArrayList<>(_statTypes.size());
        _columns = new ArrayList<>(_statTypes.size());

        // get column sort orders
        for (StatType _statType : _statTypes) {

            StatisticsColumn column = leaderboard.getColumn(_statType.getName());

            _columns.add(column);
            _sortOrders.add(column.getSettings().getSortOrder());
        }
    }

    public Leaderboard getLeaderboard() {
        return _leaderboard;
    }

    public List<String> getSortedPlayerIds() {

        final List<ArenaStats> arenaStats = _leaderboard.getArenaStats();
        final LinkedList<String> playerIds = new LinkedList<>();

        if (arenaStats.isEmpty())
            return new ArrayList<>(0);

        for (ArenaStats stats : arenaStats) {
            for (StatisticsColumn column : _columns) {
                _idCheckBuffer.addAll(stats.getRawPlayerIds(column.getStatType()));
            }
        }

        playerIds.addAll(_idCheckBuffer);
        _idCheckBuffer.clear();

        Collections.sort(playerIds, new Comparator<String>() {
            public int compare(String playerId1, String playerId2) {

                int result = 0;
                for (int i = 0; i < _sortOrders.size(); i++) {
                    SortOrder sortOrder = _sortOrders.get(i);

                    if (sortOrder == SortOrder.NONE) {
                        if (i == 0)
                            sortOrder = SortOrder.ASCENDING;
                        else
                            continue;
                    }

                    StatisticsColumn column = _columns.get(i);

                    double value1 = 0;
                    double value2 = 0;

                    for (ArenaStats stats : arenaStats) {

                        value1 += stats.getValue(column.getStatType(), playerId1, column.getSettings().getTrackingType());
                        value2 += stats.getValue(column.getStatType(), playerId2, column.getSettings().getTrackingType());
                    }

                    result = compareVals(value1, value2, sortOrder);

                    // if results are same, continue to sort by next statistic
                    if (result == 0)
                        continue;

                    return result;
                }
                return result;
            }
        });

        return new ArrayList<>(playerIds);
    }

    private static int compareVals(double value1, double value2, SortOrder sortOrder) {
        if (value1 == value2)
            return 0;

        switch (sortOrder) {
            case ASCENDING:
                return value1 < value2 ? 1 : -1;
            case DESCENDING:
                return value1 > value2 ? 1 : -1;
            default:
                return 0;
        }
    }

}
