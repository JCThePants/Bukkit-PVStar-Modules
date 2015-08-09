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
        command="explevels",
        staticParams = { "levels="},
        description="Set or view initial player Exp levels in the selected arena.",

        paramDescriptions = {
                "levels= The number of Exp levels the player starts the game with. "})

public class ExpLevelsSubCommand extends AbstractPVCommand implements IExecutableCommand {

    @Localizable static final String _INFO =
            "Initial Exp levels in arena '{0: arena name}' is {1: levels}.";

    @Localizable static final String _SET =
            "Initial Exp levels in arena '{0: arena name}' changed to {1: levels}.";

    @Override
    public void execute(CommandSender sender, ICommandArguments args) throws CommandException {

        IArena arena = getSelectedArena(sender, AbstractPVCommand.ArenaReturned.ALWAYS);
        if (arena == null)
            return; // finished

        GameStateExtension extension = getExtension(sender, arena, GameStateExtension.class);
        if (extension == null)
            return; // finished

        if (args.isDefaultValue("levels")) {

            int levels = extension.getExpLevels();

            tell(sender, Lang.get(_INFO, arena.getName(), levels));
            return; // finished
        }

        int levels = args.getInteger("levels");

        extension.setExpLevels(levels);

        tellSuccess(sender, Lang.get(_SET, arena.getName(), levels));
    }
}

