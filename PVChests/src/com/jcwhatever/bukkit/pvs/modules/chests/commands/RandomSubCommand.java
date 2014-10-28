/* This file is part of PV-Star Modules: PVChests for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.pvs.modules.chests.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.modules.chests.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.chests.ChestExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="chests",
        command="random",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} chests random <on|off>",
        description="Set or view randomizing of chest availability setting in the selected arena.")

public class RandomSubCommand extends AbstractPVCommand {

    @Localizable static final String _INFO_ENABLED = "Chest randomizing in arena '{0}' is enabled.";
    @Localizable static final String _INFO_DISABLED = "Chest randomizing in arena '{0}' is disabled.";
    @Localizable static final String _SET_ENABLED = "Chest randomizing in arena '{0}' has been changed to Enabled.";
    @Localizable static final String _SET_DISABLED = "Chest randomizing in arena '{0}' has been changed to Disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = extension.getChestSettings().isChestsRandomized();

            if (isEnabled)
                tell(sender, Lang.get(_INFO_ENABLED, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_DISABLED, arena.getName()));

        }
        else {

            boolean isEnabled = args.getBoolean("on|off|info");

            if (isEnabled)
                tellSuccess(sender, Lang.get(_SET_ENABLED, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_DISABLED, arena.getName()));
        }

    }
}