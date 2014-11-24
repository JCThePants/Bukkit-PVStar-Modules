/*
 * This file is part of PV-StarModules for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.pvs.modules.citizens.traits;

import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.NPCEntityRegistry;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.NPCEntityRegistry.RegisteredNPC;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import net.citizensnpcs.api.trait.Trait;

/*
 * 
 */
public class UnbreakableArmorTrait extends Trait {

    protected UnbreakableArmorTrait() {
        super("UnbreakableArmor");
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        if (!event.getEntity().hasMetadata("NPC"))
            return;

        if (!(event.getEntity() instanceof LivingEntity))
            return;

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getEntity());
        if (registeredNPC == null)
            return;

        if (!registeredNPC.getNPC().equals(getNPC()))
            return;

        ItemStackHelper.repair(((LivingEntity)event.getEntity()).getEquipment().getArmorContents());
    }
}
