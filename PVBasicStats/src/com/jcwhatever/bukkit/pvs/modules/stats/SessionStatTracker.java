package com.jcwhatever.bukkit.pvs.modules.stats;

import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;

public class SessionStatTracker {

    private final ArenaPlayer _player;
    private final StatType _statType;
    private double _total;

    public SessionStatTracker(ArenaPlayer player, StatType type) {
        _player = player;
        _statType = type;
    }

    public ArenaPlayer getPlayer() {
        return _player;
    }

    public StatType getStatType() {
        return _statType;
    }

    public double getTotal() {
        return _total;
    }

    public double increment(double amount) {
        _total += amount;
        return _total;
    }

}
