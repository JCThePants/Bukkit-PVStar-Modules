/* This file is part of PV-Star Modules: PVCitizens for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


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
