package com.jcwhatever.bukkit.pvs.modules.chests.commands.items;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="chests",
        command="items",
        description="Manage arena chest content items.")

public class ItemsCommand extends AbstractPVCommand {

    public ItemsCommand() {
        super();

        registerSubCommand(AddDefaultSubCommand.class);
        registerSubCommand(ClearSubCommand.class);
        registerSubCommand(ListSubCommand.class);
        registerSubCommand(MaxSubCommand.class);
        registerSubCommand(RandomPresetSubCommand.class);
    }
}
