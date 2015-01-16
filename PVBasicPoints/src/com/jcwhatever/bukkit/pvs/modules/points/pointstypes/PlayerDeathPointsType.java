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


package com.jcwhatever.bukkit.pvs.modules.points.pointstypes;

import com.jcwhatever.nucleus.events.manager.EventMethod;
import com.jcwhatever.nucleus.events.manager.IEventListener;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.PlayerDeathPointsType.PlayerDeathPointsHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathPointsType extends AbstractPointsType<PlayerDeathPointsHandler> {

    @Override
    public String getName() {
        return "PlayerDeath";
    }

    @Override
    public String getDescription() {
        return "Receive points when killed in an arena.";
    }

    @Override
    protected PlayerDeathPointsHandler onGetNewHandler(Arena arena, IDataNode node) {
        return new PlayerDeathPointsHandler(arena, this, node);
    }


    public static class PlayerDeathPointsHandler extends AbstractPointsHandler implements IEventListener {

        PlayerDeathPointsHandler(Arena arena, PointsType type, IDataNode node) {
            super(arena, type, node);
        }

        @EventMethod
        private void onPlayerDeath(PlayerDeathEvent event) {

            ArenaPlayer player = PVStarAPI.getArenaPlayer(event.getEntity());

            player.incrementPoints(getPoints());
        }

    }

}
