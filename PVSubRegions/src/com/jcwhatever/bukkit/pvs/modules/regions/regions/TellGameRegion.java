package com.jcwhatever.bukkit.pvs.modules.regions.regions;

import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionTypeInfo;

@RegionTypeInfo(
        name="tellgame",
        description="Tell all game players in an arena a message when one of the players enters or leaves the region.")
public class TellGameRegion extends TellRegion {

    @Override
    protected void tellMessage(ArenaPlayer player, String message) {

        getArena().getGameManager().tell(message);
    }
}
