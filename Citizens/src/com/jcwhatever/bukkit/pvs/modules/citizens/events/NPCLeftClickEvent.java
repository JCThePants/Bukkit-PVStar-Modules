package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;

public class NPCLeftClickEvent extends NPCClickEvent {

    public NPCLeftClickEvent(Arena arena, ScriptNPC scriptNPC, ArenaPlayer clicker) {
        super(arena, scriptNPC, clicker);
    }
}
