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


package com.jcwhatever.pvs.modules.chests.commands.items;

import com.jcwhatever.nucleus.collections.WeightedArrayList.WeightedIterator;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.nucleus.managed.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.chests.ChestExtension;
import com.jcwhatever.pvs.modules.chests.Lang;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandInfo(
        parent="items",
        command="list",
        staticParams={"page=1"},
        description="List items available in the selected arenas chests.",

        paramDescriptions = {
                "page= {PAGE}"})

public class ListSubCommand extends AbstractPVCommand {

    @Localizable static final String _PAGINATOR_TITLE =
            "Available Chest Items";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null) {
            return; // finish
        }

        int page = args.getInteger("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        WeightedIterator<ItemStack> iterator = extension.getItemSettings().getItems().weightedIterator();

        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            pagin.add(iterator.weight() + "w, " + ItemStackUtils.serialize(stack));
        }

        pagin.show(sender, page, FormatTemplate.RAW);
    }
}
