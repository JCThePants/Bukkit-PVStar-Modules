package com.jcwhatever.bukkit.pvs.modules.regions.commands.settings;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.regions.commands.AbstractRegionCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.regions.AbstractPVRegion;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="settings",
        command="clear",
        staticParams={ "regionName", "property" },
        usage="/{plugin-command} {command} settings clear <regionName> <property> <value>",
        description="Clear a sub region property to default value in the selected arena.")

public class ClearSubCommand extends AbstractRegionCommand {

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        String regionName = args.getName("regionName");

        AbstractPVRegion region = getRegion(sender, arena, regionName);
        if (region == null)
            return; // finish

        ISettingsManager settings = region.getSettingsManager();

        clearSetting(sender, settings, args, "property");
    }
}
