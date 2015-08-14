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

import com.jcwhatever.nucleus.mixins.ILoadable;
import com.jcwhatever.nucleus.mixins.INamed;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.SignUtils;
import com.jcwhatever.nucleus.utils.observer.future.FutureResultSubscriber;
import com.jcwhatever.nucleus.utils.observer.future.Result;
import com.jcwhatever.nucleus.utils.text.TextFormat;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.stats.IPlayerStats;
import com.jcwhatever.pvs.api.stats.IStatsFilter;
import com.jcwhatever.pvs.api.stats.StatTracking.StatTrackType;
import com.jcwhatever.pvs.api.stats.StatType;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.AnchorColumn;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.ColumnSettings;
import com.jcwhatever.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Leaderboard.
 */
public class Leaderboard implements ILoadable, INamed {

    private final String _name;

    private List<UUID> _arenaIds;

    private Location _anchorLocation;
    private Sign _anchorSign;

    private String[] _lineFormats; // add coloring to lines

    private AnchorColumn _anchorColumn;
    private List<StatisticsColumn> _columns;

    private IDataNode _dataNode;
    private IDataNode _columnsNode;

    private boolean _isLoaded = false;
    private boolean _isEnabled = true;

    /**
     * Constructor.
     *
     * @param name      The name of the leaderboard.
     * @param arenaIds  The ID of arenas in the leaderboards scope.
     * @param dataNode  The leaderboards data node.
     */
    public Leaderboard(String name, Collection<UUID> arenaIds, IDataNode dataNode) {
        _name = name;
        _arenaIds = new ArrayList<>(arenaIds);
        _dataNode = dataNode;
        _columns = new ArrayList<StatisticsColumn>(10);
        _columnsNode = dataNode.getNode("columns");

        _isLoaded = load();
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public boolean isLoaded() {
        return _isLoaded;
    }

    /**
     * Determine if the leader board is enabled.
     */
    public boolean isEnabled() {
        return _isEnabled && _isLoaded;
    }

    /**
     * Set the leader boards enabled state.
     *
     * @param isEnabled  True to enable, otherwise false.
     */
    public void setEnabled(boolean isEnabled) {
        _isEnabled = isEnabled;
    }

    /**
     * Get the leaderboards data node.
     */
    public IDataNode getDataNode() {
        return _dataNode;
    }

    /**
     * Get ID's of arenas in the leaderboards scope.
     */
    public List<UUID> getArenaIds() {
        return new ArrayList<>(_arenaIds);
    }

    /**
     * Get the number of data display signs vertically available to
     * the leaderboard.
     */
    public int getColumnHeight() {
        return _anchorColumn != null ? _anchorColumn.getColumnHeight() : 0;
    }

    /**
     * Get all columns in the leader board excluding the anchor column.
     */
    public List<StatisticsColumn> getStatisticsColumns() {
        return new ArrayList<>(_columns);
    }

    /**
     * Get the total number of lines in the leaderboard, excluding lines
     * in the header signs.
     */
    public int getLineHeight() {
        return _anchorColumn.getTotalLines();
    }

    /**
     * Get the anchor sign.
     */
    public Sign getAnchorSign() {
        return _anchorSign;
    }

    /**
     * Get the leftmost column which includes the anchor sign.
     */
    public AnchorColumn getAnchorColumn() {
        return _anchorColumn;
    }

    /**
     * Set the text format prepended to a specific line in the leaderboard signs.
     *
     * @param index       The sign line (0-3)
     * @param lineFormat  The format to prepend.
     */
    public void setLineFormat(int index, @Nullable String lineFormat) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, 4);

        if (lineFormat == null)
            lineFormat = "";

        _lineFormats[index] = lineFormat;

        _dataNode.set("format-line-" + (index + 1), lineFormat);
        _dataNode.save();

