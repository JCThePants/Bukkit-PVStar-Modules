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
import com.jcwhatever.bukkit.pvs.modules.deathdrops.commands.TransferType;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="items",
        command="transfer",
        staticParams = { "specificity", "transferType=info"},
        usage="/{plugin-command} {command} items transfer <specificity> [transferType|clear]",
        description="View or set how dropped items are transferred to a player in the selected arena.")

public class TransferSubCommand extends AbstractDropsCommand {

    @Localizable static final String _INFO_DIRECT = "Items are transferred directly to the player in arena '{0}'.";
    @Localizable static final String _INFO_DROP = "Items are dropped in arena '{0}'.";
    @Localizable static final String _SET_DIRECT = "Items transfer type changed to DIRECT in arena '{0}'.";
    @Localizable static final String _SET_DROP = "Item transfer type changed to DROP in arena '{0}'.";
    @Localizable static final String _CLEAR = "Value cleared for specificity '{0}' in arena '{1}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "transferType"));
        if (arena == null)
            return; // finished

        DeathDropsExtension extension = getExtension(sender, arena, DeathDropsExtension.class);
        if (extension == null)
            return; // finished

        String specificity = args.getString("specificity");

        DropSettings settings = getDropSettings(specificity, extension);

        if (args.getString("transferType").equals("info")) {

            boolean isDirect = settings.isDirectItemTransfer();

            if (isDirect)
                tell(sender, Lang.get(_INFO_DIRECT, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_DROP, arena.getName()));
        }
        else if (args.getString("transferType").equalsIgnoreCase("clear")) {

            settings.clearDirectItemTransfer();
            tellSuccess(sender, Lang.get(_CLEAR, specificity, arena.getName()));
        }
        else {

            TransferType type = args.getEnum("transferType", TransferType.class);

            settings.setDirectItemTransfer(type == TransferType.DIRECT);

            if (type == TransferType.DIRECT)
                tellSuccess(sender, Lang.get(_SET_DIRECT, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_DROP, arena.getName()));
        }
    }
}
