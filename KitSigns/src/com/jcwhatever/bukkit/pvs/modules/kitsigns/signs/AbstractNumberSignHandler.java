package com.jcwhatever.bukkit.pvs.modules.kitsigns.signs;

import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class AbstractNumberSignHandler extends SignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    protected void onSignLoad(SignContainer sign) {
        // do nothing
    }

    @Override
    protected boolean onSignChange(Player p, SignContainer sign) {

        double cost = getCost(sign);
        if (cost == -1) {
            Msg.tell(p, "Failed to add sign because the cost could not be parsed.");
            return false;
        }

        Kit kit = getKit(sign);
        if (kit == null) {
            Msg.tell(p, "Failed to add sign because the kit named could not be found.");
            return false;
        }

        return true;
    }

    @Override
    protected boolean onSignClick(Player p, SignContainer sign) {

        ArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return false;

        double cost = getCost(sign);
        if (cost == -1)
            return false;

        double balance = getBalance(player);

        if (balance < cost) {
            Msg.tell(player, "You don't have enough {0} to afford this.", getCurrencyName());
            return false;
        }

        Kit kit = getKit(sign);
        if (kit == null)
            return false;

        incrementBalance(player, -cost);

        kit.give(p);

        return true;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {
        return true;
    }

    protected abstract double getCost(SignContainer sign);

    protected abstract double getBalance(ArenaPlayer player);

    protected abstract void incrementBalance(ArenaPlayer player, double amount);

    protected abstract String getCurrencyName();




    private Kit getKit(SignContainer sign) {
        String kitName = sign.getRawLine(1);

        Kit kit = PVStarAPI.getKitManager().getKitByName(kitName);
        if (kit == null)
            Msg.warning("Failed to find kit named '{0}' from line 2 of {1} sign.", kitName, getDisplayName());

        return kit;
    }
}
