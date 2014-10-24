package com.jcwhatever.bukkit.pvs.modules.leaderboards;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BukkitEventListener implements Listener {

    @EventHandler(priority= EventPriority.HIGHEST)
    private void onPlayerInteract(PlayerInteractEvent event) {

        if (!event.hasBlock())
            return;

        // prevent leaderboard damage
        if (LeaderboardsModule.getInstance().isLeaderboardBlock(event.getClickedBlock().getLocation())) {
            event.setCancelled(true);
        }
    }

}
