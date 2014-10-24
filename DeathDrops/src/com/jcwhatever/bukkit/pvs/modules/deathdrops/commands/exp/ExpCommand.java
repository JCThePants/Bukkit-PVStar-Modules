package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.exp;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.DeathDropsExtension;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.DropSettings;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.AbstractDropsCommand;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="drops",
        command="exp",
        staticParams = { "specificity", "on|off|clear|info=info"},
        usage="/{plugin-command} {command} exp <specificity> [on|off|clear]",
        description="Enable or disable exp drops.")

public class ExpCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO_ON = "Exp drops in arena '{0}' are on.";
    @Localizable static final String _INFO_OFF = "Exp drops in arena '{0}' are off.";
    @Localizable static final String _SET_ON = "Exp drops in arena '{0}' changed to ON.";
    @Localizable static final String _SET_OFF = "Exp drops in arena '{0}' changed to {RED}OFF.";
    @Localizable static final String _CLEAR = "Value cleared for specificity '{0}' in arena '{1}'.";

    public ExpCommand() {
        super();

        registerSubCommand(AmountSubCommand.class);
        registerSubCommand(DropRateSubCommand.class);
        registerSubCommand(TransferSubCommand.class);
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|clear|info"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("on|off|clear|info").equals("info")) {

            boolean isEnabled = settings.isExpDropEnabled();

            if (isEnabled)
                tell(sender, Lang.get(_INFO_ON, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_OFF, arena.getName()));
        }
        else if (args.getString("on|off|clear|info").equalsIgnoreCase("clear")) {

            settings.clearExpDropEnabled();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {
            boolean isEnabled = args.getBoolean("on|off|clear|info");

            settings.setExpDropEnabled(isEnabled);

            if (isEnabled)
                tellSuccess(sender, Lang.get(_SET_ON, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_OFF, arena.getName()));
        }


    }

}
