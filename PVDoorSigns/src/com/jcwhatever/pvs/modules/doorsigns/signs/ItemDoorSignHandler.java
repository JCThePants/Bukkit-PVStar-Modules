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

import com.jcwhatever.nucleus.providers.kits.IKit;
import com.jcwhatever.nucleus.providers.kits.Kits;
import com.jcwhatever.nucleus.utils.coords.LocationUtils;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.managed.signs.SignHandler;
import com.jcwhatever.nucleus.utils.text.TextColor;
import com.jcwhatever.nucleus.utils.text.TextUtils;
import com.jcwhatever.pvs.api.PVStarAPI;
import com.jcwhatever.pvs.api.arena.IArenaPlayer;
import com.jcwhatever.pvs.api.arena.options.ArenaPlayerRelation;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.doorsigns.DoorBlocks;
import com.jcwhatever.pvs.modules.doorsigns.DoorManager;
import com.jcwhatever.pvs.modules.doorsigns.DoorSignsModule;
import com.jcwhatever.pvs.modules.doorsigns.Lang;

import org.bukkit.entity.Player;

import java.util.regex.Matcher;
import javax.annotation.Nullable;

public class ItemDoorSignHandler extends SignHandler {

    @Localizable static final String _INSUFFICIENT_FUNDS =
            "You don't have enough to afford this door.";

    @Localizable static final String _DESCRIPTION =
            "Open doors by paying with items from a specified kit.";

    /**
     * Constructor.
     */
    public ItemDoorSignHandler() {
        super(PVStarAPI.getPlugin(), "Item_Door");
    }

    @Override
    public String getDescription() {
        return Lang.get(_DESCRIPTION);
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
    protected void onSignLoad(ISignContainer sign) {
        // do nothing
    }

    @Override
    protected SignChangeResult onSignChange(Player player, ISignContainer sign) {

        int cost = getCost(sign);
        if (cost == -1) {
            return SignChangeResult.INVALID;
        }

        IKit kit = getKit(sign);
        if (kit == null) {
            return SignChangeResult.INVALID;
        }

        DoorManager manager = DoorSignsModule.getModule().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null)
            return SignChangeResult.INVALID;

        return SignChangeResult.VALID;
    }

    @Override
    protected SignClickResult onSignClick(Player p, ISignContainer sign) {

        IArenaPlayer player = PVStarAPI.getArenaPlayer(p);
        if (player.getArena() == null || player.getArenaRelation() == ArenaPlayerRelation.SPECTATOR)
            return SignClickResult.IGNORED;

        int cost = getCost(sign);
        if (cost == -1)
            return SignClickResult.IGNORED;

        IKit kit = getKit(sign);
        if (kit == null)
            return SignClickResult.IGNORED;

        DoorManager manager = DoorSignsModule.getModule().getDoorManager();

        DoorBlocks doorBlocks = manager.findDoors(this, sign);
        if (doorBlocks == null)
            return SignClickResult.IGNORED;

        if (!kit.take(p, cost)) {
            Msg.tell(p, Lang.get(_INSUFFICIENT_FUNDS));
            return SignClickResult.IGNORED;
        }

        doorBlocks.setOpen(true);

        return SignClickResult.HANDLED;
    }

    @Override
    protected SignBreakResult onSignBreak(Player player, ISignContainer sign) {

        String doorBlocksId = LocationUtils.serialize(sign.getLocation());
        DoorSignsModule.getModule().getDoorManager().removeArenaDoorBlocks(doorBlocksId);

        return SignBreakResult.ALLOW;
    }

    private int getCost(ISignContainer sign) {

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
    private IKit getKit(ISignContainer sign) {

        String rawKitName = sign.getRawLine(2);

        Matcher matcher = TextUtils.PATTERN_NUMBERS.matcher(rawKitName);

        String kitName = matcher.replaceFirst("").trim();

        IKit kit = Kits.get(kitName);
        if (kit == null)
            Msg.warning("Failed to find kit named '{0}' from line 3 of Item Door sign.", kitName);

        return kit;
    }
}
