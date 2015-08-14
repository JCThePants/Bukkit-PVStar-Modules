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

package com.jcwhatever.pvs.modules.leaderboards.leaderboards;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.pvs.api.stats.StatTracking;
import com.jcwhatever.pvs.api.stats.StatType;

/**
 * Data object for column statistic and tracking type.
 */
public class ColumnStatType implements Comparable<ColumnStatType> {

    private final int _priority;
    private final StatType _statType;
    private final StatTracking.StatTrackType _trackType;

    /**
     * Constructor.
     *
     * @param priority   The statistics priority used for sorting.
     * @param statType   The columns statistic type.
     * @param trackType  The columns tracking type.
     */
    public ColumnStatType(int priority, StatType statType, StatTracking.StatTrackType trackType) {
        PreCon.notNull(statType);
        PreCon.notNull(trackType);

        _priority = priority;
        _statType = statType;
        _trackType = trackType;
    }

    /**
     * Get the sort order priority of the statistic.
     */
    public int getPriority() {
        return _priority;
    }

    /**
     * Get the columns statistic type.
     */
    public StatType getStatType() {
        return _statType;
    }

    /**
     * Get the tracking type for the column.
     */
    public StatTracking.StatTrackType getTrackType() {
        return _trackType;
    }

    @Override
    public int compareTo(ColumnStatType other) {
        return Integer.compare(_priority, other._priority);
    }
}
