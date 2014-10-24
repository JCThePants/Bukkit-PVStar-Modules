package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;

public class NPCSpawnEvent extends AbstractNPCEvent {

    public NPCSpawnEvent(Arena arena, ScriptNPC scriptNPC) {
        super(arena, scriptNPC, true);
    }
}
