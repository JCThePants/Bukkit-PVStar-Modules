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


package com.jcwhatever.bukkit.pvs.modules.startcountdown.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.startcountdown.Lang;
import com.jcwhatever.bukkit.pvs.modules.startcountdown.StartCountdownExtension;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="game",
        command="countdown",
        staticParams={"seconds=info"},
        description="Set or view game start countdown seconds in the currently selected arena.",

        paramDescriptions = {
                "seconds= The number of seconds to countdown from. Leave blank " +
                        "to see the current setting."})

public class CountdownSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "Cannot set start countdown on arena '{0}' " +
            "because the PVStartCountdown extension is not installed.";
    @Localizable static final String _COUNTDOWN_INFO = "Game start countdown seconds in arena '{0}' is set to {1}.";
    @Localizable static final String _COUNTDOWN_SET = "Game start countdown seconds in arena '{0}' changed to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "seconds"));
        if (arena == null)
            return; // finished

        StartCountdownExtension extension = arena.getExtensionManager().get(StartCountdownExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; // finished
        }

        if (args.getString("seconds").equals("info")) {

            int seconds = extension.getStartCountdownSeconds();
            tell(sender, Lang.get(_COUNTDOWN_INFO, arena.getName(), seconds));
        }
        else {

            int seconds = args.getInteger("seconds");

            extension.setStartCountdownSeconds(seconds);

            tellSuccess(sender, Lang.get(_COUNTDOWN_SET, arena.getName(), seconds));
        }
    }
}

