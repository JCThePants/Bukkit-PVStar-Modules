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


package com.jcwhatever.bukkit.pvs.modules.kitsigns.signs;

import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.nucleus.utils.kits.IKit;
import com.jcwhatever.nucleus.signs.SignContainer;
import com.jcwhatever.nucleus.signs.SignHandler;

import org.bukkit.entity.Player;

public abstract class AbstractNumberSignHandler extends SignHandler {

    /**
     * Constructor.
     *
     * @param name   The name of the sign (Used in the sign header)
     */
    public AbstractNumberSignHandler(String name) {
        super(PVStarAPI.getPlugin(), name);
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // do nothing
    }

    @Override
    protected SignChangeResult onSignChange(Player p, SignContainer sign) {

        double cost = getCost(sign);
        if (cost == -1) {
            Msg.tell(p, "Failed to add sign because the cost could not be parsed.");
            return SignChangeResult.INVALID;
        }

        IKit kit = getKit(sign);
        if (kit == null) {
            Msg.tell(p, "Failed to add sign because the kit named could not be found.");
            return SignChangeResult.INVALID;
        }

        return SignChangeResult.VALID;
    }

    @Override
    protected SignClickResult onSignClick(Player p, SignContainer sign) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return SignClickResult.IGNORED;

        double cost = getCost(sign);
        if (cost == -1)
            return SignClickResult.IGNORED;

        double balance = getBalance(player);

        if (balance < cost) {
            Msg.tell(player, "You don't have enough {0} to afford this.", getCurrencyName());
            return SignClickResult.IGNORED;
        }

        IKit kit = getKit(sign);
        if (kit == null)
            return SignClickResult.IGNORED;

        incrementBalance(player, -cost);

        kit.give(p);

        return SignClickResult.HANDLED;
    }

    @Override
    protected SignBreakResult onSignBreak(Player p, SignContainer sign) {
        return SignBreakResult.ALLOW;
    }

    protected abstract double getCost(SignContainer sign);

    protected abstract double getBalance(ArenaPlayer player);

    protected abstract void incrementBalance(ArenaPlayer player, double amount);

    protected abstract String getCurrencyName();


    private IKit getKit(SignContainer sign) {
        String kitName = sign.getRawLine(1);

        IKit kit = PVStarAPI.getKitManager().get(kitName);
        if (kit == null)
            Msg.warning("Failed to find kit named '{0}' from line 2 of {1} sign.", kitName, getDisplayName());

        return kit;
    }
}
