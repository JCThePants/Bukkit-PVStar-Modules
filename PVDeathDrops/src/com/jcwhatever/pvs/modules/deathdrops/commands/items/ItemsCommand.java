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


package com.jcwhatever.pvs.modules.deathdrops.commands.items;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.pvs.modules.deathdrops.DropSettings;
import com.jcwhatever.pvs.modules.deathdrops.Lang;
import com.jcwhatever.pvs.modules.deathdrops.commands.AbstractDropsCommand;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="drops",
        command="items",
        staticParams = { "specificity", "on|off|clear|info=info"},
        description="Enable or disable item drops.",

        paramDescriptions = {
                "specificity= Specify what scope the setting applies to. " +
                        "Use 'global' for all, 'player' for players, 'mobs' for all mobs, " +
                        "or specify the mob EntityType name. More specific settings " +
                        "override general settings.",
                "on|off|clear|info= Use 'on' to turn on, 'off' to turn off, 'clear' to " +
                        "remove the setting, 'info' or leave blank to see current setting."})

public class ItemsCommand extends AbstractDropsCommand implements IExecutableCommand {

    @Localizable static final String _INFO_ON =
            "Item drops in arena '{0: arena name}' are on.";

    @Localizable static final String _INFO_OFF =
            "Item drops in arena '{0: arena name}' are off.";

    @Localizable static final String _SET_ON =
            "Item drops in arena '{0: arena name}' changed to ON.";

    @Localizable static final String _SET_OFF =
            "Item drops in arena '{0: arena name}' changed to {RED}OFF.";

    @Localizable static final String _CLEAR =
            "Value cleared for specificity '{0: specificity name}' in arena '{1: arena name}'.";

    public ItemsCommand() {
        super();

        registerCommand(DropRateSubCommand.class);
        registerCommand(RandomSubCommand.class);
        registerCommand(TransferSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|clear|info"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("on|off|clear|info").equals("info")) {

            boolean isEnabled = settings.isItemDropEnabled();

            if (isEnabled)
                tell(sender, Lang.get(_INFO_ON, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_OFF, arena.getName()));
        }
        else if (args.getString("on|off|clear|info").equalsIgnoreCase("clear")) {

            settings.clearItemDropEnabled();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {
            boolean isEnabled = args.getBoolean("on|off|clear|info");

            settings.setItemDropEnabled(isEnabled);

            if (isEnabled)
                tellSuccess(sender, Lang.get(_SET_ON, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_OFF, arena.getName()));
        }
    }
}
