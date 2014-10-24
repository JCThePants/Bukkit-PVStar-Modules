package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaDeathEvent;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.PlayerDeathPointsType.PlayerDeathPointsHandler;

public class PlayerDeathPointsType extends AbstractPointsType<PlayerDeathPointsHandler> {

    @Override
    public String getName() {
        return "PlayerDeath";
    }

    @Override
    public String getDescription() {
        return "Receive points when killed in an arena.";
    }

    @Override
    protected PlayerDeathPointsHandler onGetNewHandler(Arena arena, IDataNode node) {
        return new PlayerDeathPointsHandler(arena, this, node);
    }


    public static class PlayerDeathPointsHandler extends AbstractPointsHandler implements GenericsEventListener {

        PlayerDeathPointsHandler(Arena arena, PointsType type, IDataNode node) {
            super(arena, type, node);
        }

        @GenericsEventHandler
        private void onPlayerDeath(PlayerArenaDeathEvent event) {

            event.getPlayer().incrementPoints(getPoints());
        }

    }

}
