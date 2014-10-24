package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;

public class EntityTargetNPCEvent extends AbstractNPCEvent {

    private final EntityTargetEvent _parentEvent;

    public EntityTargetNPCEvent(Arena arena, ScriptNPC scriptNPC, EntityTargetEvent parentEvent) {
        super(arena, scriptNPC, false);

        PreCon.notNull(parentEvent);

        _parentEvent = parentEvent;
    }

    public Entity getEntity() {
        return _parentEvent.getEntity();
    }

    public TargetReason getReason() {
        return _parentEvent.getReason();
    }
}
