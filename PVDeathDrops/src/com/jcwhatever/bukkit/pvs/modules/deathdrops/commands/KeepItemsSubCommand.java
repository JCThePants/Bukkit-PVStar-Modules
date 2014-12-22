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


package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands;

import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidArgumentException;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.DeathDropsExtension;
import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="drops",
        command="keepitems",
        staticParams = { "on|off|info=info"},
        description="View or set if players keep their items when they are killed in the selected arena.",

        paramDescriptions = {
                "on|off|info= Use 'on' to turn on, 'off' to turn off, " +
                        "'info' or leave blank to see current setting."})

public class KeepItemsSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO_KEEP = "Players keep their items when they die and respawn in arena '{0}'.";
    @Localizable static final String _INFO_NOT_KEEP = "Players lose their items when they die in arena '{0}'.";
    @Localizable static final String _SET_KEEP = "Players will now keep their items when they die in arena '{0}'.";
    @Localizable static final String _SET_NOT_KEEP = "Players will now lose their items when they die in arena '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidArgumentException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        if (args.getString("on|off|info").equals("info")) {

            boolean canKeepItems = extension.canKeepItemsOnDeath();

            if (canKeepItems)
                tell(sender, Lang.get(_INFO_KEEP, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_NOT_KEEP, arena.getName()));
        }
        else {

            boolean canKeepItems = args.getBoolean("on|off|info");

            extension.setKeepItemsOnDeath(canKeepItems);

            if (canKeepItems)
                tellSuccess(sender, Lang.get(_SET_KEEP, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_NOT_KEEP, arena.getName()));
        }
    }
}

