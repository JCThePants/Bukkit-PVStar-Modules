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

import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.language.Localizable;
import com.jcwhatever.nucleus.messaging.ChatPaginator;
import com.jcwhatever.nucleus.utils.text.TextUtils.FormatTemplate;
import com.jcwhatever.pvs.api.utils.Msg;
import com.jcwhatever.pvs.modules.regions.Lang;
import com.jcwhatever.pvs.modules.regions.RegionTypeInfo;
import com.jcwhatever.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.pvs.modules.regions.regions.AbstractPVRegion;

import org.bukkit.command.CommandSender;

import java.util.List;

@CommandInfo(
        parent="regions",
        command="types",
        staticParams={ "page=1" },
        description="List available sub region types.",

        paramDescriptions = {
                "page= {PAGE}"})

public class TypesSubCommand extends AbstractRegionCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Sub Regions Types";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        int page = args.getInteger("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        List<Class<? extends AbstractPVRegion>> types = SubRegionsModule.getModule().getTypesManager().getRegionTypes();

        for (Class<? extends AbstractPVRegion> type : types) {

            RegionTypeInfo info = type.getAnnotation(RegionTypeInfo.class);
            if (info == null)
                continue;

            pagin.add(info.name(), info.description());
        }

        pagin.show(sender, page, FormatTemplate.LIST_ITEM_DESCRIPTION);
    }
}
