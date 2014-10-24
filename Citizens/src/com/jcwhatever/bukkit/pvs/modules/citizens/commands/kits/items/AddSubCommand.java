package com.jcwhatever.bukkit.pvs.modules.citizens.commands.kits.items;

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
import org.bukkit.inventory.ItemStack;

@ICommandInfo(
        parent="items",
        command="add",
        staticParams={ "kitName", "items" },
        usage="/{plugin-command} {command} items add <kitName> <items>",
        description="Add items to the specified NPC kit.")

public class AddSubCommand extends AbstractCommand {

    @Localizable static final String _KIT_NOT_FOUND = "An NPC kit named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Added items to NPC kit '{0}'.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        String kitName = args.getName("kitName");
        ItemStack[] items = args.getItemStack(sender, "items");

        KitManager manager = CitizensModule.getInstance().getKitManager();

        Kit kit = manager.getKitByName(kitName);
        if (kit == null) {
            tellError(sender, Lang.get(_KIT_NOT_FOUND, kitName));
            return; // finish
        }

        kit.addItems(items);

        manager.saveKits();

        tellSuccess(sender, Lang.get(_SUCCESS, kit.getName()));
    }
}