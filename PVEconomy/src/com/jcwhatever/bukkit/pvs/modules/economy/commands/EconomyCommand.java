package com.jcwhatever.bukkit.pvs.modules.economy.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        command="economy",
        description="Manage economy settings.")

public class EconomyCommand extends AbstractPVCommand {

    public EconomyCommand() {
        super();

        registerSubCommand(DeathSubCommand.class);
        registerSubCommand(KillSubCommand.class);
        registerSubCommand(LoseSubCommand.class);
        registerSubCommand(ParticipantSubCommand.class);
    }
}
