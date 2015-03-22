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


package com.jcwhatever.pvs.modules.chests.commands;

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
        parent="chests",
        command="max",
        staticParams={"number=info"},
        description="Set max number of chests in selected arena when chests are randomized. -1 to disregard setting.",

        paramDescriptions = {
                "number= The max number of chests. Leave blank to see current setting."})

public class MaxSubCommand extends AbstractPVCommand {

    @Localizable static final String _INFO = "Max random chests in arena '{0}' is {1}.";
    @Localizable static final String _SET = "Max random chests in arena '{0}' changed to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "number"));
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        if (args.getString("number").equals("info")) {

            int max = extension.getChestSettings().getMaxChests();

            tell(sender, Lang.get(_INFO, arena.getName(), max));

        }
        else {

            int max = args.getInteger("number");

            tellSuccess(sender, Lang.get(_SET, arena.getName(), max));

        }
    }
}
