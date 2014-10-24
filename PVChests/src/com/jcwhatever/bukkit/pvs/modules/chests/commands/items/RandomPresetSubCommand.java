package com.jcwhatever.bukkit.pvs.modules.chests.commands.items;

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
        parent="items",
        command="randompreset",
        staticParams={"on|off|info=info"},
        usage="/{plugin-command} chests items randompreset <on|off>",
        description="Turn randomizing of preset chest contents on or off in the selected arena.")

public class RandomPresetSubCommand extends AbstractPVCommand {

    @Localizable static final String _INFO_ON = "Preset chest contents are randomized in arena '{0}'.";
    @Localizable static final String _INFO_OFF = "Preset chest contents are NOT randomized in arena '{0}'.";
    @Localizable static final String _SET_ON = "Preset chest contents in arena '{0}' changed to ON.";
    @Localizable static final String _SET_OFF = "Preset chest contents in arena '{0}' changed to OFF.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws InvalidValueException {

        Arena arena = getSelectedArena(sender, ArenaReturned.getInfoToggled(args, "on|off|info"));
        if (arena == null)
            return; // finish

        ChestExtension extension = getExtension(sender, arena, ChestExtension.class);
        if (extension == null)
            return; // finish

        if (args.getString("on|off|info").equals("info")) {

            boolean isRandomized = extension.getItemSettings().isPresetContentsRandomized();

            if (isRandomized)
                tell(sender, Lang.get(_INFO_ON, arena.getName()));
            else
                tell(sender, Lang.get(_INFO_OFF, arena.getName()));
        }
        else {

            boolean isRandomized = args.getBoolean("on|off|info");

            extension.getItemSettings().setPresetContentsRandomized(isRandomized);

            if (isRandomized)
                tellSuccess(sender, Lang.get(_SET_ON, arena.getName()));
            else
                tellSuccess(sender, Lang.get(_SET_OFF, arena.getName()));

        }
    }
}
