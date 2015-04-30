package com.jcwhatever.pvs.modules.chunkloader;

import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;

@ArenaExtensionInfo(
        name="PVChunkLoader",
        description = "Prevents arena region chunks from unloading while the arena is running."
)
public class ChunkLoaderExtension extends ArenaExtension {

    @Override
    protected void onEnable() {
        // do nothing
    }

    @Override
    protected void onDisable() {
        // do nothing
    }
}
