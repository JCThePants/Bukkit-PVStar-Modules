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


package com.jcwhatever.pvs.modules.mobs.commands.settings;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.storage.settings.ISettingsManager;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.mobs.Lang;
import com.jcwhatever.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.pvs.modules.mobs.spawners.ISpawner;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="settings",
        command="set",
        staticParams={ "property", "value"},
        description="Change setting in the mob spawner in the currently selected arena.",

        paramDescriptions = {
                "property= The name of the setting property.",
                "value= The value to set. Value type depends on the property being set."})

public class SetSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED =
            "PVMobs extension is not installed in arena '{0: arena name}'.";

    @Localizable static final String _SPAWNER_NOT_FOUND =
            "Arena '{0: arena name}' does not have a mob spawner.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNING);
        if (arena == null)
            return; // finish

        MobArenaExtension extension = arena.getExtensions().get(MobArenaExtension.class);
        if (extension == null)
            throw new CommandException(Lang.get(_EXTENSION_NOT_INSTALLED, arena.getName()));

        ISpawner spawner = extension.getSpawner();
        if (spawner == null)
            throw new CommandException(Lang.get(_SPAWNER_NOT_FOUND, arena.getName()));

        ISettingsManager settings = spawner.getSettings().getManager();

        setSetting(sender, settings, args, "property", "value");
    }
}