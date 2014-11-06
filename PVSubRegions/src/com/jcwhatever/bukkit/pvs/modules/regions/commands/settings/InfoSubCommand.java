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


package com.jcwhatever.bukkit.pvs.modules.regions.commands.settings;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.modules.regions.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinition;
import com.jcwhatever.bukkit.generic.storage.settings.SettingDefinitions;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.regions.commands.AbstractRegionCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="settings",
        command="info",
        staticParams={ "regionName", "page=1" },
        usage="/{plugin-command} {command} settings info <regionName> [page]",
        description="Displays settings of the specified sub region in the currently selected arena.")

public class InfoSubCommand extends AbstractRegionCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Sub Region '{0}'.";
    @Localizable static final String _LABEL_ENABLED = "ENABLED";
    @Localizable static final String _LABEL_TYPE = "TYPE";
    @Localizable static final String _LABEL_DEFINED = "DEFINED";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        String regionName = args.getName("regionName");
        int page = args.getInteger("page");

        AbstractPVRegion region = getRegion(sender, arena, regionName);
        if (region == null)
            return; // finish

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE, region.getName()));

        pagin.add(Lang.get(_LABEL_ENABLED), region.isEnabled());
        pagin.add(Lang.get(_LABEL_TYPE), region.getTypeName());
        pagin.add(Lang.get(_LABEL_DEFINED), region.isDefined());

        if (region.isDefined()) {
            Location p1 = region.getP1();
            Location p2 = region.getP2();

            pagin.add("P1", TextUtils.formatLocation(p1, true));
            pagin.add("P2", TextUtils.formatLocation(p2, true));
        }

        SettingDefinitions defs = region.getSettingsManager().getPossibleSettings();

        if (defs.size() > 0) {

            for (SettingDefinition def : defs.values()) {
                Object value = region.getSettingsManager().get(def.getSettingName(), true);

                pagin.add(def.getSettingName(), value);
            }
        }

        pagin.show(sender, page, FormatTemplate.DEFINITION);
    }

}