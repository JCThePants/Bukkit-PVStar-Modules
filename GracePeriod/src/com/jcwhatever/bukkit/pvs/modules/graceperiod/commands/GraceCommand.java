package com.jcwhatever.bukkit.pvs.modules.graceperiod.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.graceperiod.GracePeriodExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        command={ "grace" },
        staticParams={"seconds=info"},
        usage="/{plugin-command} {command} [seconds]",
        description="Set or view the pvp grace period time in seconds for the selected arena.")

public class GraceCommand extends AbstractPVCommand {

    @Localizable static final String _GRACE_SECONDS_INFO = "Grace period seconds in arena '{0}' is set to {1}.";
    @Localizable static final String _GRACE_SECONDS_SET = "Grace period seconds in arena '{0}' changed to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "seconds"));
        if (arena == null)
            return; // finished

        GracePeriodExtension extension = getExtension(sender, arena, GracePeriodExtension.class);
        if (extension == null)
            return; // finished

        if (args.getString("seconds").equals("info")) {

            int seconds = extension.getGracePeriodSeconds();
            tell(sender, Lang.get(_GRACE_SECONDS_INFO, arena.getName(), seconds));
        } else {

            int seconds = args.getInt("seconds");

            extension.setGracePeriodSeconds(seconds);

            tellSuccess(sender, Lang.get(_GRACE_SECONDS_SET, arena.getName(), seconds));
        }
    }
}
