package com.jcwhatever.bukkit.pvs.modules.regions.commands.settings;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="regions",
        command={ "settings" },
        description="Manage special arena region settings.")

public class SettingsCommand extends AbstractPVCommand {

    public SettingsCommand() {
        super();

        registerSubCommand(ClearSubCommand.class);
        registerSubCommand(InfoSubCommand.class);
        registerSubCommand(SetSubCommand.class);
    }
}
