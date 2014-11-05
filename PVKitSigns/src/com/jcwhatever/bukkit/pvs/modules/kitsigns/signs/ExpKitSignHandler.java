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

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;

import java.util.regex.Matcher;

public class ExpKitSignHandler extends AbstractNumberSignHandler {

    @Override
    public String getName() {
        return "Exp_Kit";
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    public String getDescription() {
        return "Purchase kits using player Exp as currency.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Exp Kit",
                "<kitName>",
                "*<cost>*",
                "-- anything--"
        };
    }

    @Override
    protected double getBalance(ArenaPlayer player) {
        return player.getHandle().getLevel();
    }

    @Override
    protected void incrementBalance(ArenaPlayer player, double amount) {
        int level = player.getHandle().getLevel();

        player.getHandle().setLevel(0);
        player.getHandle().setLevel(level + (int)amount);
    }

    @Override
    protected String getCurrencyName() {
        return "Exp Levels";
    }

    @Override
    protected double getCost(SignContainer sign) {
        int cost;

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(sign.getRawLine(2));

        if (!matcher.find()) {
            Msg.warning("No cost could be found on line 3 of Exp Kit sign.");
            return -1;
        }

        String rawNumber = matcher.group();

        try {
            cost = Integer.parseInt(rawNumber);
        }
        catch (NumberFormatException exc) {
            Msg.warning("Failed to parse cost from Exp Kit sign.");
            return -1;
        }

        return cost;
    }
}