        update();
    }

    /**
     * Update players displayed in the leaderboard.
     */
    public void update() {

        if (!_isLoaded) {
            Msg.debug("Leaderboard update called on leaderboard '{0}' but " +
                    "the leaderboard isn't loaded.", getName());
            return;
        }

        if (!isEnabled()) {
            Msg.debug("Leaderboard update called on leaderboard '{0}' but " +
                    "the leaderboard isn't enabled.", getName());
            return;
        }

        IStatsFilter filter = PVStarAPI.getStatsManager().createFilter();

        for (UUID arenaId : _arenaIds) {
            filter.addArena(arenaId);
        }

        for (StatisticsColumn column : _columns) {
            filter.addStat(column.getStatType(), column.getSettings().getTrackType());
        }

        filter.filter(0, getLineHeight())
                .onSuccess(new FutureResultSubscriber<List<IPlayerStats>>() {
                    @Override
                    public void on(Result<List<IPlayerStats>> result) {
                        for (StatisticsColumn column : _columns) {
                            column.update(result.getResult());
                        }

                        _anchorColumn.update(result.getResult());
                    }
                });
    }

    /**
     * Get blocks the leaderboard signs are attached to.
     */
    public List<Block> getAttachedBlocks() {

        List<Block> blocks = new ArrayList<Block>((1 + _columns.size()) * getColumnHeight());

        if (_anchorColumn == null)
            return blocks;

        blocks.addAll(_anchorColumn.getAttachedBlocks());

        for (StatisticsColumn column : _columns) {
            blocks.addAll(column.getAttachedBlocks());
        }

        return blocks;
    }

    /**
     * Set the arenas the leaderboard compiles statistics from.
     *
     * @param arenaIds A collection of arena id's
     */
    public void setArenas(Collection<UUID> arenaIds) {
        _arenaIds = new ArrayList<>(arenaIds);

        _dataNode.set("arenas", TextUtils.concat(_arenaIds, ", "));
        _dataNode.save();

        // reload leader board with new scope
        _isLoaded = load();

        // update display
        update();
    }

    /**
     * Set anchor sign. loads initial settings from signs.
     *
     * @param anchorSign
     */
    public void setAnchor(Sign anchorSign) {

        LeaderboardsModule.getModule().unregisterBlocks(this);

        _anchorSign = anchorSign;
        _anchorLocation = anchorSign.getLocation();
        _anchorColumn = new AnchorColumn(this, anchorSign, getLineFormats());

        // get column headers to the right of the anchor
        BlockFace direction = getSignSearchDirection(anchorSign);
        List<Sign> headers = SignUtils.getAllAdjacent(anchorSign.getBlock(), direction);

        _columnsNode.clear();
        _columns.clear();

        _dataNode.set("scope", TextUtils.concat(_arenaIds, ", "));
        _dataNode.set("world", anchorSign.getWorld().getName());
        _dataNode.set("anchor", anchorSign.getLocation());

        _dataNode.set("format-line-1",
                TextFormat.getEndFormat(TextFormat.translateFormatChars(anchorSign.getLine(0))).toString());

        _dataNode.set("format-line-2",
                TextFormat.getEndFormat(TextFormat.translateFormatChars(anchorSign.getLine(1))).toString());

        _dataNode.set("format-line-3",
                TextFormat.getEndFormat(TextFormat.translateFormatChars(anchorSign.getLine(2))).toString());

        _dataNode.set("format-line-4",
                TextFormat.getEndFormat(TextFormat.translateFormatChars(anchorSign.getLine(3))).toString());

        // iterate column headers
        for (int i = 0, priority = 0; i < headers.size(); i++, priority++) {

            Sign sign = headers.get(i);
            String statName = TextFormat.remove(sign.getLine(0)).trim();
            StatTrackType trackingType = getTrackingTypeFromSign(sign);

            if (statName.isEmpty())
                break;

            StatType type = PVStarAPI.getStatsManager().getType(statName);
            if (type == null) {
                Msg.warning("Skipped loading leaderboard column for stat type '{0}' " +
                        "because that stat type was not found.");
                continue;
            }

            ColumnStatType columnStat = new ColumnStatType(priority, type, trackingType);

            IDataNode columnNode = _columnsNode.getNode(getColumnNodeName(columnStat));

            ColumnSettings settings = new ColumnSettings(columnNode, columnStat);
            StatisticsColumn column = new StatisticsColumn(this, sign, settings);
            _columns.add(column);
        }

        _columnsNode.save();
        _isLoaded = true;

        LeaderboardsModule.getModule().unregisterBlocks(this);
        LeaderboardsModule.getModule().registerBlocks(this);
    }

    private boolean load() {

        _anchorLocation = _dataNode.getLocation("anchor");

        if (!loadAnchorSign(_anchorLocation))
            return false;

        List<Sign> headers = SignUtils.getAllAdjacent(
                _anchorLocation.getBlock(), getSignSearchDirection(_anchorSign));

        if (headers.size() < _columnsNode.size()) {
            Msg.warning("Failed to load columns for leaderboard '{0}' because " +
                    "there were not enough column signs.", getName());
            return false;
        }

        int totalColumns = headers.size();

        // setup column stats list
        _columns = new ArrayList<>(totalColumns);
        for (IDataNode columnNode : _columnsNode) {

            ColumnSettings settings = new ColumnSettings(columnNode);
            StatisticsColumn column = new StatisticsColumn(this, headers.get(settings.getPriority()), settings);

            _columns.add(column);
        }

        // make sure stats are in the correct order
        Collections.sort(_columns);

        LeaderboardsModule.getModule().registerBlocks(this);

        return true;
    }

    private boolean loadAnchorSign(Location signLocation) {
        if (signLocation == null)
            return false;

        BlockState state = signLocation.getBlock().getState();

        if (!(state instanceof Sign)) {
            return false;
        }

        _anchorSign = (Sign) state;
        _anchorLocation = signLocation;
        _anchorColumn = new AnchorColumn(this, (Sign) state, getLineFormats());
        return true;
    }

    /*
     * Get sign line formatting from settings
     */
    private String[] getLineFormats() {
        if (_lineFormats == null) {
            _lineFormats = new String[]{
                    _dataNode.getString("format-line-1", ""),
                    _dataNode.getString("format-line-2", ""),
                    _dataNode.getString("format-line-3", ""),
                    _dataNode.getString("format-line-4", "")
            };
        }
        return _lineFormats;
    }

    private String getColumnNodeName(ColumnStatType columnStat) {
        return columnStat.getStatType().getName() + '-' + columnStat.getTrackType().name();
    }

    private StatTrackType getTrackingTypeFromSign(Sign sign) {
        switch (sign.getLine(1).toLowerCase().trim()) {
            case "min":
            case "minimum":
                return StatTrackType.MIN;

            case "max":
            case "maximum":
                return StatTrackType.MAX;

            default:
                return StatTrackType.TOTAL;
        }
    }

    private BlockFace getSignSearchDirection(Sign anchorSign) {
        switch (SignUtils.getFacing(anchorSign)) {
            case NORTH:
                return BlockFace.WEST;

            case EAST:
                return BlockFace.NORTH;

            case SOUTH:
                return BlockFace.EAST;

            case WEST:
                return BlockFace.SOUTH;

            case EAST_NORTH_EAST:
                return BlockFace.NORTH_NORTH_WEST;

            case EAST_SOUTH_EAST:
                return BlockFace.NORTH_NORTH_EAST;

            case NORTH_EAST:
                return BlockFace.NORTH_WEST;

            case NORTH_NORTH_EAST:
                return BlockFace.WEST_NORTH_WEST;

            case NORTH_NORTH_WEST:
                return BlockFace.WEST_SOUTH_WEST;

            case NORTH_WEST:
                return BlockFace.SOUTH_WEST;

            case SOUTH_EAST:
                return BlockFace.NORTH_EAST;

            case SOUTH_SOUTH_EAST:
                return BlockFace.EAST_NORTH_EAST;

            case SOUTH_SOUTH_WEST:
                return BlockFace.EAST_SOUTH_EAST;

            case SOUTH_WEST:
                return BlockFace.SOUTH_EAST;

            case WEST_NORTH_WEST:
                return BlockFace.SOUTH_SOUTH_WEST;

            case WEST_SOUTH_WEST:
                return BlockFace.SOUTH_SOUTH_EAST;

            default:
                return BlockFace.SOUTH;
        }
    }
}
