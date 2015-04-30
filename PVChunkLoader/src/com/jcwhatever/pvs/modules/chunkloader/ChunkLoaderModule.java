package com.jcwhatever.pvs.modules.chunkloader;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.modules.PVStarModule;

import org.bukkit.Bukkit;

/**
 * Chunk loader module.
 */
public class ChunkLoaderModule extends PVStarModule {

    @Override
    protected void onRegisterTypes() {
        PVStarAPI.getExtensionManager().registerType(ChunkLoaderExtension.class);
    }

    @Override
    protected void onEnable() {
        Bukkit.getPluginManager().registerEvents(new BukkitListener(), Nucleus.getPlugin());
    }
}
