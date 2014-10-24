package com.jcwhatever.bukkit.pvs.modules.chests.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.chests.ChestExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="chests",
        command="random",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} chests random <on|off>",
        description="Set or view randomizing of chest availability setting in the selected arena.")

public class RandomSubCommand extends AbstractPVCommand {

    @Localizable static final String _INFO_ENABLED = "Chest randomizing in arena '{0}' is enabled.";
    @Localizable static final String _INFO_DISABLED = "Chest randomizing in arena '{0}' is disabled.";
    @Localizable static final String _SET_ENABLED = "Chest randomizing in arena '{0}' has been changed to Enabled.";
    @Localizable static final String _SET_DISABLED = "Chest randomizing in arena '{0}' has been changed to Disabled.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isEnabled = extension.getChestSettings().isChestsRandomized();

            if (isEnabled)
                tell(sender, Lang.get(_INFO_ENABLED, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_DISABLED, arena.getName()));

        }
        else {

            boolean isEnabled = args.getBoolean("on|off|info");

            if (isEnabled)
                tellSuccess(sender, Lang.get(_SET_ENABLED, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_DISABLED, arena.getName()));
        }

    }
}