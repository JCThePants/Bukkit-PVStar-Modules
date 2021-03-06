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


package com.jcwhatever.pvs.modules.revive.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.revive.Lang;
import com.jcwhatever.pvs.modules.revive.ReviveExtension;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandInfo(
        parent="revive",
        command="item",
        staticParams={"itemStack=info"},
        usage="/{plugin-command} revive item [itemStack]",
        description="Set or view the item or items that can be used to revive a downed " +
                "player in the selected arena.",

        paramDescriptions = {
                "itemStack= The item or items. {ITEM_STACK}"})

public class ItemSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND =
            "PVRevive extension not installed in arena '{0: arena name}'.";

    @Localizable static final String _CURRENT =
            "Current revival items: {0: item list}";

    @Localizable static final String _CHANGED =
            "Revival items in arena '{0: arena name}' set to:";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        ReviveExtension extension = arena.getExtensions().get(ReviveExtension.class);
        if (extension == null)
            throw new CommandException(Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));

        if (args.getString("itemStack").equals("info")) {

            ItemStack[] revivalItems = extension.getRevivalItems();

            tell(sender, Lang.get(_CURRENT, ItemStackUtils.serialize(revivalItems)));
        }
        else {

            ItemStack[] revivalItems = args.getItemStack(sender, "itemStack");

            extension.setRevivalItems(revivalItems);

            tellSuccess(sender, Lang.get(_CHANGED, arena.getName()));
            tell(sender, ItemStackUtils.serialize(revivalItems));
        }
    }
}

