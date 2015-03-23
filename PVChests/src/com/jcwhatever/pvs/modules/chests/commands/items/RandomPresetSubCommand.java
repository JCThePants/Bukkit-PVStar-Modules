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


package com.jcwhatever.pvs.modules.chests.commands.items;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.chests.ChestExtension;
import com.jcwhatever.pvs.modules.chests.Lang;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="items",
        command="randompreset",
        staticParams={"on|off|info=info"},
        description="Turn randomizing of preset chest contents on or off in the selected arena.",

        paramDescriptions = {
                "on|off|info= Use 'on' to turn on, 'off' to turn off, " +
                        "'info' or leave blank to see current setting."})

public class RandomPresetSubCommand extends AbstractPVCommand {

    @Localizable static final String _INFO_ON =
            "Preset chest contents are randomized in arena '{0: arena name}'.";

    @Localizable static final String _INFO_OFF =
            "Preset chest contents are NOT randomized in arena '{0: arena name}'.";

    @Localizable static final String _SET_ON =
            "Preset chest contents in arena '{0: arena name}' changed to ON.";

    @Localizable static final String _SET_OFF =
            "Preset chest contents in arena '{0: arena name}' changed to OFF.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isRandomized = extension.getItemSettings().isPresetContentsRandomized();

            if (isRandomized)
                tell(sender, Lang.get(_INFO_ON, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_OFF, arena.getName()));
        }
        else {

            boolean isRandomized = args.getBoolean("on|off|info");

            extension.getItemSettings().setPresetContentsRandomized(isRandomized);

            if (isRandomized)
                tellSuccess(sender, Lang.get(_SET_ON, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_OFF, arena.getName()));

        }
    }
}
