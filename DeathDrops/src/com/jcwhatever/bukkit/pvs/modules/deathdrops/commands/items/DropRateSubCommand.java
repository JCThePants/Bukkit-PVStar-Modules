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
        command="droprate",
        staticParams = { "specificity", "percent=info"},
        usage="/{plugin-command} {command} items droprate <specificity> [percent|clear]",
        description="Set or view item drop rates in the selected arena.")

public class DropRateSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO = "Item drop rate in arena '{0}' is {1}.";
    @Localizable static final String _SET = "Item drop rate in arena '{0}' changed to {1}.";
    @Localizable static final String _CLEAR = "Value cleared for specificity '{0}' in arena '{1}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "percent"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("percent").equals("info")) {

            double dropRate = settings.getItemDropRate();

            tell(sender, Lang.get(_INFO, arena.getName(), dropRate));
        }
        else if (args.getString("percent").equalsIgnoreCase("clear")) {

            settings.clearItemDropRate();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {
            double dropRate = args.getDouble("percent");

            settings.setItemDropRate(dropRate);

            tellSuccess(sender, Lang.get(_SET, arena.getName(), dropRate));
        }
    }
}

