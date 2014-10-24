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
        command="scan",
        usage="/{plugin-command} chests scan",
        description="Scan for chests in the selected arena.")

public class ScanSubCommand extends AbstractPVCommand {

    @Localizable static final String _SCAN_START = "Scan starting...";
    @Localizable static final String _SCAN_FINISH = "Scan finished. {0} chests found.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        tell(sender, Lang.get(_SCAN_START));

        extension.getChestSettings().scanChests();

        tellSuccess(sender, Lang.get(_SCAN_FINISH, extension.getChestSettings().getTotalChests()));
    }
}
