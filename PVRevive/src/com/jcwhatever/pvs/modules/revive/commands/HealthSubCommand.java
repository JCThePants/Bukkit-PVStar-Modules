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


package com.jcwhatever.pvs.modules.revive.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.revive.Lang;
import com.jcwhatever.pvs.modules.revive.ReviveExtension;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="revive",
        command="health",
        staticParams={"amount=info"},
        description="Set or view the amount of health a player has after being revived " +
                "in the selected arena. Value range is from 0 to 20.",

        paramDescriptions = {
                "amount= The amount in the range of 0 to 20. Leave blank to see current setting."})

public class HealthSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND =
            "PVRevive extension not installed in arena '{0: arena name}'.";

    @Localizable static final String _CURRENT =
            "Current revive health in arena '{0: arena name}' is {1: health}.";

    @Localizable static final String _CHANGED =
            "Changed revive health in arena '{0: arena name}' to {1: health}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        ReviveExtension extension = arena.getExtensionManager().get(ReviveExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; //finish
        }

        if (args.getString("amount").equals("info")) {

            int health = extension.getReviveHealth();

            tell(sender, Lang.get(_CURRENT, arena.getName(), health));
        }
        else {

            int health = Math.max(1, args.getInteger("amount"));

            extension.setReviveHealth(health);

            tellSuccess(sender, Lang.get(_CHANGED, arena.getName(), health));
        }
    }
}
