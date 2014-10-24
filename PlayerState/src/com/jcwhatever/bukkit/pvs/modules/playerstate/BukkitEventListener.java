package com.jcwhatever.bukkit.pvs.modules.playerstate;

import com.jcwhatever.bukkit.generic.player.PlayerState;
import com.jcwhatever.bukkit.generic.player.PlayerState.RestoreChecks;
import com.jcwhatever.bukkit.generic.player.PlayerState.RestoreLocation;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.io.IOException;

public class BukkitEventListener implements Listener {

    /**
     * Restore player on join.
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoin(PlayerJoinEvent event) {

        PlayerState state = PlayerState.get(PVStarAPI.getPlugin(), event.getPlayer());
        if (state != null) {

            try {
                state.restore(RestoreLocation.TRUE);

            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }


    /*
        Handle arena respawning
     */
    @EventHandler(priority=EventPriority.LOWEST)
    private void onPlayerRespawn(PlayerRespawnEvent event) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getPlayer());
        Arena arena = player.getArena();

        // restore player state, if any
        if (arena == null) {
            PlayerState state = PlayerState.get(PVStarAPI.getPlugin(), event.getPlayer());
            if (state == null)
                return; // finished

            Location respawnLocation = null;

            try {
                respawnLocation = state.restore(RestoreLocation.FALSE, RestoreChecks.FORCE_RESTORE);
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }

            if (respawnLocation != null) {
                event.setRespawnLocation(respawnLocation);
            }
        }
    }

}
