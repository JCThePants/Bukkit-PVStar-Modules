package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaTeam;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.TeamKillPointsType.TeamKillPointsHandler;

public class TeamKillPointsType extends AbstractPointsType<TeamKillPointsHandler> {

    @Override
    public String getName() {
        return "TeamKill";
    }

    @Override
    public String getDescription() {
        return "Receive points for team kills in an arena.";
    }

    @Override
    protected TeamKillPointsHandler onGetNewHandler(Arena arena, IDataNode node) {
        return new TeamKillPointsHandler(arena, this, node);
    }

    public static class TeamKillPointsHandler extends AbstractPointsHandler {

        TeamKillPointsHandler(Arena arena, PointsType type, IDataNode node) {
            super(arena, type, node);
        }

        @GenericsEventHandler
        private void onPlayerKill(PlayerArenaKillEvent event) {

            if (event.getDeadPlayer() == null)
                return;

            if (event.getPlayer().getTeam() == event.getDeadPlayer().getTeam() &&
                    event.getPlayer().getTeam() != ArenaTeam.NONE) {

                event.getPlayer().incrementPoints(getPoints());
            }
        }

    }
}
