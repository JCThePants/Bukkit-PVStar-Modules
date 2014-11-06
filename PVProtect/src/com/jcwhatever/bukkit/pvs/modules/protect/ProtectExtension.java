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


package com.jcwhatever.bukkit.pvs.modules.protect;

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.events.players.ArenaBlockDamagePreventEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;

@ArenaExtensionInfo(
        name="PVProtect",
        description="Prevent players from damaging the arena.")
public class ProtectExtension extends ArenaExtension implements GenericsEventListener {

    private static BukkitEventListener _bukkitListener;

    @Override
    protected void onEnable() {

        if (_bukkitListener == null) {
            _bukkitListener = new BukkitEventListener();
            Bukkit.getPluginManager().registerEvents(_bukkitListener, PVStarAPI.getPlugin());
        }

        getArena().getEventManager().register(this);
    }

    @Override
    protected void onDisable() {

        getArena().getEventManager().unregister(this);
    }

    @GenericsEventHandler
    private void onPlayerBlockInteract(PlayerInteractEvent event) {

        if (!event.hasBlock())
            return;

        ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getPlayer());

        // deny interaction

        // allow other modules/extensions to cancel the damage prevention
        ArenaBlockDamagePreventEvent preventEvent = new ArenaBlockDamagePreventEvent(
                getArena(), player, event);

        getArena().getEventManager().call(preventEvent);

        if (!preventEvent.isCancelled()) {
            event.setCancelled(true);
        }
    }

    /*
     * Prevent entities such as endermen from changing arena blocks.
     */
    @GenericsEventHandler
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {

        event.setCancelled(true);
    }
}
