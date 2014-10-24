package com.jcwhatever.bukkit.pvs.modules.doorsigns.signs;

import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.signs.SignContainer;
import com.jcwhatever.bukkit.generic.signs.SignHandler;
import com.jcwhatever.bukkit.generic.signs.SignManager;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.TextColor;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.ArenaPlayer;
import com.jcwhatever.bukkit.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.DoorBlocks;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.DoorManager;
import com.jcwhatever.bukkit.pvs.modules.doorsigns.DoorSignsModule;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.util.regex.Matcher;

public class ItemDoorSignHandler extends SignHandler {

    @Override
    public Plugin getPlugin() {
        return PVStarAPI.getPlugin();
    }

    @Override
    public String getName() {
        return "Item_Door";
    }

    @Override
    public String getDescription() {
        return "Open doors by paying with items from a specified kit.";
    }

    @Override
    public String[] getUsage() {
        return new String[] {
                "Item Door",
                "--anything--",
                "<cost> <kitName>",
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

        int cost = getCost(sign);
        if (cost == -1) {
            return false;
        }

        Kit kit = getKit(sign);
        if (kit == null) {
            return false;
        }

        DoorManager manager = DoorSignsModule.getInstance().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null)
            return false;

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

        Kit kit = getKit(sign);
        if (kit == null)
            return false;

        DoorManager manager = DoorSignsModule.getInstance().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null)
            return false;

        if (!kit.take(p, cost)) {
            Msg.tell(p, "You don't have enough to afford this door.");
            return false;
        }

        doorBlocks.setOpen(true);

        return false;
    }

    @Override
    protected boolean onSignBreak(Player p, SignContainer sign) {

        String doorBlocksId = SignManager.getSignNodeName(sign.getLocation());
        DoorSignsModule.getInstance().getDoorManager().removeArenaDoorBlocks(doorBlocksId);

        // allow
        return true;
    }

    private int getCost(SignContainer sign) {

        String rawCost = sign.getRawLine(2);

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(rawCost);

        if (!matcher.find()) {
            Msg.warning("Failed to parse cost on line 3 of Item Door sign.");
            return -1;
        }

        String cost = matcher.group();

        try {
            return Integer.parseInt(cost);
        }
        catch (Exception e) {
            Msg.warning("Failed to parse cost on line 3 of Item Door sign.");
            return -1;
        }
    }

    @Nullable
    private Kit getKit(SignContainer sign) {

        String rawKitName = sign.getRawLine(2);

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(rawKitName);

        String kitName = matcher.replaceFirst("").trim();

        Kit kit = PVStarAPI.getKitManager().getKitByName(kitName);
        if (kit == null)
            Msg.warning("Failed to find kit named '{0}' from line 3 of Item Door sign.", kitName);

        return kit;
    }

}
