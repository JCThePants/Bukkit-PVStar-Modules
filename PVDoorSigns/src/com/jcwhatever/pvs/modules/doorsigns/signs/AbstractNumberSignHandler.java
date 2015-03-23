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


package com.jcwhatever.pvs.modules.doorsigns.signs;

import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.signs.SignContainer;
import com.jcwhatever.nucleus.utils.signs.SignHandler;
import com.jcwhatever.nucleus.utils.signs.SignManager;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.doorsigns.DoorBlocks;
import com.jcwhatever.pvs.modules.doorsigns.DoorManager;
import com.jcwhatever.pvs.modules.doorsigns.DoorSignsModule;
import com.jcwhatever.pvs.modules.doorsigns.Lang;

import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractNumberSignHandler extends SignHandler {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.00");
    private static final Pattern PATTERN_EMPTY_COINS = Pattern.compile("\\.00");

    @Localizable static final String _INSUFFICIENT_FUNDS =
            "You don't have enough {0: currency name} to open the door.";

    @Localizable static final String _PARTIAL_DEDUCTION =
            "Deducted {0: amount} {1: currency name} towards opening the door.";

    @Localizable static final String _DOOR_OPENED =
            "Opened the door using {0: amount} {1: currency name}.";

    /**
     * Constructor.
     */
    public AbstractNumberSignHandler(String name) {
        super(PVStarAPI.getPlugin(), name);
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // Do nothing
    }

    @Override
    protected SignChangeResult onSignChange(Player p, SignContainer sign) {

        double cost = getCost(sign);
        if (cost == -1)
            return SignChangeResult.INVALID;

        DoorManager manager = DoorSignsModule.getModule().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null) {
            Msg.debug("Door blocks not found.");
            return SignChangeResult.INVALID;
        }

        doorBlocks.setOpen(true);
        return SignChangeResult.VALID;
    }

    @Override
    protected SignClickResult onSignClick(Player p, SignContainer sign) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return SignClickResult.IGNORED;

        double cost = getCost(sign);
        if (Double.compare(cost, -1) == 0)
            return SignClickResult.IGNORED;

        DoorManager manager = DoorSignsModule.getModule().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null) {
            Msg.debug("Door blocks not found.");
            return SignClickResult.IGNORED;
        }

        if (doorBlocks.isOpen()) {
            Msg.tell(player, "Door is already open.");
            return SignClickResult.IGNORED;
        }

        double balance = getPlayerBalance(player);

        if (balance <= 0) {
            Msg.tell(player, Lang.get(_INSUFFICIENT_FUNDS, getCurrencyNamePlural()));
            return SignClickResult.IGNORED;
        }

        if (balance < cost) {

            incrementPlayerBalance(player, -balance);
            double newCost = cost - balance;

            Msg.tell(player, Lang.get(_PARTIAL_DEDUCTION, format(balance), getCurrencyNamePlural()));

            // update cost display
            String newLine = sign.getLine(2);

            Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(newLine);
            newLine = matcher.replaceFirst(format(newCost));

            sign.setLine(2, newLine);
            sign.update();

            return SignClickResult.IGNORED;
        }
        else {
            incrementPlayerBalance(player, -cost);
        }

        // open door
        if (!doorBlocks.setOpen(true)) {
            Msg.debug("Failed to open {0}.", getDisplayName());
            return SignClickResult.IGNORED;
        }

        Msg.tell(player, Lang.get(_DOOR_OPENED, format(cost), getCurrencyNamePlural()));
        manager.addArenaDoorBlocks(player.getArena(), doorBlocks);

        // restore sign
        PVStarAPI.getSignManager().restoreSign(getName(), sign.getLocation());

        return SignClickResult.HANDLED;
    }

    @Override
    protected SignBreakResult onSignBreak(Player p, SignContainer sign) {

        String doorBlocksId = SignManager.getSignNodeName(sign.getLocation());
        DoorSignsModule.getModule().getDoorManager().removeArenaDoorBlocks(doorBlocksId);

        return SignBreakResult.ALLOW;
    }


    protected abstract double getCost (SignContainer sign);

    protected abstract double getPlayerBalance(ArenaPlayer player);

    protected abstract void incrementPlayerBalance(ArenaPlayer player, double amount);

    protected abstract String getCurrencyName();

    protected abstract String getCurrencyNamePlural();

    protected String format(double number) {

        String result = DECIMAL_FORMAT.format(number);

        Matcher matcher = PATTERN_EMPTY_COINS.matcher(result);

        return matcher.replaceFirst("");

    }
}
