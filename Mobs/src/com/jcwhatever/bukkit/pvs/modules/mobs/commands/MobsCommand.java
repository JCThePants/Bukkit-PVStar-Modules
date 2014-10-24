package com.jcwhatever.bukkit.pvs.modules.mobs.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.commands.paths.PathsCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.commands.settings.SettingsCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.commands.spawner.SpawnerCommand;

@ICommandInfo(
        command="mobs",
        description="Manage mob settings.")

public class MobsCommand extends AbstractPVCommand {

    public MobsCommand() {
        super();

        registerSubCommand(PathsCommand.class);
        registerSubCommand(SettingsCommand.class);
        registerSubCommand(SpawnerCommand.class);
    }
}
