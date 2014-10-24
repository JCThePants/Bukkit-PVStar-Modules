package com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.items;

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
        parent="items",
        command="random",
        staticParams = { "specificity", "on|off|clear|info=info"},
        usage="/{plugin-command} {command} items random <specificity> [on|off|clear]",
        description="View or set if specific item dropped is random, otherwise all items dropped.")

public class RandomSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO_ON = "Random item drop in arena '{0}' is on.";
    @Localizable static final String _INFO_OFF = "Random item drop in arena '{0}' is off.";
    @Localizable static final String _SET_ON = "Random item drop in arena '{0}' changed to ON.";
    @Localizable static final String _SET_OFF = "Random item drop in arena '{0}' changed to OFF.";
    @Localizable static final String _CLEAR = "Value cleared for specificity '{0}' in arena '{1}'.";

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

            boolean isRandom = settings.isRandomItemDrop();

            if (isRandom)
                tell(sender, Lang.get(_INFO_ON, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_OFF, arena.getName()));
        }
        else if (args.getString("on|off|clear|info").equalsIgnoreCase("clear")) {

            settings.clearRandomItemDrop();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {

            boolean isRandom = args.getBoolean("on|off|clear|info");

            settings.setRandomItemDrop(isRandom);

            if (isRandom)
                tellSuccess(sender, Lang.get(_SET_ON, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_OFF, arena.getName()));
        }
    }
}
