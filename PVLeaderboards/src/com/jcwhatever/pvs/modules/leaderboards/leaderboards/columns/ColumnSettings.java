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

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.stats.StatTracking.StatTrackType;
import com.jcwhatever.pvs.api.stats.StatType;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.ColumnStatType;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.MissingStatType;

/**
 * Statistics column settings.
 */
public class ColumnSettings {

    private final ColumnStatType _columnStatType;
    private final String[] _lineFormats;

    /**
     * Constructor.
     *
     * <p>Used when loading an existing column.</p>
     *
     * @param dataNode  The columns data node.
     */
    public ColumnSettings(IDataNode dataNode) {

        int priority = dataNode.getInteger("priority", 100);
        String statName = dataNode.getString("stat");
        StatTrackType trackType = dataNode.getEnum("tracking", StatTrackType.TOTAL, StatTrackType.class);

        StatType type = PVStarAPI.getStatsManager().getType(statName);
        _columnStatType = type == null
                ? new ColumnStatType(priority, new MissingStatType(statName), StatTrackType.TOTAL)
                : new ColumnStatType(priority, type, trackType);

        _lineFormats = new String[] {
                dataNode.getString("format-line-1", ""),
                dataNode.getString("format-line-2", ""),
                dataNode.getString("format-line-3", ""),
                dataNode.getString("format-line-4", "")
        };
    }

    /**
     * Constructor.
     *
     * <p>Used when creating a new column. Saves column statistics to the
     * specified data node.</p>
     *
     * @param dataNode        The columns data node.
     * @param columnStatType  The columns statistics type data.
     */
    public ColumnSettings(IDataNode dataNode, ColumnStatType columnStatType) {

        dataNode.set("stat", columnStatType.getStatType().getName());
        dataNode.set("priority", columnStatType.getPriority());
        dataNode.set("tracking", columnStatType.getTrackType());

        _columnStatType = columnStatType;

        _lineFormats = new String[] {
                dataNode.getString("format-line-1", ""),
                dataNode.getString("format-line-2", ""),
                dataNode.getString("format-line-3", ""),
                dataNode.getString("format-line-4", "")
        };
    }

    /**
     * Get the column statistics info.
     */
    public ColumnStatType getColumnStatType() {
        return _columnStatType;
    }

    /**
     * Get the column sort priority.
     *
     * <p>The sort priority also indicates the column position in the
     * leaderboard.</p>
     */
    public int getPriority() {
        return _columnStatType.getPriority();
    }

    /**
     * Get the statistic type.
     */
    public StatType getStatType() {
        return _columnStatType.getStatType();
    }

    /**
     * Get the statistic tracking type.
     */
    public StatTrackType getTrackType() {
        return _columnStatType.getTrackType();
    }

    /**
     * Get the format prepended to signs at the specified
     * sign line index.
     *
     * @param index  The sign index (0-4)
     */
    public String getLineFormat(int index) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, 4);

        return _lineFormats[index];
    }
}
