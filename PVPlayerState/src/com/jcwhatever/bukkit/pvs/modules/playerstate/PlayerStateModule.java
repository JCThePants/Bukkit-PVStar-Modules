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

import com.jcwhatever.bukkit.generic.events.manager.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.manager.IGenericsEventListener;
import com.jcwhatever.bukkit.generic.player.PlayerState;
import com.jcwhatever.bukkit.generic.player.PlayerState.RestoreLocation;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerLeaveEvent;
import com.jcwhatever.bukkit.pvs.api.modules.PVStarModule;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;

public class PlayerStateModule extends PVStarModule implements IGenericsEventListener {

    @Override
    protected void onRegisterTypes() {

        PVStarAPI.getExtensionManager().registerType(PlayerStateExtension.class);
    }

    @Override
    protected void onEnable() {

        PVStarAPI.getEventManager().register(this);
        Bukkit.getPluginManager().registerEvents(new BukkitEventListener(), PVStarAPI.getPlugin());
    }

    @GenericsEventHandler
    private void onPlayerLeave(PlayerLeaveEvent event) {

        if (!event.isRestoring() || event.getPlayer().getHandle().isDead())
            return;

        PlayerState state = PlayerState.get(PVStarAPI.getPlugin(), event.getPlayer().getHandle());
        if (state == null || !state.isSaved())
            return;

        try {
            event.setRestoreLocation(state.restore(RestoreLocation.FALSE));
        }
        catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

}
