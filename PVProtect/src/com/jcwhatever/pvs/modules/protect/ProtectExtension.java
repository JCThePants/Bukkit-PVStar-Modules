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


package com.jcwhatever.pvs.modules.protect;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.pvs.api.arena.extensions.ArenaExtensionInfo;

import org.bukkit.entity.EntityType;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name="PVProtect",
        description="Prevent players from damaging the arena.")
public class ProtectExtension extends ArenaExtension implements IEventListener {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onEnable() {
        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {
        getArena().getEventManager().unregister(this);
    }

    @EventMethod
    private void onPlayerBlockInteract(PlayerInteractEvent event) {

        if (!event.hasBlock())
            return;

        event.setUseInteractedBlock(Result.ALLOW);
        event.setUseItemInHand(Result.DENY);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            event.setCancelled(true);
    }

    /*
     * Prevent placing blocks
     */
    @EventMethod
    private void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    /*
     * Prevent placing blocks
     */
    @EventMethod
    private void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        event.setCancelled(true);
    }

    /*
     * Prevent entities such as endermen from changing arena blocks.
     */
    @EventMethod
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {

        event.setCancelled(true);
    }

    /*
      Handle arena damage (Item Frames and Paintings)
     */
    @EventMethod
    private void onItemFrameDamage(EntityDamageEvent event) {

        if (event.getEntityType() != EntityType.ITEM_FRAME && event.getEntityType() != EntityType.PAINTING)
            return;

        event.setDamage(0.0);
        event.setCancelled(true);
    }

    /*
      Handle arena damage (Item Frames and Paintings)
     */
    @EventMethod
    private void onItemFrameBreak(HangingBreakEvent event) {

        // do not prevent physics break
        if (event.getCause() == RemoveCause.PHYSICS)
            return;

        event.setCancelled(true);
    }

    /*
      Handle arena damage (Item Frames and Paintings)
     */
    @EventMethod
    private void onPlayerInteractItemFrame(PlayerInteractEntityEvent event) {

        if (event.getRightClicked().getType() != EntityType.ITEM_FRAME)
            return;

        event.setCancelled(true);
    }
}
