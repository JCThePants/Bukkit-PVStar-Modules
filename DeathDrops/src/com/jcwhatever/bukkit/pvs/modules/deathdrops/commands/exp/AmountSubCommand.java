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
        parent="exp",
        command="amount",
        staticParams = { "specificity", "number=info"},
        usage="/{plugin-command} {command} exp amount <specificity> [number|clear]",
        description="View or set the amount of Exp dropped in the selected arena at the specified specificity.")

public class AmountSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO = "The amount of Exp dropped in arena '{0}' is {1}.";
    @Localizable static final String _SET = "The amount of Exp dropped in arena '{0}' changed to {1}.";
    @Localizable static final String _CLEAR = "Amount cleared for specificity '{0}' in arena {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "number"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("number").equals("info")) {

            int amount = settings.getExpDropAmount();

            tell(sender, Lang.get(_INFO, arena.getName(), amount));
        }
        else if (args.getString("number").equalsIgnoreCase("clear")) {

            settings.clearExpDropAmount();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {

            int amount = args.getInt("number");

            settings.setExpDropAmount(amount);

            tellSuccess(sender, Lang.get(_SET, arena.getName(), amount));
        }
    }
}
