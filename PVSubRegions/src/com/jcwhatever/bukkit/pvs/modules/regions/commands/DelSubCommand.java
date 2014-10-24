package com.jcwhatever.bukkit.pvs.modules.regions.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.regions.RegionManager;
import com.jcwhatever.bukkit.pvs.modules.regions.SubRegionsModule;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="regions",
        command="del",
        staticParams={ "regionName" },
        usage="/{plugin-command} {command} del <regionName>",
        description="Removes a sub region from the selected arena.")

public class DelSubCommand extends AbstractRegionCommand {

    @Localizable static final String _FAILED = "Failed to remove sub region.";
    @Localizable static final String _SUCCESS = "Sub region named '{0}' was removed from arena '{1}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        String regionName = args.getName("regionName");

        RegionManager manager = SubRegionsModule.getInstance().getManager(arena);

        AbstractPVRegion region = getRegion(sender, arena, regionName);
        if (region == null)
            return; // finish

        if (manager.removeRegion(regionName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, regionName, arena.getName()));
    }

}
