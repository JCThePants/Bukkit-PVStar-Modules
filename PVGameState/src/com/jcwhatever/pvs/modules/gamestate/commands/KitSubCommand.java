package com.jcwhatever.pvs.modules.gamestate.commands;

import com.jcwhatever.nucleus.managed.commands.CommandInfo;
import com.jcwhatever.nucleus.managed.commands.arguments.ICommandArguments;
import com.jcwhatever.nucleus.managed.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.managed.commands.mixins.IExecutableCommand;
import com.jcwhatever.nucleus.managed.language.Localizable;
import com.jcwhatever.pvs.api.arena.IArena;
import com.jcwhatever.pvs.api.commands.AbstractPVCommand;
import com.jcwhatever.pvs.modules.gamestate.GameStateExtension;
import com.jcwhatever.pvs.modules.gamestate.Lang;
import org.bukkit.command.CommandSender;

@CommandInfo(
        command="kit",
        staticParams = { "kitName="},
        description="Set or view the name of the initial player kit in the selected arena.",

        paramDescriptions = {
                "kitName= The name of the NucleusFramework kit to give players when the game starts."})

public class KitSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _INFO =
            "Initial kit in arena '{0: arena name}' is {1: kit name}.";

    @Localizable static final String _SET =
            "Initial kit in arena '{0: arena name}' changed to {1: kit name}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, AbstractPVCommand.ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finished

        GameStateExtension extension = getExtension(sender, arena, GameStateExtension.class);
        if (extension == null)
            return; // finished

        if (args.isDefaultValue("kitName")) {

            String kitName = extension.getKitName();
            if (kitName == null || kitName.isEmpty()) {
                kitName = "<none>";
            }

            tell(sender, Lang.get(_INFO, arena.getName(), kitName));
            return; // finished
        }

        String kitName = args.getString("kitName");

        extension.setKitName(kitName);

        tellSuccess(sender, Lang.get(_SET, arena.getName(), kitName));
    }
}
