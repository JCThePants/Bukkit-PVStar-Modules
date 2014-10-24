package com.jcwhatever.bukkit.pvs.modules.doorsigns.signs;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

public class PointsDoorSignHandler extends AbstractNumberSignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "Points_Door";
    }

    @Override
    public String getDescription() {
        return "Opens doors using player points as currency.";
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
    protected double getCost(SignContainer sign) {
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
        return "Points";
    }

    @Override
    protected String getCurrencyNamePlural() {
        return "Points";
    }

}
