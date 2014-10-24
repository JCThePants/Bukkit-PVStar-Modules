package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.AbstractArenaEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;

public class AbstractNPCEvent extends AbstractArenaEvent {

    private final ScriptNPC _scriptNPC;

    public AbstractNPCEvent(Arena arena, ScriptNPC scriptNPC, boolean isCancellable) {
        super(arena, isCancellable);

        PreCon.notNull(scriptNPC);

        _scriptNPC = scriptNPC;
    }

    public ScriptNPC getNPC() {
        return _scriptNPC;
    }


}
