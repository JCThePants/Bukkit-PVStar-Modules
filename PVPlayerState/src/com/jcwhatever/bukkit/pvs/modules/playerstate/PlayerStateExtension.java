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


package com.jcwhatever.bukkit.pvs.modules.playerstate;

import com.jcwhatever.nucleus.events.manager.NucleusEventHandler;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.utils.player.PlayerState;
import com.jcwhatever.nucleus.utils.player.PlayerUtils;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtension;
import com.jcwhatever.bukkit.pvs.api.arena.extensions.ArenaExtensionInfo;
import com.jcwhatever.bukkit.pvs.api.arena.options.AddPlayerReason;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerAddedEvent;

import org.bukkit.plugin.Plugin;

@ArenaExtensionInfo(
        name="PVPlayerState",
        description="Adds player state save and restore to an arena.")
public class PlayerStateExtension extends ArenaExtension implements IEventListener {

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

    @NucleusEventHandler
    private void onPlayerAdded(PlayerAddedEvent event) {

        if (event.getReason() == AddPlayerReason.ARENA_RELATION_CHANGE ||
                event.getReason() == AddPlayerReason.FORWARDING) {
            return;
        }

        // store player state
        PlayerState state = PlayerState.get(PVStarAPI.getPlugin(), event.getPlayer().getPlayer());
        if (state == null) {
            state = PlayerState.store(PVStarAPI.getPlugin(), event.getPlayer().getPlayer());
        }

        if (state != null) {
            // clear players chest and reset state
            PlayerUtils.resetPlayer(event.getPlayer().getPlayer());
        }
    }

}
