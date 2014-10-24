package com.jcwhatever.bukkit.pvs.modules.citizens.events;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class NPCDeathEvent extends AbstractNPCEvent {

    private final EntityDeathEvent _parentEvent;

    public NPCDeathEvent(Arena arena, ScriptNPC scriptNPC, EntityDeathEvent parentEvent) {
        super(arena, scriptNPC, false);

        PreCon.notNull(parentEvent);

        _parentEvent = parentEvent;
    }

    public int getDroppedExp() {
        return _parentEvent.getDroppedExp();
    }

    public void setDroppedExp(int exp) {
        _parentEvent.setDroppedExp(exp);
    }

    public List<ItemStack> getDrops() {
        return _parentEvent.getDrops();
    }
}
