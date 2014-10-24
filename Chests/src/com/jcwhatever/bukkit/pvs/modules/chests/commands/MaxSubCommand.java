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
        command="max",
        staticParams={"number=info"},
        usage="/{plugin-command} chests max [number]",
        description="Set max number of chests in selected arena when chests are randomized. -1 to disregard setting.")

public class MaxSubCommand extends AbstractPVCommand {

    @Localizable static final String _INFO = "Max random chests in arena '{0}' is {1}.";
    @Localizable static final String _SET = "Max random chests in arena '{0}' changed to {1}.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "number"));
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        if (args.getString("number").equals("info")) {

            int max = extension.getChestSettings().getMaxChests();

            tell(sender, Lang.get(_INFO, arena.getName(), max));

        }
        else {

            int max = args.getInt("number");

            tellSuccess(sender, Lang.get(_SET, arena.getName(), max));

        }
    }
}
