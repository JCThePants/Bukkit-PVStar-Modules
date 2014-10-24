package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.modules.deathdrops.DeathDropsExtension;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="drops",
        command="keepitems",
        staticParams = { "on|off|info=info"},
        usage="/{plugin-command} {command} keepitems [on|off]",
        description="View or set if players keep their items when they are killed in the selected arena.")

public class KeepItemsSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO_KEEP = "Players keep their items when they die and respawn in arena '{0}'.";
    @Localizable static final String _INFO_NOT_KEEP = "Players lose their items when they die in arena '{0}'.";
    @Localizable static final String _SET_KEEP = "Players will now keep their items when they die in arena '{0}'.";
    @Localizable static final String _SET_NOT_KEEP = "Players will now lose their items when they die in arena '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        if (args.getString("on|off|info").equals("info")) {

            boolean canKeepItems = extension.canKeepItemsOnDeath();

            if (canKeepItems)
                tell(sender, Lang.get(_INFO_KEEP, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_NOT_KEEP, arena.getName()));
        }
        else {

            boolean canKeepItems = args.getBoolean("on|off|info");

            extension.setKeepItemsOnDeath(canKeepItems);

            if (canKeepItems)
                tellSuccess(sender, Lang.get(_SET_KEEP, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_NOT_KEEP, arena.getName()));
        }
    }
}

