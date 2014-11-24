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

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import net.citizensnpcs.api.trait.Trait;

/**
 * Trait that prevents the item in an NPC's hand from being
 * damaged when the the NPC attacks another entity.
 */
public class UnbreakableWeaponsTrait extends Trait {

    protected UnbreakableWeaponsTrait() {
        super("UnbreakableWeapons");
    }

    @EventHandler
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        Entity entityDamager = event.getDamager();

        LivingEntity damager;

        if (entityDamager instanceof Projectile) {
            ProjectileSource source = ((Projectile) entityDamager).getShooter();
            if (source instanceof LivingEntity) {
                damager = (LivingEntity)source;
            }
            else {
                return;
            }
        }
        else if (entityDamager instanceof  LivingEntity) {
            damager = (LivingEntity)entityDamager;
        }
        else {
            return;
        }

        if (!damager.hasMetadata("NPC"))
            return;

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(damager);
        if (registeredNPC == null)
            return;

        if (!registeredNPC.getNPC().equals(getNPC()))
            return;

        ItemStackHelper.repair(damager.getEquipment().getItemInHand());
    }
}
