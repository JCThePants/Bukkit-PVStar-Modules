package com.jcwhatever.bukkit.pvs.modules.revive.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        command="revive",
        description="Manage arena player revive settings.")

public class ReviveCommand extends AbstractPVCommand {

    public ReviveCommand() {
        super();

        registerSubCommand(HealthSubCommand.class);
        registerSubCommand(ItemSubCommand.class);
        registerSubCommand(TimeSubCommand.class);
    }
}
