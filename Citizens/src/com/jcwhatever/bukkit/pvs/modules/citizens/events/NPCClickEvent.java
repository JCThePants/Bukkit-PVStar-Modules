package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;

public class NPCClickEvent extends AbstractNPCEvent {

    private final ArenaPlayer _clicker;

    public NPCClickEvent(Arena arena, ScriptNPC scriptNPC, ArenaPlayer clicker) {
        super(arena, scriptNPC, false);

        PreCon.notNull(clicker);

        _clicker = clicker;
    }

    public ArenaPlayer getClicker() {
        return _clicker;
    }
}
