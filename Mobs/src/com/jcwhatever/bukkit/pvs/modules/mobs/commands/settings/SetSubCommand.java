package com.jcwhatever.bukkit.pvs.modules.mobs.commands.settings;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidCommandSenderException;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.storage.settings.ISettingsManager;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.mobs.MobArenaExtension;
import com.jcwhatever.bukkit.pvs.modules.mobs.spawners.ISpawner;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="settings",
        command="set",
        staticParams={ "property", "value"},
        usage="/{plugin-command} mobs settings set <property> <value>",
        description="Change setting in the mob spawner in the currently selected arena.")

public class SetSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_INSTALLED = "PVMobs extension is not installed in arena '{0}'.";
    @Localizable static final String _SPAWNER_NOT_FOUND = "Arena '{0}' does not have a mob spawner.";

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws InvalidValueException, InvalidCommandSenderException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        MobArenaExtension extension = arena.getExtensionManager().get(MobArenaExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_INSTALLED, arena.getName()));
            return; // finish
        }


        ISpawner spawner = extension.getSpawner();
        if (spawner == null) {
            tellError(sender, Lang.get(_SPAWNER_NOT_FOUND, arena.getName()));
            return; // finish
        }

        ISettingsManager settings = spawner.getSettings().getManager();

        setSetting(sender, settings, args, "property", "value");
    }
}