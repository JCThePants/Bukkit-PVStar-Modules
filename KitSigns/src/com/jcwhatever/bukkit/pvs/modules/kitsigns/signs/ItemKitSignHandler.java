package com.jcwhatever.bukkit.pvs.modules.kitsigns.signs;

import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.regex.Matcher;

public class ItemKitSignHandler extends SignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "Item_Kit";
    }

    @Override
    public String getDescription() {
        return "Purchase kits by paying with items from a specified kit.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Item Kit",
                "<purchaseKitName>",
                "<cost> <currencyKitName>",
                "--anything--"
        };
    }

    @Override
    public String getHeaderPrefix() {
        return TextColor.DARK_BLUE.toString();
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // do nothing
    }

    @Override
    protected boolean onSignChange(Player p, SignContainer sign) {

        Kit purchaseKit = getPurchaseKit(sign);
        if (purchaseKit == null) {
            Msg.tellError(p, "Purchase kit on line 1 was not found.");
            return false;
        }

        int cost = getCost(sign);
        if (cost == -1) {
            Msg.tellError(p, "Could not find cost on line 2.");
            return false;
        }

        Kit currencyKit = getCurrencyKit(sign);
        if (currencyKit == null) {
            Msg.tellError(p, "Currency kit on line 2 was not found.");
            return false;
        }

        return true;
    }

    @Override
    protected boolean onSignClick(Player p, SignContainer sign) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return false;

        int cost = getCost(sign);
        if (cost == -1)
            return false;

        Kit purchaseKit = getPurchaseKit(sign);
        if (purchaseKit == null)
            return false;

        Kit currencyKit = getCurrencyKit(sign);
        if (currencyKit == null)
            return false;

        if (!currencyKit.take(p, cost)) {
            Msg.tell(p, "You don't have enough to afford this kit.");
            return false;
        }

        purchaseKit.give(p);

        return true;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {
        // allow
        return true;
    }

    private int getCost(SignContainer sign) {

        String rawCost = sign.getRawLine(1);

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(rawCost);

        if (!matcher.find()) {
            Msg.warning("Failed to parse cost on line 2 of Item Door sign.");
            return -1;
        }

        String cost = matcher.group();

        try {
            return Integer.parseInt(cost);
        }
        catch (Exception e) {
            Msg.warning("Failed to parse cost on line 2 of Item Door sign.");
            return -1;
        }
    }

    @Nullable
    private Kit getPurchaseKit(SignContainer sign) {

        String kitName = sign.getRawLine(1);

        Kit kit = PVStarAPI.getKitManager().getKitByName(kitName);
        if (kit == null)
            Msg.warning("Failed to find purchase kit named '{0}' from line 2 of Item Kit sign.", kitName);

        return kit;
    }

    @Nullable
    private Kit getCurrencyKit(SignContainer sign) {

        String rawKitName = sign.getRawLine(2);

        // remove cost
        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(rawKitName);

        String kitName = matcher.replaceFirst("").trim();

        Kit kit = PVStarAPI.getKitManager().getKitByName(kitName);
        if (kit == null)
            Msg.warning("Failed to find kit named '{0}' from line 3 of Item Door sign.", kitName);

        return kit;
    }

}
