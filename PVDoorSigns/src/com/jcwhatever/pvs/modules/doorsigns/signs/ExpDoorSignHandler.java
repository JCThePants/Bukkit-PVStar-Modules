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

import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.doorsigns.Lang;
import org.bukkit.entity.Player;

import java.util.regex.Matcher;

public class ExpDoorSignHandler extends AbstractNumberSignHandler {

    public static final String NAME = "Exp_Door";

    @Localizable static final String _DESCRIPTION =
            "Opens doors using player Exp as currency.";

    @Localizable static final String _CURRENCY_NAME =
            "Exp Levels";

    /**
     * Constructor.
     */
    public ExpDoorSignHandler() {
        super(NAME);
    }

    @Override
    public String getDescription() {
        return Lang.get(_DESCRIPTION).toString();
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Exp Door",
                "--anything--",
                "*<cost>*",
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

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(sign.getRawLine(2));

        if (!matcher.find()) {
            Msg.warning("No cost could be found on line 3 of Exp Door sign.");
            return -1;
        }

        String rawNumber = matcher.group();

        try {
            cost = Integer.parseInt(rawNumber);
        }
        catch (NumberFormatException exc) {
            Msg.warning("Failed to parse cost from Exp Door sign.");
            return -1;
        }

        return cost;
    }

    @Override
    protected double getPlayerBalance(IArenaPlayer arenaPlayer) {

        if (!(arenaPlayer.getEntity() instanceof Player))
            return 0.0D;

        return ((Player) arenaPlayer.getEntity()).getLevel();
    }

    @Override
    protected void incrementPlayerBalance(IArenaPlayer arenaPlayer, double amount) {

        if (!(arenaPlayer.getEntity() instanceof Player))
            return;

        Player player = (Player)arenaPlayer.getEntity();

        int level = player.getLevel();
        player.setLevel(0);
        player.setLevel(level + (int) amount);
    }

    @Override
    protected String getCurrencyName() {
        return Lang.get(_CURRENCY_NAME).toString();
    }

    @Override
    protected String getCurrencyNamePlural() {
        return getCurrencyName();
    }
}
