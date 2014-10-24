package com.jcwhatever.bukkit.pvs.modules.leaderboards.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.leaderboards.commands.columns.ColumnsCommand;

@ICommandInfo(
        command={ "lb", "leaderboards", "pvleaderboards" },
        description="Manage leaderboards.")

public class LBCommand extends AbstractPVCommand {

    public LBCommand() {
        super();

        registerSubCommand(ColumnsCommand.class);

        registerSubCommand(AddSubCommand.class);
        registerSubCommand(DelSubCommand.class);
        registerSubCommand(DisableSubCommand.class);
        registerSubCommand(EnableSubCommand.class);
        registerSubCommand(FormatSubCommand.class);
        registerSubCommand(ListSubCommand.class);
        registerSubCommand(SetAnchorSubCommand.class);
        registerSubCommand(SetArenasSubCommand.class);
        registerSubCommand(UpdateSubCommand.class);
    }
}
