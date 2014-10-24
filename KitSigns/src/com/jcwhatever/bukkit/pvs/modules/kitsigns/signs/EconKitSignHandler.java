package com.jcwhatever.bukkit.pvs.modules.kitsigns.signs;

import com.jcwhatever.bukkit.generic.economy.EconomyHelper;
import com.jcwhatever.bukkit.generic.economy.EconomyHelper.CurrencyNoun;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;

import java.util.regex.Matcher;

public class EconKitSignHandler extends AbstractNumberSignHandler {

    @Override
    public String getName() {
        return "Econ_Kit";
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    public String getDescription() {
        return "Purchase kits using economy currency.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Econ Kit",
                "<kitName>",
                "*<cost>*",
                "--anything--"
        };
    }

    @Override
    protected double getCost(SignContainer sign) {
        double cost;

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(sign.getRawLine(2));

        if (!matcher.find()) {
            Msg.warning("No cost could be found on line 3 of Econ Kit sign.");
            return -1;
        }

        String rawNumber = matcher.group();

        try {
            cost = Double.parseDouble(rawNumber);
        } catch (NumberFormatException exc) {
            Msg.warning("Failed to parse cost from Econ Kit sign.");
            return -1;
        }

        return cost;
    }

    @Override
    protected double getBalance(ArenaPlayer player) {
        return EconomyHelper.getBalance(player.getHandle());
    }

    @Override
    protected void incrementBalance(ArenaPlayer player, double amount) {
        EconomyHelper.giveMoney(player.getHandle(), amount);
    }

    @Override
    protected String getCurrencyName() {
        return EconomyHelper.getCurrencyName(CurrencyNoun.PLURAL);
    }

}
