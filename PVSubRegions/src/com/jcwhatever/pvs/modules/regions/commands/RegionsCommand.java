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


package com.jcwhatever.pvs.modules.regions.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.regions.commands.settings.SettingsCommand;

@CommandInfo(
        command={"regions", "subregions", "pvregions" },
        description="Manage special arena regions. [PVSubRegions]")

public class RegionsCommand extends AbstractPVCommand {

    public RegionsCommand() {
        super();

        registerCommand(SettingsCommand.class);
        registerCommand(AddSubCommand.class);
        registerCommand(DelSubCommand.class);
        registerCommand(ListSubCommand.class);
        registerCommand(RedefineSubCommand.class);
        registerCommand(TriggerSubCommand.class);
        registerCommand(TypesSubCommand.class);
        registerCommand(UntriggerSubCommand.class);
    }
}
