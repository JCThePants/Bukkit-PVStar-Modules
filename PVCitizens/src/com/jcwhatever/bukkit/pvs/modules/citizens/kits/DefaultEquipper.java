package com.jcwhatever.bukkit.pvs.modules.citizens.kits;

import com.jcwhatever.bukkit.generic.extended.ArmorType;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.ScriptNPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

/**
 * Default equipper when an equipper is not registered
 * for an entity type.
 */
public class DefaultEquipper extends Equipper {

    @Override
    public boolean equip(ScriptNPC npc, ItemStack item) {
        PreCon.notNull(npc);
        PreCon.notNull(item);

        if (!npc.isSpawned())
            return false;

        Equipment equipment = npc.getTrait(Equipment.class);
        if (equipment == null) {
            Msg.debug("Failed to get Equipment trait while attempting to equip NPC.");
            return false;
        }

        if (npc.getEntity() instanceof HumanEntity) {

            ArmorType armorType = ArmorType.getType(item);

            equipment.set(armorType.getArmorSlot() + 1, item);
        }
        else {
            equipment.set(0, item);
        }

        return false;
    }
}
