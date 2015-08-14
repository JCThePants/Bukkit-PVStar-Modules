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


package com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.stats.IPlayerStats;
import com.jcwhatever.pvs.api.stats.StatType;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.Leaderboard;
import org.bukkit.block.Sign;

/**
 * Column used for displaying statistics values.
 */
public class StatisticsColumn extends AbstractColumn implements Comparable<StatisticsColumn> {

    private ColumnSettings _settings;

    /**
     * Constructor.
     *
     * @param leaderboard   The owning leaderboard.
     * @param columnHeader  The header sign.
     * @param settings      The columns settings.
     */
    public StatisticsColumn(Leaderboard leaderboard,
                            Sign columnHeader, ColumnSettings settings) {
        super(leaderboard, columnHeader);

        PreCon.notNull(settings);

        _settings = settings;
    }

    /**
     * Get the columns statistic type.
     */
    public StatType getStatType() {
        return _settings.getStatType();
    }

    /**
     * Get the columns settings.
     */
    public ColumnSettings getSettings() {
        return _settings;
    }

    @Override
    protected String getPlayerStatDisplay(int signLine, IPlayerStats playerStats) {

        double score = playerStats.getScore(getStatType(), _settings.getTrackType());
        return _settings.getLineFormat(signLine) + getStatType().formatDisplay(score);
    }

    @Override
    public int compareTo(StatisticsColumn o) {
        return Integer.compare(getSettings().getPriority(), o.getSettings().getPriority());
    }
}
