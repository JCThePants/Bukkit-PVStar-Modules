package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.KillPlayerPointsType.KillPlayerPointsHandler;

public class KillPlayerPointsType extends AbstractPointsType<KillPlayerPointsHandler> {

    @Override
    public String getName() {
        return "KillPlayer";
    }

    @Override
    public String getDescription() {
        return "Give points for killing another player.";
    }

    @Override
    protected KillPlayerPointsHandler onGetNewHandler(Arena arena, IDataNode node) {
        return new KillPlayerPointsHandler(arena, this, node);
    }

    public static class KillPlayerPointsHandler extends AbstractPointsHandler implements GenericsEventListener {

        KillPlayerPointsHandler(Arena arena, PointsType type, IDataNode node) {
            super(arena, type, node);
        }

        @GenericsEventHandler
        private void onPlayerKill(PlayerArenaKillEvent event) {

            if (event.getDeadPlayer() == null)
                return;

            event.getPlayer().incrementPoints(getPoints());
        }

    }

}
