package com.jcwhatever.bukkit.pvs.modules.citizens.scripts;

import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.pvs.modules.citizens.kits.Equipper;
import org.bukkit.inventory.ItemStack;

/**
 * Wraps a {@code Kit} and provides script with NPC kit
 * utilities.
 */
public class ScriptKit {

    private final Kit _kit;

    /**
     * Constructor.
     *
     * @param kit  The kit to wrap.
     */
    public ScriptKit(Kit kit) {
        PreCon.notNull(kit);

        _kit = kit;
    }

    /**
     * Apply the kit to the specified NPC.
     *
     * @param npc  The NPC to apply the kit to.
     */
    public void apply(ScriptNPC npc) {
        PreCon.notNull(npc);

        if (!npc.isSpawned())
            return;

        Equipper equipper = Equipper.getEquipper(npc.getEntityType());

        for (ItemStack armor : _kit.getArmor()) {
            equipper.equip(npc, armor);
        }

        for (ItemStack item : _kit.getItems()) {
            equipper.equip(npc, item);
        }
    }
}
