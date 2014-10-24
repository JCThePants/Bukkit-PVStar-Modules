package com.jcwhatever.bukkit.pvs.modules.doorsigns.signs;

import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.signs.SignManager;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.DoorBlocks;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.DoorManager;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.DoorSignsModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractNumberSignHandler extends SignHandler {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.00");
    private static final Pattern PATTERN_EMPTY_COINS = Pattern.compile("\\.00");

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // Do nothing
    }

    @Override
    protected boolean onSignChange(Player p, SignContainer sign) {

        double cost = getCost(sign);
        if (cost == -1)
            return false;

        DoorManager manager = DoorSignsModule.getInstance().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null) {
            Msg.debug("Door blocks not found.");
            return false; // finished
        }

        doorBlocks.setOpen(true);
        return true;
    }

    @Override
    protected boolean onSignClick(Player p, SignContainer sign) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return false; // finished

        double cost = getCost(sign);
        if (Double.compare(cost, -1) == 0)
            return false; // finished

        DoorManager manager = DoorSignsModule.getInstance().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null) {
            Msg.debug("Door blocks not found.");
            return false; // finished
        }

        if (doorBlocks.isOpen()) {
            Msg.tell(player, "Door is already open.");
            return false; // finished
        }

        double balance = getPlayerBalance(player);

        if (balance <= 0) {
            Msg.tell(player, "You don't have enough {0} to open the door.", getCurrencyNamePlural());
            return false; // finished
        }

        if (balance < cost) {

            incrementPlayerBalance(player, -balance);
            double newCost = cost - balance;

            Msg.tell(player, "Deducted {0} {1} towards opening the door.", format(balance), getCurrencyNamePlural());

            // update cost display
            String newLine = sign.getLine(2);

            Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(newLine);
            newLine = matcher.replaceFirst(format(newCost));

            sign.setLine(2, newLine);
            sign.update();

            return false; // finished
        }
        else {
            incrementPlayerBalance(player, -cost);
        }

        // open door
        if (!doorBlocks.setOpen(true)) {
            Msg.debug("Failed to open {0}.", getDisplayName());
            return false; // finished
        }

        Msg.tell(player, "Opened the door using {0} {1}.", format(cost), getCurrencyNamePlural());
        manager.addArenaDoorBlocks(player.getArena(), doorBlocks);

        // restore sign
        PVStarAPI.getSignManager().restoreSign(getName(), sign.getLocation());

        return true;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {

        String doorBlocksId = SignManager.getSignNodeName(sign.getLocation());
        DoorSignsModule.getInstance().getDoorManager().removeArenaDoorBlocks(doorBlocksId);

        // allow
        return true;
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
