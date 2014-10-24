package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.columns;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="lb",
        command={ "columns" },
        description="Manage leaderboard columns.")

public class ColumnsCommand extends AbstractPVCommand {

    public ColumnsCommand() {
        super();

        registerSubCommand(InfoSubCommand.class);
        registerSubCommand(SignSubCommand.class);

    }
}
