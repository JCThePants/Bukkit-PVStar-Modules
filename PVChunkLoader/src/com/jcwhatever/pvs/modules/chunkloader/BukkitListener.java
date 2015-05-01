package com.jcwhatever.pvs.modules.chunkloader;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.pvs.api.arena.ArenaRegion;
import com.jcwhatever.pvs.api.arena.IArena;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.List;

/**
 * Bukkit event listener
 */
public class BukkitListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onChunkUnload(ChunkUnloadEvent event) {

        List<ArenaRegion> regions = Nucleus.getRegionManager()
                .getRegionsInChunk(event.getChunk(), ArenaRegion.class);
        if (regions.size() == 0)
            return;

        for (ArenaRegion region : regions) {

            IArena arena = region.getArena();
            if (!arena.getGame().isRunning())
                continue;

            if (arena.getExtensions().has(ChunkLoaderExtension.class)) {
                event.setCancelled(true);
                break;
            }
        }
    }
}
