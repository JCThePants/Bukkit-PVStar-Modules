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

package com.jcwhatever.bukkit.pvs.modules.gamblesigns.signs;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.generic.utils.Rand;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.gamblesigns.events.GambleTriggeredEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

public class GambleSignHandler extends SignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "Gamble";
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    public String getDescription() {
        return "Triggers gamble event when clicked. Event can used by scripts via the gamble script api.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Gamble",
                "<eventName>",
                "<chance> (i.e. 1:10) (i.e 45%)",
                "--anything--"
        };
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // do nothing
    }

    @Override
    protected boolean onSignChange(Player p, SignContainer sign) {

        String eventName = sign.getRawLine(1);
        if (eventName.trim().isEmpty()) {
            Msg.tellError(p, "No event name specified on line 2.");
            return false;
        }

        double chance = getChance(sign);
        if (Double.compare(chance, 0.0D) == 0) {
            Msg.tellError(p, "Failed to parse chance on line 3.");
            return false;
        }

        return true;
    }

    @Override
    protected boolean onSignClick(Player p, SignContainer sign) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return false;

        String eventName = sign.getRawLine(1);
        if (eventName.isEmpty())
            return false;

        double chance = getChance(sign);
        if (Double.compare(chance, 0.0D) == 0)
            return false;


        boolean isWin = Rand.chance((int)chance);
        if (!isWin)
            return false;

        GambleTriggeredEvent event = new GambleTriggeredEvent(player.getArena(), player, eventName, sign);
        player.getArena().getEventManager().call(event);

        return true;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {

        // allow
        return true;
    }

    private double getChance(SignContainer sign) {

        String[] components = TextUtils.PATTERN_COLON.split(sign.getRawLine(2));

        // Format1: minValue:total (i.e. 1:10)
        // Format2: percent

        double minValue;
        double total = 100;

        if (components.length == 2) {

            try {
                minValue = Integer.parseInt(components[0]);
            }
            catch (NumberFormatException nfe) {
                return 0.0D;
            }


            try {
                total = Integer.parseInt(components[1]);
            }
            catch (NumberFormatException nfe) {
                return 0.0D;
            }
        }
        else if (components.length == 1 && components[0].endsWith("%")) {

            Matcher matt = TextUtils.PATTERN_NUMBERS.matcher(components[0]);
            if (!matt.find()) {
                return 0.0D;
            }

            try {
                minValue = Integer.parseInt(matt.group());
            }
            catch (NumberFormatException nfe) {
                return 0.0D;
            }
        }
        else {
            return 0.0D;
        }

        return (minValue / total) * 100;
    }

}
