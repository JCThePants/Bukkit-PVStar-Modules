package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityCombustByBlockEvent;


public class NPCCombustByBlockEvent extends AbstractNPCEvent {

    private final EntityCombustByBlockEvent _parentEvent;

    public NPCCombustByBlockEvent(Arena arena, ScriptNPC scriptNPC, EntityCombustByBlockEvent parentEvent) {
        super(arena, scriptNPC, false);

        PreCon.notNull(parentEvent);

        _parentEvent = parentEvent;
    }

    public Block getCombuster() {
        return _parentEvent.getCombuster();
    }
}
