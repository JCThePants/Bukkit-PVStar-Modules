package com.jcwhatever.pvs.modules.randombox.commands.items;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.randombox.ChestItems;
import com.jcwhatever.pvs.modules.randombox.Lang;
import com.jcwhatever.pvs.modules.randombox.RandomBoxExtension;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@CommandInfo(
        command="del",
        staticParams = { "items" },
        description="Remove items from the random boxes in the selected arena.")

public class DelSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _SUCCESS =
            "Random box items removed from arena '{0: arena name}'.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, ArenaReturned.NOT_RUNNING);
        if (arena == null)
            return; // finish

        RandomBoxExtension extension = getExtension(sender, arena, RandomBoxExtension.class);
        if (extension == null) {
            return; // finish
        }

        ItemStack[] items = args.getItemStack(sender, "item");

        ChestItems chestItems = extension.getItems();

        for (ItemStack item : items) {
            chestItems.remove(item);
        }

        tellSuccess(sender, Lang.get(_SUCCESS, arena.getName()));
    }
}

