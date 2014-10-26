/* This file is part of PV-Star Modules: PVBasicPoints for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.events.GenericsEventHandler;
import com.jcwhatever.bukkit.generic.events.GenericsEventListener;
import com.jcwhatever.bukkit.generic.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.events.players.PlayerArenaKillEvent;
import com.jcwhatever.bukkit.pvs.api.points.PointsType;
import com.jcwhatever.bukkit.pvs.modules.points.pointstypes.KillPlayerPointsType.KillPlayerPointsHandler;

public class KillPlayerPointsType extends AbstractPointsType<KillPlayerPointsHandler> {

    @Override
    public String getName() {
        return "KillPlayer";
    }

    @Override
    public String getDescription() {
        return "Give points for killing another player.";
    }

    @Override
    protected KillPlayerPointsHandler onGetNewHandler(Arena arena, IDataNode node) {
        return new KillPlayerPointsHandler(arena, this, node);
    }

    public static class KillPlayerPointsHandler extends AbstractPointsHandler implements GenericsEventListener {

        KillPlayerPointsHandler(Arena arena, PointsType type, IDataNode node) {
            super(arena, type, node);
        }

        @GenericsEventHandler
        private void onPlayerKill(PlayerArenaKillEvent event) {

            if (event.getDeadPlayer() == null)
                return;

            event.getPlayer().incrementPoints(getPoints());
        }

    }

}
