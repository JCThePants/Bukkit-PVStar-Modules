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


package com.jcwhatever.pvs.modules.notesigns.commands;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.signs.ISignContainer;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.notesigns.Lang;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.UUID;

@CommandInfo(
        parent="notes",
        command="hide",
        description="Hide notes in the selected arena.")

public class HideSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS = "{0: number} signs hidden in arena '{1: arena name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNING);
        if (arena == null)
            return; // finish

        int hideCount = 0;
        Collection<ISignContainer> signs = Nucleus.getSignManager().getSigns("Note");

        for (ISignContainer sign : signs) {

            IDataNode metaNode = sign.getMetaNode();

            UUID arenaId = metaNode.getUUID("arena-id");
            if (!arena.getId().equals(arenaId))
                continue;

            Sign s = sign.getSign();
            if (s == null)
                continue;

            s.getBlock().setType(Material.AIR);
            hideCount++;
        }

        tellSuccess(sender, Lang.get(_SUCCESS, hideCount, arena.getName()));
    }
}
