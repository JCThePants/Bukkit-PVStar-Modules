package com.jcwhatever.bukkit.pvs.modules.kitsigns.signs;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;

import java.util.regex.Matcher;

public class PointsKitSignHandler extends AbstractNumberSignHandler {

    @Override
    public String getName() {
        return "Points_Kit";
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    public String getDescription() {
        return "Purchase kits using player points as currency.";
    }

    @Override
    public String[] getUsage() {
        return new String[]{
                "Points Kit",
                "<kitName>",
                "*<cost>*",
                "--anything--"
        };
    }


    @Override
    protected double getBalance(ArenaPlayer player) {
        return player.getPoints();
    }

    @Override
    protected void incrementBalance(ArenaPlayer player, double amount) {
        player.incrementPoints((int) amount);
    }

    @Override
    protected String getCurrencyName() {
        return "Points";
    }

    @Override
    protected double getCost(SignContainer sign) {
        int cost;

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(sign.getRawLine(2));

        if (!matcher.find()) {
            Msg.warning("No cost could be found on line 3 of Points Kit sign.");
            return -1;
        }

        String rawNumber = matcher.group();

        try {
            cost = Integer.parseInt(rawNumber);
        } catch (NumberFormatException exc) {
            Msg.warning("Failed to parse cost from Points Kit sign.");
            return -1;
        }

        return cost;
    }
}
