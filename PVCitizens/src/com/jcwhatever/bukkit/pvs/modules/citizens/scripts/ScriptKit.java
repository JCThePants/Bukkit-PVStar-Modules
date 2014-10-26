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
