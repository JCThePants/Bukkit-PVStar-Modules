package com.jcwhatever.bukkit.pvs.modules.mobs.commands.spawner;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="mobs",
        command="spawner",
        description="Manage arena mob spawner.")

public class SpawnerCommand extends AbstractPVCommand {

    public SpawnerCommand() {
        super();

        registerSubCommand(SetSubCommand.class);
        registerSubCommand(TypesSubCommand.class);
    }
}