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
        command="health",
        staticParams={"amount=info"},
        usage="/{plugin-command} revive health [amount]",
        description="Set or view the amount of health a player has after being revived in the selected arena. Value range is from 0 to 20.")

public class HealthSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "PVRevive extension not installed in arena '{0}'.";
    @Localizable static final String _CURRENT = "Current revive health in arena '{0}' is {1}.";
    @Localizable static final String _CHANGED = "Changed revive health in arena '{0}' to {1}.";

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

        if (args.getString("amount").equals("info")) {

            int health = extension.getReviveHealth();

            tell(sender, Lang.get(_CURRENT, arena.getName(), health));
        }
        else {

            int health = Math.max(1, args.getInt("amount"));

            extension.setReviveHealth(health);

            tellSuccess(sender, Lang.get(_CHANGED, arena.getName(), health));
        }
    }
}
