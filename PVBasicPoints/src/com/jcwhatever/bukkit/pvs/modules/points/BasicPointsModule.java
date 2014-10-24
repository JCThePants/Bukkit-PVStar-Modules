package com.jcwhatever.bukkit.pvs.modules.points;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.KillMobPointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.KillPlayerPointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.PlayerDeathPointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.TeamKillPointsType;

public class BasicPointsModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        PVStarAPI.getPointsManager().registerType(new KillMobPointsType());
        PVStarAPI.getPointsManager().registerType(new KillPlayerPointsType());
        PVStarAPI.getPointsManager().registerType(new PlayerDeathPointsType());
        PVStarAPI.getPointsManager().registerType(new TeamKillPointsType());
    }

    @Override
    protected void onEnable() {
        // do nothing
    }

}
