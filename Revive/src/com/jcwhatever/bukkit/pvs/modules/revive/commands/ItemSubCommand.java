package com.jcwhatever.bukkit.pvs.modules.revive.commands;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.items.ItemStackSerializer.SerializerOutputType;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.modules.revive.ReviveExtension;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@ICommandInfo(
        parent="revive",
        command="item",
        staticParams={"itemStack=info"},
        usage="/{plugin-command} revive item [itemStack]",
        description="Set or view the item or items that can be used to revive a downed player in the selected arena.")

public class ItemSubCommand extends AbstractPVCommand {

    @Localizable static final String _EXTENSION_NOT_FOUND = "PVRevive extension not installed in arena '{0}'.";
    @Localizable static final String _CURRENT = "Current revival items: {0}";
    @Localizable static final String _CHANGED = "Revival items in arena '{0}' set to:";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        ReviveExtension extension = arena.getExtensionManager().get(ReviveExtension.class);
        if (extension == null) {
            tellError(sender, Lang.get(_EXTENSION_NOT_FOUND, arena.getName()));
            return; //finish
        }

        if (args.getString("itemStack").equals("info")) {

            ItemStack[] revivalItems = extension.getRevivalItems();

            tell(sender, Lang.get(_CURRENT, ItemStackHelper.serializeToString(revivalItems, SerializerOutputType.COLOR)));
        }
        else {

            ItemStack[] revivalItems = args.getItemStack(sender, "itemStack");

            extension.setRevivalItems(revivalItems);

            tellSuccess(sender, Lang.get(_CHANGED, arena.getName()));
            tell(sender, ItemStackHelper.serializeToString(revivalItems, SerializerOutputType.COLOR));
        }
    }
}

