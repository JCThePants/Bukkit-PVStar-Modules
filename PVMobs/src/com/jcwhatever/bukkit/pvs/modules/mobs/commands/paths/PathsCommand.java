package com.jcwhatever.bukkit.pvs.modules.mobs.commands.paths;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;

@ICommandInfo(
        parent="mobs",
        command="paths",
        description="Manage spawn path caching.")

public class PathsCommand extends AbstractPVCommand {

    public PathsCommand() {
        super();

        registerSubCommand(CacheSubCommand.class);
    }
}
