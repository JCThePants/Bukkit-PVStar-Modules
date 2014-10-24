package com.jcwhatever.bukkit.pvs.modules.revive.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="revive",
        command="time",
        staticParams={"seconds=info"},
        usage="/{plugin-command} revive time [seconds]",
        description="Set or view the amount of time in seconds a player is downed before dying in the selected arena.")

public class TimeSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "PVRevive extension not installed in arena '{0}'.";
    @Localizable static final String _CURRENT = "Current revive time in arena '{0}' is {1} seconds.";
    @Localizable static final String _CHANGED = "Changed revive time in arena '{0}' to {1} seconds.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        ReviveExtension extension = arena.getExtensionManager().get(ReviveExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; //finish
        }

        if (args.getString("seconds").equals("info")) {

            int seconds = extension.getTimeToReviveSeconds();

            tell(sender, Lang.get(_CURRENT, arena.getName(), seconds));
        }
        else {

            int seconds = Math.max(1, args.getInt("seconds"));

            extension.setTimeToReviveSeconds(seconds);

            tellSuccess(sender, Lang.get(_CHANGED, arena.getName(), seconds));
        }
    }
}

