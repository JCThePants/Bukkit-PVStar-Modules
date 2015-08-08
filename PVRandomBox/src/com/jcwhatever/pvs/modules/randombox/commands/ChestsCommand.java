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


package com.jcwhatever.pvs.modules.randombox.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.mixins.IVisibleCommand;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.randombox.RandomBoxExtension;
import com.jcwhatever.pvs.modules.randombox.commands.items.ItemsCommand;
import org.bukkit.command.CommandSender;

@CommandInfo(
        command="randbox",
        description="Manage arena random boxes. [PVRandomBox]")

public class ChestsCommand extends AbstractPVCommand implements IVisibleCommand {

    public ChestsCommand() {
        super();

        registerCommand(MaxSubCommand.class);
        registerCommand(ScanSubCommand.class);
        registerCommand(RandomSubCommand.class);

        registerCommand(ItemsCommand.class);
    }

    @Override
    public boolean isVisible(CommandSender sender) {
        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        return arena != null && arena.getExtensions().has(RandomBoxExtension.class);
    }
}
