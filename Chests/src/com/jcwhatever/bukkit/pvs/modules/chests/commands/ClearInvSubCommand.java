package com.jcwhatever.bukkit.pvs.modules.chests.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.chests.ChestExtension;
import com.jcwhatever.bukkit.pvs.modules.chests.ChestExtension.ClearChestRestore;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="chests",
        command="clearinv",
        usage="/{plugin-command} chests clearinv",
        description="Clear inventory of all scanned chests in the selected arena. Does not change config.")

public class ClearInvSubCommand extends AbstractPVCommand {

    @Localizable static final String _SUCCESS = "Inventory of known chests have been cleared for arena '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNNING);
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        extension.clearChestContents(ClearChestRestore.NONE);

        tellSuccess(sender, Lang.get(_SUCCESS, arena.getName()));
    }
}