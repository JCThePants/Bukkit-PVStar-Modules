package com.jcwhatever.bukkit.pvs.modules.doorsigns.signs;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.generic.economy.EconomyHelper.CurrencyNoun;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;

import java.util.regex.Matcher;

public class EconDoorSignHandler extends AbstractNumberSignHandler {

    @Override
    public String getName() {
        return "Econ_Door";
    }

    @Override
    public String getDescription() {
        return "Open doors using player economy currency.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Econ Door",
                "<amount>",
                "--anything--",
                "--anything--"
        };
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    protected double getCost(SignContainer sign) {
        double cost;

        Matcher matcher = TextUtils.PATTERN_DECIMAL_NUMBERS.matcher(sign.getRawLine(1));

        if (!matcher.find()) {
            Msg.warning("No cost could be found on line 2 of Econ Door sign.");
            return -1;
        }

        String rawNumber = matcher.group();

        try {
            cost = Double.parseDouble(rawNumber);
        }
        catch (NumberFormatException exc) {
            Msg.warning("Failed to parse cost from Econ Door sign.");
            return -1;
        }

        return cost;
    }

    @Override
    protected double getPlayerBalance(ArenaPlayer player) {
        return EconomyHelper.getBalance(player.getHandle());
    }

    @Override
    protected void incrementPlayerBalance(ArenaPlayer player, double amount) {
        EconomyHelper.giveMoney(player.getHandle(), amount);
    }

    @Override
    protected String getCurrencyName() {
        return EconomyHelper.getCurrencyName(CurrencyNoun.SINGULAR);
    }

    @Override
    protected String getCurrencyNamePlural() {
        return EconomyHelper.getCurrencyName(CurrencyNoun.PLURAL);
    }
}