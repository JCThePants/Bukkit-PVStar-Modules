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


package com.jcwhatever.pvs.modules.borders.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.pvs.api.arena.Arena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.borders.BordersExtension;
import com.jcwhatever.pvs.modules.borders.Lang;
import com.jcwhatever.pvs.modules.borders.OutsidersAction;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="arena",
        command="outsiders",
        usage="/{plugin-command} {command} outsiders [none|join|kick]",
        description="Set or view the action taken when a non-arena player enters the selected arenas region.")

public class OutsidersSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "PVBorders extension is not installed in arena '{0}'.";
    @Localizable static final String _VIEW_OUTSIDERS = "Action taken when outsiders enter region for arena '{0}' is {1}.";
    @Localizable static final String _SET_OUTSIDERS = "Action taken when outsiders enter region for arena '{0}' changed to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "none|join|kick|info"));
        if (arena == null)
            return; // finished

        BordersExtension extension = arena.getExtensionManager().get(BordersExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; // finished
        }

        if (args.getString("none|join|kick|info").equals("info")) {
            tell(sender, Lang.get(_VIEW_OUTSIDERS, arena.getName(), extension.getOutsidersAction().name()));
        }
        else {
            OutsidersAction action = args.getEnum("none|join|kick|info", OutsidersAction.class);
            extension.setOutsidersAction(action);

            tellSuccess(sender, Lang.get(_SET_OUTSIDERS, arena.getName(), action.name()));
        }
    }
}
