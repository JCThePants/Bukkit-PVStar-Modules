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
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.pvs.modules.deathdrops.DropSettings;
import com.jcwhatever.pvs.modules.deathdrops.Lang;
import com.jcwhatever.pvs.modules.deathdrops.commands.AbstractDropsCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="drops",
        command="exp",
        staticParams = { "specificity", "on|off|clear|info=info"},
        description="Enable or disable exp drops.",

        paramDescriptions = {
                "specificity= Specify what scope the setting applies to. " +
                        "Use 'global' for all, 'player' for players, 'mobs' for all mobs, " +
                        "or specify the mob EntityType name. More specific settings " +
                        "override general settings.",
                "on|off|clear|info= Use 'on' to turn on, 'off' to turn off, " +
                        "'clear' to remove the setting, 'info' or leave blank " +
                        "to see current setting."})

public class ExpCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO_ON = "Exp drops in arena '{0}' are on.";
    @Localizable static final String _INFO_OFF = "Exp drops in arena '{0}' are off.";
    @Localizable static final String _SET_ON = "Exp drops in arena '{0}' changed to ON.";
    @Localizable static final String _SET_OFF = "Exp drops in arena '{0}' changed to {RED}OFF.";
    @Localizable static final String _CLEAR = "Value cleared for specificity '{0}' in arena '{1}'.";

    public ExpCommand() {
        super();

        registerCommand(AmountSubCommand.class);
        registerCommand(DropRateSubCommand.class);
        registerCommand(TransferSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|clear|info"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("on|off|clear|info").equals("info")) {

            boolean isEnabled = settings.isExpDropEnabled();

            if (isEnabled)
                tell(sender, Lang.get(_INFO_ON, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_OFF, arena.getName()));
        }
        else if (args.getString("on|off|clear|info").equalsIgnoreCase("clear")) {

            settings.clearExpDropEnabled();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {
            boolean isEnabled = args.getBoolean("on|off|clear|info");

            settings.setExpDropEnabled(isEnabled);

            if (isEnabled)
                tellSuccess(sender, Lang.get(_SET_ON, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_OFF, arena.getName()));
        }


    }

}
