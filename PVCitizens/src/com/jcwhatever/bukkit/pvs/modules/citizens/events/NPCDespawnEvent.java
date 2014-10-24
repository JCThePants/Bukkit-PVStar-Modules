package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.events.Cancellable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;

@Cancellable
public class NPCDespawnEvent extends AbstractNPCEvent {

    public NPCDespawnEvent(Arena arena, ScriptNPC scriptNPC) {
        super(arena, scriptNPC, true);
    }
}
