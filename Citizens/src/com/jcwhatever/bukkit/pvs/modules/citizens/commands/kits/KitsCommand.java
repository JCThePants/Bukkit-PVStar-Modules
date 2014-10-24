package com.jcwhatever.bukkit.pvs.modules.citizens.commands.kits;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.citizens.commands.kits.items.ItemsCommand;

@ICommandInfo(
        parent="npc",
        command="kits",
        description="Manage NPC kits.")

public class KitsCommand extends AbstractPVCommand {

    public KitsCommand() {
        super();

        registerSubCommand(ItemsCommand.class);

        registerSubCommand(AddSubCommand.class);
        registerSubCommand(DelSubCommand.class);
        registerSubCommand(ListSubCommand.class);
    }
}

