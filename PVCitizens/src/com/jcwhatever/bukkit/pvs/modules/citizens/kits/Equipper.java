package com.jcwhatever.bukkit.pvs.modules.citizens.kits;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * Base class for NPC item equipper implementations.
 */
public abstract class Equipper {

    private static final Equipper DEFAULT_EQUIPPER = new DefaultEquipper();
    private static final Map<EntityType, Equipper> _equippers = new EnumMap<>(EntityType.class);


    /**
     * Register an equipper instance.
     *
     * @param type      The entity type the equipper is for.
     * @param equipper  The equipper.
     */
    public static void registerEquipper(EntityType type, Equipper equipper) {
        PreCon.notNull(type);
        PreCon.notNull(equipper);

        _equippers.put(type, equipper);
    }

    /**
     * Get an equipper for the specified entity type.
     *
     * @param type  The entity type.
     */
    public static Equipper getEquipper(EntityType type) {
        PreCon.notNull(type);

        Equipper equipper = _equippers.get(type);
        return equipper == null ? DEFAULT_EQUIPPER : equipper;
    }


    /**
     * Equip an NPC with the specified {@code ItemStack}.
     *
     * @param npc   The npc to equip.
     * @param item  The item to equip the npc with.
     */
    public abstract boolean equip(ScriptNPC npc, ItemStack item);
}
