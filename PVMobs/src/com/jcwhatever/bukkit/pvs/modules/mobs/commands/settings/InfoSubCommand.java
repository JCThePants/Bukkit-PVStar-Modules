/* This file is part of PV-Star Modules: PVMobs for Bukkit, licensed under the MIT License (MIT).
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

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinition;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerInfo;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="settings",
        command="info",
        staticParams={ "page=1"},
        usage="/{plugin-command} mobs settings info [page]",
        description="Get info about the settings of the mob spawner in the currently selected arena.")

public class InfoSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED = "PVMobs extension is not installed in arena '{0}'.";
    @Localizable static final String _SPAWNER_NOT_FOUND = "Arena '{0}' does not have a mob spawner.";
    @Localizable static final String _PAGINATOR_TITLE = "Spawner Info";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        int page = args.getInt("page");

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

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        pagin.add("ARENA", arena.getName());
        pagin.add("SPAWNER", spawner.getClass().getAnnotation(SpawnerInfo.class).name());

        SettingDefinitions defs = spawner.getSettings().getDefinitions();

        if (defs.size() > 0) {

            for (SettingDefinition def : defs.values()) {
                Object value = spawner.getSettings().getManager().get(def.getConfigName(), true);

                pagin.add(def.getConfigName(), value);
            }
        }

        pagin.show(sender, page, FormatTemplate.DEFINITION);
    }

}
