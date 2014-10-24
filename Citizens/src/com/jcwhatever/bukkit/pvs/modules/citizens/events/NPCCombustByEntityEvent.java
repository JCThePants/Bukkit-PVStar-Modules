package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityCombustByEntityEvent;

public class NPCCombustByEntityEvent extends AbstractNPCEvent {

    private final EntityCombustByEntityEvent _parentEvent;

    public NPCCombustByEntityEvent(Arena arena, ScriptNPC scriptNPC, EntityCombustByEntityEvent parentEvent) {
        super(arena, scriptNPC, false);

        PreCon.notNull(parentEvent);

        _parentEvent = parentEvent;
    }

    public Entity getCombuster() {
        return _parentEvent.getCombuster();
    }
}
