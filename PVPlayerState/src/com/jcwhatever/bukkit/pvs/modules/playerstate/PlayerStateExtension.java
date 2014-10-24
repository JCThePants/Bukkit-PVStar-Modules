package com.jcwhatever.bukkit.pvs.modules.playerstate;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.player.PlayerState;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;

@ArenaExtensionInfo(
        name="PVPlayerState",
        description="Adds player state save and restore to an arena.")
public class PlayerStateExtension extends ArenaExtension implements GenericsEventListener{

    @Override
    protected void onEnable() {

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onPlayerAdded(PlayerAddedEvent event) {

        if (event.getReason() == AddPlayerReason.ARENA_RELATION_CHANGE ||
                event.getReason() == AddPlayerReason.FORWARDING) {
            return;
        }

        // store player state
        PlayerState state = PlayerState.get(PVStarAPI.getPlugin(), event.getPlayer().getHandle());
        if (state == null) {
            PlayerState.store(PVStarAPI.getPlugin(), event.getPlayer().getHandle());
        }
    }

}
