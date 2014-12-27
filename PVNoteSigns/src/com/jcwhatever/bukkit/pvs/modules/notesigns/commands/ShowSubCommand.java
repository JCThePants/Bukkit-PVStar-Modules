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


package com.jcwhatever.bukkit.pvs.modules.notesigns.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.language.Localizable;
import com.jcwhatever.nucleus.signs.SignContainer;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.notesigns.Lang;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.UUID;

@CommandInfo(
        parent="notes",
        command="show",
        description="Show notes in the selected arena.")

public class ShowSubCommand extends AbstractPVCommand {

    @Localizable
    static final String _SUCCESS = "{0} signs made visible in arena '{1}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        int hideCount = 0;
        List<SignContainer> signs = PVStarAPI.getSignManager().getSigns("Note");
        if (signs != null) {

            for (SignContainer sign : signs) {

                IDataNode signNode = sign.getDataNode();
                if (signNode == null)
                    continue;

                UUID arenaId = signNode.getUUID("arena-id");
                if (!arena.getId().equals(arenaId))
                    continue;

                PVStarAPI.getSignManager().restoreSign("Note", sign.getLocation());
                hideCount++;
            }
        }

        tellSuccess(sender, Lang.get(_SUCCESS, hideCount, arena.getName()));
    }
}
