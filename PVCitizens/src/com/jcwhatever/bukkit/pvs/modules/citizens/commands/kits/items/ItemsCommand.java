package com.jcwhatever.bukkit.pvs.modules.citizens.commands.kits.items;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="kits",
        command="items",
        description="Manage NPC kits items.")

public class ItemsCommand extends AbstractPVCommand {

    public ItemsCommand() {
        super();

        registerSubCommand(AddSubCommand.class);
        registerSubCommand(DelSubCommand.class);
        registerSubCommand(ListSubCommand.class);

    }
}
