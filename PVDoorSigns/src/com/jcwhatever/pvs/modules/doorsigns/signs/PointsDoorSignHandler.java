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
import com.jcwhatever.nucleus.utils.signs.ISignContainer;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.doorsigns.Lang;

import java.util.regex.Matcher;

public class PointsDoorSignHandler extends AbstractNumberSignHandler {

    @Localizable static final String _DESCRIPTION =
            "Opens doors using player points as currency.";

    @Localizable static final String _CURRENCY_NAME =
            "Points";

    /**
     * Constructor.
     */
    public PointsDoorSignHandler() {
        super("Points_Door");
    }

    @Override
    public String getDescription() {
        return Lang.get(_DESCRIPTION);
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Points Door",
                "--anything--",
                "*<points>*",
                "--anything--"
        };
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    protected double getCost(ISignContainer sign) {
        int cost;

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(sign.getRawLine(1));

        if (!matcher.find()) {
            Msg.warning("No cost could be found on line 2 of Points Door sign.");
            return -1;
        }

        String rawNumber = matcher.group();

        try {
            cost = Integer.parseInt(rawNumber);
        }
        catch (NumberFormatException exc) {
            Msg.warning("Failed to parse cost from Points Door sign.");
            return -1;
        }

        return cost;
    }

    @Override
    protected double getPlayerBalance(ArenaPlayer player) {
        return player.getPoints();
    }

    @Override
    protected void incrementPlayerBalance(ArenaPlayer player, double amount) {
        player.incrementPoints((int) amount);
    }

    @Override
    protected String getCurrencyName() {
        return Lang.get(_CURRENCY_NAME);
    }

    @Override
    protected String getCurrencyNamePlural() {
        return getCurrencyName();
    }
}