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
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.utils.signs.SignContainer;
import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.bukkit.pvs.api.PVStarAPI;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.notesigns.Lang;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="notes",
        command="hideall",
        description="Hide all notes in all arenas.")

public class HideAllSubCommand extends AbstractPVCommand {

    @Localizable
    static final String _SUCCESS = "{0} Note signs hidden.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        int hideCount = 0;
        List<SignContainer> signs = PVStarAPI.getSignManager().getSigns("Note");

        for (SignContainer sign : signs) {

            IDataNode signNode = sign.getDataNode();
            if (signNode == null)
                continue;

            Sign s = sign.getSign();
            if (s == null)
                continue;

            s.getBlock().setType(Material.AIR);
            hideCount++;
        }

        tellSuccess(sender, Lang.get(_SUCCESS, hideCount));
    }
}