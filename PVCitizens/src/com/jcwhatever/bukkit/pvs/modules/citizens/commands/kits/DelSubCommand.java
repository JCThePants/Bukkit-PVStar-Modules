package com.jcwhatever.bukkit.pvs.modules.citizens.commands.kits;

import com.jcwhatever.bukkit.generic.commands.AbstractCommand;
import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.inventory.Kit;
import com.jcwhatever.bukkit.generic.inventory.KitManager;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.pvs.modules.citizens.CitizensModule;
import org.bukkit.command.CommandSender;

@ICommandInfo(
        parent="kits",
        command="del",
        staticParams={ "kitName" },
        usage="/{plugin-command} {command} del <kitName>",
        description="Remove an NPC kit.")

public class DelSubCommand extends AbstractCommand {

    @Localizable static final String _KIT_NOT_FOUND = "An NPC kit named '{0}' was not found.";
    @Localizable static final String _FAILED = "Failed to remove NPC kit.";
    @Localizable static final String _SUCCESS = "NPC kit '{0}' removed.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String kitName = args.getName("kitName");

        KitManager manager = CitizensModule.getInstance().getKitManager();

        Kit kit = manager.getKitByName(kitName);
        if (kit == null) {
            tellError(sender, Lang.get(_KIT_NOT_FOUND, kitName));
            return; // finish
        }

        if (!manager.deleteKit(kitName)) {
            tellError(sender, Lang.get(_FAILED));
            return; // finish
        }

        tellSuccess(sender, Lang.get(_SUCCESS, kit.getName()));
    }
}
