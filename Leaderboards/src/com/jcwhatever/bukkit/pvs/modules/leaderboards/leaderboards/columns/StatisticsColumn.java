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
