package com.jcwhatever.bukkit.pvs.modules.mobs.commands.spawner;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.SpawnerManager;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="spawner",
        command="set",
        staticParams = { "spawnerName" },
        usage="/{plugin-command} spawner set <spawnerName>",
        description="Set the mob spawner to use in the selected arena.")

public class SetSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED = "PVMobs extension is not installed in arena '{0}'.";
    @Localizable static final String _SPAWNER_NOT_FOUND = "A spawner named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Spawner in arena '{0}' set to '{1}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String spawnerName = args.getString("spawnerName");

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        MobArenaExtension extension = arena.getExtensionManager().get(MobArenaExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_INSTALLED, arena.getName()));
            return; // finish
        }

        Class<? extends ISpawner> spawnerClass = SpawnerManager.getSpawnerClass(spawnerName);
        if (spawnerClass == null) {
            tellError(sender, Lang.get(_SPAWNER_NOT_FOUND, spawnerName));
            return; // finish
        }

        extension.setSpawner(spawnerName);

        tellSuccess(sender, Lang.get(_SUCCESS, arena.getName(), spawnerName));
    }
}
