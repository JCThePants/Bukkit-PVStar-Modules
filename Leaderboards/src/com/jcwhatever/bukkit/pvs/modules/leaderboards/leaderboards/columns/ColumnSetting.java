package com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns;

import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.SortOrder;
import com.jcwhatever.bukkit.pvs.api.stats.StatTracking.StatTrackType;

import javax.annotation.Nullable;

public class ColumnSetting {

    private IDataNode _dataNode;
    private SortOrder _sortOrder;
    private StatTrackType _trackType;
    private String[] _displayLines;
    private String[] _lineFormats;

    public ColumnSetting(IDataNode dataNode, String[] lineFormats) {
        _dataNode = dataNode;

        _displayLines = new String[] {
                dataNode.getString("sign-line-1"),
                dataNode.getString("sign-line-2"),
                dataNode.getString("sign-line-3"),
                dataNode.getString("sign-line-4")
        };

        _lineFormats = lineFormats;
        _sortOrder = dataNode.getEnum("sort-order", SortOrder.NONE, SortOrder.class);
        _trackType = dataNode.getEnum("tracking", StatTrackType.TOTAL, StatTrackType.class);
    }

    public IDataNode getSettings() {
        return _dataNode;
    }

    public SortOrder getSortOrder() {
        return _sortOrder;
    }

    public StatTrackType getTrackingType() {
        return _trackType;
    }

    public void setSortOrder(SortOrder sortOrder) {
        _sortOrder = sortOrder;

        _dataNode.set("sort-order", sortOrder);
        _dataNode.saveAsync(null);
    }

    @Nullable
    public String getDisplayLine(int index) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, 5);

        return _displayLines[index];
    }

    public void setDisplayLine(int index, @Nullable String line) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, 5);

        _displayLines[index] = line;

        _dataNode.set("sign-line-" + (index + 1), line);
        _dataNode.saveAsync(null);
    }

    public void setDisplayLines(String[] lines) {
        PreCon.notNull(lines);
        PreCon.isValid(lines.length == 4);

        for (int i=0; i < 4; i++) {
            _displayLines[i] = lines[i];

            _dataNode.set("sign-line-" + (i + 1), lines[i]);
        }
        _dataNode.saveAsync(null);
    }

    public String getLineFormat(int index) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, 5);

        return _lineFormats[index];
    }

}
