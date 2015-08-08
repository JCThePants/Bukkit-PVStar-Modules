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


package com.jcwhatever.pvs.modules.kitsigns.signs;

import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.Kits;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.managed.signs.SignHandler;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.options.ArenaContext;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.kitsigns.Lang;

import com.jcwhatever.pvs.modules.kitsigns.events.KitPurchasedEvent;
import org.bukkit.entity.Player;

public abstract class AbstractNumberSignHandler extends SignHandler {

    @Localizable static final String _ADD_SIGN_FAILED_COST_PARSE =
            "Failed to add sign because the cost could not be parsed.";

    @Localizable static final String _ADD_SIGN_FAILED_KIT_NOT_FOUND =
            "Failed to add sign because the kit named could not be found.";

    @Localizable static final String _INSUFFICIENT_FUNDS =
            "You don't have enough {0: currency name} to afford this.";

    /**
     * Constructor.
     *
     * @param name   The name of the sign (Used in the sign header)
     */
    public AbstractNumberSignHandler(String name) {
        super(PVStarAPI.getPlugin(), name);
    }

    @Override
    protected void onSignLoad(ISignContainer sign) {
        // do nothing
    }

    @Override
    protected SignChangeResult onSignChange(Player player, ISignContainer sign) {

        double cost = getCost(sign);
        if (cost == -1) {
            Msg.tell(player, Lang.get(_ADD_SIGN_FAILED_COST_PARSE));
            return SignChangeResult.INVALID;
        }

        IKit kit = getKit(sign);
        if (kit == null) {
            Msg.tell(player, Lang.get(_ADD_SIGN_FAILED_KIT_NOT_FOUND));
            return SignChangeResult.INVALID;
        }

        return SignChangeResult.VALID;
    }

    @Override
    protected SignClickResult onSignClick(Player p, ISignContainer sign) {

        IArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getContext() == ArenaContext.SPECTATOR)
            return SignClickResult.IGNORED;

        double cost = getCost(sign);
        if (cost == -1)
            return SignClickResult.IGNORED;

        IKit kit = getKit(sign);
        if (kit == null)
            return SignClickResult.IGNORED;

        KitPurchasedEvent event = new KitPurchasedEvent(player, kit, cost, getCurrencyType());
        player.getArena().getEventManager().call(this, event);

        if (event.isCancelled())
            return SignClickResult.IGNORED;

        kit = event.getKit();
        cost = event.getCost();

        double balance = getBalance(player);

        if (balance < cost) {
            Msg.tell(player, Lang.get(_INSUFFICIENT_FUNDS, getCurrencyName()));
            return SignClickResult.IGNORED;
        }

        incrementBalance(player, -cost);

        kit.give(p);

        return SignClickResult.HANDLED;
    }

    @Override
    protected SignBreakResult onSignBreak(Player player, ISignContainer sign) {
        return SignBreakResult.ALLOW;
    }

    protected abstract double getCost(ISignContainer sign);

    protected abstract double getBalance(IArenaPlayer player);

    protected abstract void incrementBalance(IArenaPlayer player, double amount);

    protected abstract String getCurrencyName();

    protected abstract KitPurchasedEvent.CurrencyType getCurrencyType();


    private IKit getKit(ISignContainer sign) {
        String kitName = sign.getRawLine(1);

        IKit kit = Kits.get(kitName);
        if (kit == null)
            Msg.warning("Failed to find kit named '{0}' from line 2 of {1} sign.", kitName, getDisplayName());

        return kit;
    }
}
