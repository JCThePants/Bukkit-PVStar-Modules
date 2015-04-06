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


package com.jcwhatever.pvs.modules.showspawns.commands;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.showspawns.Lang;
import com.jcwhatever.pvs.modules.showspawns.ShowSpawnsModule;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="spawns",
        command="show",
        description="Show arena spawns as blocks. [PVShowSpawns]")

public class ShowCommand extends AbstractPVCommand {

    @Localizable static final String _SHOW_SPAWNS = "Spawnpoints for arena '{0}' are visible as blocks.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) {

        IArena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finished

        ShowSpawnsModule.getModule().showSigns(arena);

        tellSuccess(sender, Lang.get(_SHOW_SPAWNS, arena.getName()));
    }
}

