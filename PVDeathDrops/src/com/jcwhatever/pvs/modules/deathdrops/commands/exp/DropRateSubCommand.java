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


package com.jcwhatever.pvs.modules.deathdrops.commands.exp;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.pvs.modules.deathdrops.DropSettings;
import com.jcwhatever.pvs.modules.deathdrops.Lang;
import com.jcwhatever.pvs.modules.deathdrops.commands.AbstractDropsCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="exp",
        command="droprate",
        staticParams = { "specificity", "percent=info"},
        description="Set or view exp drop rates in the selected arena at the specified specificity.",

        paramDescriptions = {
                "specificity= Specify what scope the setting applies to. " +
                        "Use 'global' for all, 'player' for players, 'mobs' for all mobs, " +
                        "or specify the mob EntityType name. More specific settings " +
                        "override general settings.",
                "number= The drop rate as a percent. (i.e 99%). Use 'clear' to remove the setting. " +
                        "Leave blank to see current setting."})

public class DropRateSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO =
            "Item drop rate in arena '{0: arena name}' is {1: percent}.";

    @Localizable static final String _SET =
            "Item drop rate in arena '{0: arena name}' changed to {1: percent}.";

    @Localizable static final String _CLEAR =
            "Value cleared for specificity '{0: specificity name}' in arena '{1: arena name}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "percent"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("percent").equals("info")) {

            double dropRate = settings.getExpDropRate();

            tell(sender, Lang.get(_INFO, arena.getName(), dropRate));
        }
        else if (args.getString("percent").equalsIgnoreCase("clear")) {

            settings.clearExpDropRate();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {
            double dropRate = args.getDouble("percent");

            settings.setExpDropRate(dropRate);

            tellSuccess(sender, Lang.get(_SET, arena.getName(), dropRate));
        }
    }
}
