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

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.EntityTargetNPCEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCCombustByBlockEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCCombustByEntityEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCCombustEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCDamageEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCDeathEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCLeftClickEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.events.NPCRightClickEvent;
import com.jcwhatever.bukkit.pvs.modules.citizens.scripts.NPCEntityRegistry.RegisteredNPC;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class BukkitEventListener implements Listener {

    @EventHandler
    private void onEntityTargetNPC(EntityTargetEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getTarget());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        EntityTargetNPCEvent npcEvent = new EntityTargetNPCEvent(
                arena, registeredNPC.getScriptNPC(), event);

        arena.getEventManager().call(npcEvent);
    }


    @EventHandler
    private void onNPCClick(PlayerInteractEntityEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getRightClicked());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        NPCRightClickEvent npcEvent = new NPCRightClickEvent(
                arena, registeredNPC.getScriptNPC(), PVStarAPI.getArenaPlayer(event.getPlayer()));

        registeredNPC.getScriptNPC().getEventManager().call(npcEvent);
    }

    private void onNPCCollision() {

    }

    @EventHandler
    private void onNPCCombustByBlock(EntityCombustByBlockEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getEntity());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        NPCCombustByBlockEvent npcEvent = new NPCCombustByBlockEvent(
                arena, registeredNPC.getScriptNPC(), event);

        registeredNPC.getScriptNPC().getEventManager().call(npcEvent);
    }

    @EventHandler
    private void onNPCCombustByEntity(EntityCombustByEntityEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getEntity());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        NPCCombustByEntityEvent npcEvent = new NPCCombustByEntityEvent(
                arena, registeredNPC.getScriptNPC(), event);

        registeredNPC.getScriptNPC().getEventManager().call(npcEvent);
    }

    @EventHandler
    private void onNPCCombust(EntityCombustEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getEntity());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        NPCCombustEvent npcEvent = new NPCCombustEvent(arena, registeredNPC.getScriptNPC(), event);

        registeredNPC.getScriptNPC().getEventManager().call(npcEvent);
    }

    @EventHandler
    private void onNPCDamage(EntityDamageEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getEntity());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        event.setCancelled(!registeredNPC.getNPC().data().get(NPC.DAMAGE_OTHERS_METADATA, true));

        if (!event.isCancelled()) {
            NPCDamageEvent npcEvent = new NPCDamageEvent(arena, registeredNPC.getScriptNPC(), event);

            registeredNPC.getScriptNPC().getEventManager().call(npcEvent);
        }


        if (event instanceof EntityDamageByEntityEvent) {

            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent)event;

            if (entityEvent.getDamager() instanceof Player) {

                ArenaPlayer clicker = PVStarAPI.getArenaPlayer(entityEvent.getDamager());

                if (clicker != null) {

                    NPCLeftClickEvent npcLeftClickEvent = new NPCLeftClickEvent(arena, registeredNPC.getScriptNPC(), clicker);
                    registeredNPC.getScriptNPC().getEventManager().call(npcLeftClickEvent);
                }
            }
        }

    }

    @EventHandler
    private void onNPCDeath(EntityDeathEvent event) {

        RegisteredNPC registeredNPC = NPCEntityRegistry.getNPCInfo(event.getEntity());
        if (registeredNPC == null)
            return;

        Arena arena = registeredNPC.getArena();

        NPCDeathEvent npcEvent = new NPCDeathEvent(arena, registeredNPC.getScriptNPC(), event);

        registeredNPC.getScriptNPC().getEventManager().call(npcEvent);
    }


}
