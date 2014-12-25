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


package com.jcwhatever.bukkit.pvs.modules.mobs.commands.settings;

import com.jcwhatever.generic.commands.CommandInfo;
import com.jcwhatever.generic.commands.arguments.CommandArguments;
import com.jcwhatever.generic.commands.exceptions.CommandException;
import com.jcwhatever.generic.language.Localizable;
import com.jcwhatever.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.Lang;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="settings",
        command="reset",
        staticParams={ "property" },
        description="Reset setting in the mob spawner in the currently selected arena to default.",

        paramDescriptions = {
                "property= The name of the setting property to reset."})

public class ResetSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED = "PVMobs extension is not installed in arena '{0}'.";
    @Localizable static final String _SPAWNER_NOT_FOUND = "Arena '{0}' does not have a mob spawner.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        MobArenaExtension extension = arena.getExtensionManager().get(MobArenaExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_INSTALLED, arena.getName()));
            return; // finish
        }

        ISpawner spawner = extension.getSpawner();
        if (spawner == null) {
            tellError(sender, Lang.get(_SPAWNER_NOT_FOUND, arena.getName()));
            return; // finish
        }

        ISettingsManager settings = spawner.getSettings().getManager();

        clearSetting(sender, settings, args, "property");
    }
}
