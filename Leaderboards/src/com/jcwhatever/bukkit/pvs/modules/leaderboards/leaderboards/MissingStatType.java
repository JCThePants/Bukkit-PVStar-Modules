package com.jcwhatever.bukkit.pvs.modules.leaderboards.leaderboards;

import com.jcwhatever.bukkit.pvs.api.stats.StatTracking;
import com.jcwhatever.bukkit.pvs.api.stats.StatType;

public class MissingStatType extends StatType {

    public MissingStatType(String statName) {
        super(statName, statName + "[missing]", StatTracking.TOTAL);
    }
}
