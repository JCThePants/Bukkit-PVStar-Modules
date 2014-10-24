package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

@RegionTypeInfo(
        name="tellarena",
        description="Tell all players in an arena a message when one of the players enters or leaves the region.")
public class TellArenaRegion extends TellRegion {

    @Override
    protected void tellMessage(ArenaPlayer player, String message) {

        getArena().getGameManager().tell(message);
        getArena().getLobbyManager().tell(message);
        getArena().getSpectatorManager().tell(message);
    }
}
