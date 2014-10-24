package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.events.Cancellable;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

@Cancellable
public class NPCDamageEvent extends AbstractNPCEvent {

    private final EntityDamageEvent _parentEvent;

    public NPCDamageEvent(Arena arena, ScriptNPC scriptNPC, EntityDamageEvent parentEvent) {
        super(arena, scriptNPC, true);

        PreCon.notNull(parentEvent);

        _parentEvent = parentEvent;
    }

    public DamageCause getCause() {
        return _parentEvent.getCause();
    }

    public double getDamage() {
        return _parentEvent.getDamage();
    }

    public void setDamage(double damage) {
        _parentEvent.setDamage(damage);
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
