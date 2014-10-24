package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.exp.ExpCommand;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.items.ItemsCommand;

@ICommandInfo(
        command="drops",
        description="Manage death drop settings.")

public class DropsCommand extends AbstractPVCommand {

    public DropsCommand() {
        super();

        registerSubCommand(ItemsCommand.class);
        registerSubCommand(ExpCommand.class);
        registerSubCommand(KeepItemsSubCommand.class);
        registerSubCommand(SpecTypesSubCommand.class);
    }
}
