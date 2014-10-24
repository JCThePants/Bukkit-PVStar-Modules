package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.KillMobPointsType.KillMobPointsHandler;


public class KillMobPointsType extends AbstractPointsType<KillMobPointsHandler> {


    @Override
    public String getName() {
        return "KillMobs";
    }

    @Override
    public String getDescription() {
        return "Give points for killing a mob.";
    }

    @Override
    protected KillMobPointsHandler onGetNewHandler(Arena arena, IDataNode node) {
        return new KillMobPointsHandler(arena, this, node);
    }


    public static class KillMobPointsHandler extends AbstractPointsHandler implements GenericsEventListener {

        KillMobPointsHandler(Arena arena, PointsType type, IDataNode node) {
            super(arena, type, node);
        }

        @GenericsEventHandler
        private void onMobKill(PlayerArenaKillEvent event) {

            if (event.getDeadPlayer() != null)
                return;

            event.getPlayer().incrementPoints(getPoints());
        }

    }

}
