package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.events.Cancellable;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.event.entity.EntityCombustEvent;

@Cancellable
public class NPCCombustEvent extends AbstractNPCEvent {

    private final EntityCombustEvent _parentEvent;

    public NPCCombustEvent(Arena arena, ScriptNPC scriptNPC, EntityCombustEvent parentEvent) {
        super(arena, scriptNPC, true);

        PreCon.notNull(parentEvent);

        _parentEvent = parentEvent;
    }

    public int getDuration() {
        return _parentEvent.getDuration();
    }

    public void setDuration(int duration) {
        _parentEvent.setDuration(duration);
    }

    @Override
    public boolean isCancelled() {
        return _parentEvent.isCancelled();
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        _parentEvent.setCancelled(isCancelled());
    }
}
