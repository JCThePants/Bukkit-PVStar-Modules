package com.jcwhatever.bukkit.pvs.modules.chests.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.chests.commands.items.ItemsCommand;

@ICommandInfo(
        command="chests",
        description="Manage arena chests.")

public class ChestsCommand extends AbstractPVCommand {

    public ChestsCommand() {
        super();

        registerSubCommand(ItemsCommand.class);

        registerSubCommand(ClearInvSubCommand.class);
        registerSubCommand(MaxSubCommand.class);
        registerSubCommand(ScanSubCommand.class);
        registerSubCommand(RandomSubCommand.class);

    }
}
