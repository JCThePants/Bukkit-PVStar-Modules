package com.jcwhatever.bukkit.pvs.modules.chests.commands.items;

import com.jcwhatever.bukkit.generic.commands.ICommandInfo;
import com.jcwhatever.bukkit.generic.commands.arguments.CommandArguments;
import com.jcwhatever.bukkit.generic.commands.exceptions.InvalidValueException;
import com.jcwhatever.bukkit.generic.items.ItemStackHelper;
import com.jcwhatever.bukkit.generic.items.ItemStackSerializer.SerializerOutputType;
import com.jcwhatever.bukkit.generic.messaging.ChatPaginator;
import com.jcwhatever.bukkit.pvs.api.utils.Lang;
import com.jcwhatever.bukkit.generic.language.Localizable;
import com.jcwhatever.bukkit.generic.utils.TextUtils.FormatTemplate;
import com.jcwhatever.bukkit.generic.collections.Weighted;
import com.jcwhatever.bukkit.pvs.api.arena.Arena;
import com.jcwhatever.bukkit.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.bukkit.pvs.api.utils.Msg;
import com.jcwhatever.bukkit.pvs.modules.chests.ChestExtension;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

@ICommandInfo(
        parent="items",
        command="list",
        staticParams={"page=1"},
        usage="/{plugin-command} chests items list [page]",
        description="List items available in the selected arenas chests.")

public class ListSubCommand extends AbstractPVCommand {

    @Localizable static final String _PAGINATOR_TITLE = "Available Chest Items";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null) {
            return; // finish
        }

        int page = args.getInt("page");

        ChatPaginator pagin = Msg.getPaginator(Lang.get(_PAGINATOR_TITLE));

        for (Weighted<ItemStack> weighted : extension.getItemSettings().getItems()) {
            ItemStack stack = weighted.getItem();
            pagin.add(ItemStackHelper.serializeToString(stack, SerializerOutputType.COLOR));
        }

        pagin.show(sender, page, FormatTemplate.RAW);
    }
}
