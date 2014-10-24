package com.jcwhatever.bukkit.pvs.modules.mobs.commands.settings;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="mobs",
        command="settings",
        description="Manage a mob spawners settings.")

public class SettingsCommand extends AbstractPVCommand {

    public SettingsCommand() {
        super();

        registerSubCommand(InfoSubCommand.class);
        registerSubCommand(ResetSubCommand.class);
        registerSubCommand(SetSubCommand.class);

    }
}