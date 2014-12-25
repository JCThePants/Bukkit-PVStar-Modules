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

import com.jcwhatever.generic.utils.SignUtils;
import com.jcwhatever.generic.storage.IDataNode;
import com.jcwhatever.generic.utils.PreCon;
import com.jcwhatever.generic.utils.text.TextUtils;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.stats.ArenaStats;
import com.jcwhatever.bukkit.pvs.api.stats.StatTracking.StatTrackType;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.LeaderboardsModule;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.AnchorColumn;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.ColumnSetting;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards.columns.StatisticsColumn;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Leaderboard {

    private final String _name;

    private List<UUID> _arenaIds;
    private Set<ArenaStats> _arenaStats;

    private Location _anchorLocation;
    private Sign _anchorSign;

    private String[] _lineFormats; // add coloring to lines

    private AnchorColumn _anchorColumn;
    private List<StatisticsColumn> _columns;

    // Store columns by statistic name
    // keyed to column stat name
    private Map<String, StatisticsColumn> _mappedColumns;

    // List of ordered columns statistics types
    private List<StatType> _columnStatTypes;

    private IDataNode _dataNode;
    private IDataNode _columnsNode;

    private boolean _isLoaded = false;
    private boolean _isEnabled = true;

    PlayerSorter _sorter;

    public Leaderboard(String name, Collection<UUID> arenaIds, IDataNode dataNode) {
        _name = name;
        _arenaIds = new ArrayList<>(arenaIds);
        _dataNode = dataNode;
        _columns = new ArrayList<StatisticsColumn>(10);
        _mappedColumns = new HashMap<>(10);
        _columnsNode = dataNode.getNode("columns");

        _isLoaded = load();
    }

    public IDataNode getDataNode() {
        return _dataNode;
    }

    public String getName() {
        return _name;
    }

    public List<UUID> getArenaIds() {
        return new ArrayList<>(_arenaIds);
    }

    public List<ArenaStats> getArenaStats() {
        return new ArrayList<>(_arenaStats);
    }

    public int getColumnHeight() {
        return _anchorColumn != null ? _anchorColumn.getColumnHeight() : 0;
    }

    public StatisticsColumn getColumn(String statName) {
        return _mappedColumns.get(statName);
    }

    public List<StatisticsColumn> getStatisticsColumns() {
        return new ArrayList<>(_columns);
    }

    public int getLineHeight() {
        return _anchorColumn.getTotalLines();
    }

    public Location getAnchorLocation() {
        return _anchorLocation;
    }

    public Sign getAnchorSign() {
        return _anchorSign;
    }

    public AnchorColumn getAnchorColumn() {
        return _anchorColumn;
    }

    public boolean isLoaded() {
        return _isLoaded;
    }

    public boolean isEnabled() {
        return _isEnabled && _isLoaded;
    }

    public void setEnabled(boolean isEnabled) {
        _isEnabled = isEnabled;
    }

    public void setLineFormat(int index, @Nullable String lineFormat) {
        PreCon.positiveNumber(index);
        PreCon.lessThan(index, 5);

        if (lineFormat == null)
            lineFormat = "";

        _lineFormats[index] = lineFormat;

        _dataNode.set("format-line-" + (index + 1), lineFormat);
        _dataNode.saveAsync(null);

        update();
    }

    public void update() {
        if (!_isLoaded) {
            Msg.debug("Leaderboard update called on leaderboard '{0}' but the leaderboard isn't loaded.", getName());
            return;
        }

        if (!isEnabled()) {
            return;
        }

        if (_sorter == null) {
            Msg.debug("Leaderboard update called on leaderboard '{0}' but PlayerSorter was null.", getName());
            return;
        }

        List<String> playerIds = _sorter.getSortedPlayerIds();

        _anchorColumn.update(playerIds);

        for (StatisticsColumn column : _columns) {
            column.update(playerIds);
        }
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
     * Get ordered list of statistic types used in the leaderboard columns.
     * @return
     */
    public List<StatType> getColumnStatTypes() {
        return new ArrayList<>(_columnStatTypes);
    }

    /**
     * Set the arenas the leaderboard compiles statistics from.
     *
     * @param arenaIds  A collection of arena id's
     */
    public void setArenas(Collection<UUID> arenaIds) {
        _arenaIds = new ArrayList<>(arenaIds);
        loadArenaStats();

        _dataNode.set("arenas", TextUtils.concat(_arenaIds, ", "));
        _dataNode.saveAsync(null);

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

        LeaderboardsModule.getModule().removeBlockLocations(this);

        _anchorSign = anchorSign;
        _anchorLocation = anchorSign.getLocation();
        _anchorColumn = new AnchorColumn(this, anchorSign, getLineFormats());

        // get column headers to the right of the anchor
        BlockFace direction = getSignSearchDirection(anchorSign);
        List<Sign> headers = SignUtils.getAdjacentSigns(anchorSign.getBlock(), direction);

        _columnStatTypes.clear();
        _columnsNode.clear();

        _dataNode.set("scope", TextUtils.concat(_arenaIds, ", "));
        _dataNode.set("world", anchorSign.getWorld().getName());
        _dataNode.set("anchor", anchorSign.getLocation());

        _dataNode.set("format-line-1", ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', anchorSign.getLine(0))));
        _dataNode.set("format-line-2", ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', anchorSign.getLine(1))));
        _dataNode.set("format-line-3", ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', anchorSign.getLine(2))));
        _dataNode.set("format-line-4", ChatColor.getLastColors(ChatColor.translateAlternateColorCodes('&', anchorSign.getLine(3))));

        // iterate column headers
        for (int i =0, order=0; i < headers.size(); i++, order++) {
            Sign sign = headers.get(i);

            String statName = sign.getLine(0);
            if (ChatColor.stripColor(statName).trim().isEmpty()) {
                break;
            }

            StatType type = PVStarAPI.getStatsManager().getType(statName);
            if (type == null) {
                Msg.warning("Skipped loading leaderboard column for stat type '{0}' because that stat type was not found.");
                continue;
            }

            SortOrder sortOrder = getSortOrderFromSign(sign);
            StatTrackType trackingType = getTrackingTypeFromSign(sign);

            IDataNode columnNode = _columnsNode.getNode(statName);

            columnNode.set("order", order);
            columnNode.set("sort-order", sortOrder);
            columnNode.set("tracking", trackingType);

            _columnStatTypes.add(type);

            addColumn(headers.get(i), type);
        }

        _sorter = new PlayerSorter(this);
        _columnsNode.saveAsync(null);
        _isLoaded = true;

        LeaderboardsModule.getModule().addBlockLocations(this);
    }



    private boolean load() {

        loadArenaStats();

        _anchorLocation = _dataNode.getLocation("anchor");

        // setup column stats list
        Set<String> statNames = _columnsNode.getSubNodeNames();
        _columnStatTypes = new ArrayList<>(statNames.size());

        for (String statName : statNames) {
            StatType type = PVStarAPI.getStatsManager().getType(statName);
            if (type == null) {
                Msg.warning("Failed to load statistic type '{0}' while loading leaderboard '{1}'.", statName, getName());

                // insert dummy stat
                _columnStatTypes.add(new MissingStatType(statName));
            }
            else {
                _columnStatTypes.add(type);
            }
        }

        // make sure stats are in the correct order
        Collections.sort(_columnStatTypes, new Comparator<StatType>() {
            public int compare(StatType s1, StatType s2) {
                int s1order = _columnsNode.getInteger(s1 + ".order");
                int s2order = _columnsNode.getInteger(s2 + ".order");

                return Integer.compare(s1order, s2order);
            }
        });

        if (!loadAnchorSign(_anchorLocation))
            return false;

        if (!loadColumns(_columnStatTypes))
            return false;

        _sorter = new PlayerSorter(this);

        LeaderboardsModule.getModule().addBlockLocations(this);

        return true;

    }


    private boolean loadAnchorSign(Location signLocation) {
        if (signLocation == null)
            return false;

        BlockState state = signLocation.getBlock().getState();

        if (!(state instanceof Sign)) {
            return false;
        }

        _anchorSign = (Sign)state;
        _anchorLocation = signLocation;
        _anchorColumn = new AnchorColumn(this, (Sign)state, getLineFormats());
        return true;
    }

    private boolean loadColumns(List<StatType> statTypes) {
        List<Sign> headers = SignUtils.getAdjacentSigns(_anchorSign.getBlock(), getSignSearchDirection(_anchorSign));

        if (headers.size() < statTypes.size()) {
            Msg.warning("Failed to load columns for leaderboard '{0}' because there were not enough column signs.", getName());
            return false;
        }

        for (int i=0; i < statTypes.size(); i++) {
            addColumn(headers.get(i), statTypes.get(i));
        }

        return true;
    }

    /**
     * Get sign line formatting from settings
     * @return
     */
    private String[] getLineFormats() {
        if (_lineFormats == null) {
            _lineFormats = new String[] {
                    _dataNode.getString("format-line-1", ""),
                    _dataNode.getString("format-line-2", ""),
                    _dataNode.getString("format-line-3", ""),
                    _dataNode.getString("format-line-4", "")
            };
        }
        return _lineFormats;
    }

    /**
     * Add column using the top most sign and the statistic property name
     * the column will track.
     *
     * @param headerSign
     * @param statType
     */
    private void addColumn(Sign headerSign, StatType statType) {

        ColumnSetting columnSettings = new ColumnSetting(_columnsNode.getNode(statType.getName()), getLineFormats());
        StatisticsColumn column = new StatisticsColumn(statType, this, headerSign, columnSettings);

        _columns.add(column);
        _mappedColumns.put(statType.getName(), column);
    }


    private SortOrder getSortOrderFromSign(Sign sign) {
        switch (sign.getLine(1).toLowerCase()) {
            case "high":
            case "highest":
            case "ascending":
            case "ascend":
            case "a":
                return SortOrder.ASCENDING;

            case "low":
            case "lowest":
            case "descending":
            case "descend":
            case "d":
                return SortOrder.DESCENDING;

            default:
                return SortOrder.NONE;
        }
    }

    private StatTrackType getTrackingTypeFromSign(Sign sign) {
        switch (sign.getLine(2).toLowerCase().trim()) {
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
        switch (SignUtils.getSignFacing(anchorSign)) {
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

    private void loadArenaStats() {

        _arenaStats = new HashSet<>(_arenaIds.size());

        for (UUID arenaId : _arenaIds) {
            ArenaStats stats = PVStarAPI.getStatsManager().getArenaStats(arenaId);
            if (stats == null)
                continue;

            _arenaStats.add(stats);
        }
    }


}
