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


package com.jcwhatever.bukkit.pvs.modules.regions.commands;

import com.jcwhatever.bukkit.generic.commands.CommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.CommandException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.regions.selection.IRegionSelection;
import com.jcwhatever.bukkit.generic.utils.text.TextUtils;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.regions.Lang;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionManager;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(
        parent="regions",
        command="add",
        staticParams={ "regionName", "regionType" },
        description="Adds a new sub region to the currently selected arena using " +
                "your current region selection.",

        paramDescriptions = {
                "regionName= The name of the sub region. {NAME16}",
                "regionType= The name of the sub region type the region will be."})

public class AddSubCommand extends AbstractRegionCommand {

    @Localizable static final String _INVALID_TYPE = "'{0}' is not a valid sub region type. Valid types are:";
    @Localizable static final String _REGION_ALREADY_EXISTS = "A sub region named '{0}' already exists in arena '{1}'.";
    @Localizable static final String _FAILED = "Failed to add sub region.";
    @Localizable static final String _SUCCESS = "Sub region named '{0}' of type '{1}' was added to arena '{2}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException{

        CommandException.assertNotConsole(this, sender);

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        String regionName = args.getName("regionName");
        String regionType = args.getString("regionType");

        Player p = (Player)sender;

        IRegionSelection sel = getRegionSelection(p);
        if (sel == null)
            return; // finish

        if (!SubRegionsModule.getModule().getTypesManager().hasType(regionType)) {

            tellError(sender, Lang.get(_INVALID_TYPE, regionType));

            List<String> typeNames = SubRegionsModule.getModule().getTypesManager().getRegionTypeNames();

            tell(sender, TextUtils.concat(typeNames, ", "));
            return; // finish
        }

        RegionManager manager = SubRegionsModule.getModule().getManager(arena);

        AbstractPVRegion region = manager.getRegion(regionName);
        if (region != null) {
            tellError(sender, Lang.get(_REGION_ALREADY_EXISTS, regionName));
            return; // finish
        }

        region = manager.addRegion(regionName, regionType, sel.getP1(), sel.getP2());
        if (region == null) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, regionName, regionType, arena.getName()));
    }

}

