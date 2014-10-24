package com.jcwhatever.bukkit.pvs.modules.mobs.commands.paths;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="paths",
        command="cache",
        usage="/{plugin-command} {command} cache",
        description="Cache mob paths in the currently selected arena.")

public class CacheSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED = "PVMobs extension is not installed in arena '{0}'.";
    @Localizable static final String _SUCCESS = "Mob paths in arena '{0}' cached.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        MobArenaExtension extension = arena.getExtensionManager().get(MobArenaExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_INSTALLED, arena.getName()));
            return; // finish
        }

        extension.getGroupGenerator().getPathCache().cachePaths(16, 18);

        tellSuccess(sender, Lang.get(_SUCCESS, arena.getName()));
    }
}

