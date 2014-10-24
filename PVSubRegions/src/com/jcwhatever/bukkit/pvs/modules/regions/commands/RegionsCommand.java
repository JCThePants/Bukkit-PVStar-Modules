package com.jcwhatever.bukkit.pvs.modules.regions.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.regions.commands.settings.SettingsCommand;

@ICommandInfo(
        command={"regions", "subregions", "pvregions" },
        description="Manage special arena regions.")

public class RegionsCommand extends AbstractPVCommand {

    public RegionsCommand() {
        super();

        registerSubCommand(SettingsCommand.class);
        registerSubCommand(AddSubCommand.class);
        registerSubCommand(DelSubCommand.class);
        registerSubCommand(ListSubCommand.class);
        registerSubCommand(RedefineSubCommand.class);
        registerSubCommand(TypesSubCommand.class);
    }
}
