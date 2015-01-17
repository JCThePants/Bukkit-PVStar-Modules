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

package com.jcwhatever.bukkit.pvs.modules.mobs.commands.limit;

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.extended.EntityTypeExt;
import com.jcwhatever.nucleus.utils.extended.EntityTypeExt.EntityProperty;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.Lang;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="limit",
        command="reset",
        staticParams = { "entityType" },
        description="Remove the limit on the number of mobs of an entity type that can be " +
                "spawned in the selected arena.",

        paramDescriptions = {
                "entityType= The EntityType name."})

public class ResetSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED = "PVMobs extension is not installed in arena '{0}'.";
    @Localizable static final String _SUCCESS = "Limit removed for entity type '{0}' in arena '{1}'.";

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

        EntityTypeExt type = args.getEnum("entityType",
                EntityTypeExt.class, EntityTypeExt.getMatching(EntityProperty.ALIVE));

        extension.removeMobLimit(type.getType());

        tellSuccess(sender, Lang.get(_SUCCESS, type.name(), arena.getName()));
    }
}
